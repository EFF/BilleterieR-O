package ca.ulaval.glo4003.acceptances;

import ca.ulaval.glo4003.TestGlobal;
import ca.ulaval.glo4003.acceptances.pages.CartPage;
import ca.ulaval.glo4003.acceptances.pages.EventPage;
import ca.ulaval.glo4003.acceptances.pages.LoginPage;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import play.libs.F;
import play.test.TestBrowser;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.*;

public class SimultateousMultipleUsersTest {

    private static final int EVENT_INDEX = 1;
    private static final String VALID_USER_EMAIL_1 = "user1@example.com";
    private static final String VALID_USER_EMAIL_2 = "user2@example.com";
    private static final String PASSWORD = "secret";
    private static final int TICKET_CATEGORY_ID = 1;
    private static final int TICKET_QUANTITY = 1;

    @Test
    public void ensureLoginWithTwoUsersAtTheSameTime() {
        running(testServer(3333, fakeApplication(new TestGlobal())), FIREFOX, new F.Callback<TestBrowser>() {
            @Override
            public void invoke(TestBrowser browser) {
                WebDriver driver1 = browser.getDriver();
                FirefoxDriver driver2 = new FirefoxDriver();

                LoginPage loginPage1 = new LoginPage(driver1);
                LoginPage loginPage2 = new LoginPage(driver2);

                loginWithDifferentUsersOnBothDrivers(loginPage1, loginPage2);

                assertThat(loginPage1.getDisplayedUsername()).isEqualTo(VALID_USER_EMAIL_1);
                assertThat(loginPage2.getDisplayedUsername()).isEqualTo(VALID_USER_EMAIL_2);

                closeDriver(driver2);
            }
        });
    }

    @Test
    public void ensureDifferentSessionsHaveSameNumberOfTicketsForCategory() {
        running(testServer(3333, fakeApplication(new TestGlobal())), FIREFOX, new F.Callback<TestBrowser>() {
            @Override
            public void invoke(TestBrowser browser) {
                WebDriver driver1 = browser.getDriver();
                FirefoxDriver driver2 = new FirefoxDriver();

                EventPage eventPage1 = new EventPage(driver1, EVENT_INDEX);
                EventPage eventPage2 = new EventPage(driver2, EVENT_INDEX);

                goToEventPages(eventPage1, eventPage2);

                assertThat(eventPage1.getTicketNumberForCategory(1)).isEqualTo(eventPage2.getTicketNumberForCategory(1));

                closeDriver(driver2);
            }
        });
    }

    @Test
    public void ensureTwoUsersCanBuyTicketsAtTheSameTime() {
        running(testServer(3333, fakeApplication(new TestGlobal())), FIREFOX, new F.Callback<TestBrowser>() {
            @Override
            public void invoke(TestBrowser browser) {
                WebDriver driver1 = browser.getDriver();
                FirefoxDriver driver2 = new FirefoxDriver();

                LoginPage loginPage1 = new LoginPage(driver1);
                LoginPage loginPage2 = new LoginPage(driver2);
                EventPage eventPage1 = new EventPage(driver1, EVENT_INDEX);
                EventPage eventPage2 = new EventPage(driver2, EVENT_INDEX);
                CartPage cartPage1 = new CartPage(driver1);
                CartPage cartPage2 = new CartPage(driver2);
                loginWithDifferentUsersOnBothDrivers(loginPage1, loginPage2);
                goToEventPages(eventPage1, eventPage2);
                int initialNumberOfTicketsInCategory = eventPage1.getTicketNumberForCategory(TICKET_CATEGORY_ID);

                addTicketsToCartOnBothDrivers(eventPage1, eventPage2);
                goToCartPages(cartPage1, cartPage2);
                checkoutBoth(cartPage1, cartPage2);
                cartPage1.confirm(driver1);
                cartPage2.confirm(driver2);
                goToEventPages(eventPage1, eventPage2);

                assertThat(eventPage1.getTicketNumberForCategory(TICKET_CATEGORY_ID)).isEqualTo(eventPage2.getTicketNumberForCategory(TICKET_CATEGORY_ID));
                assertThat(eventPage1.getTicketNumberForCategory(TICKET_CATEGORY_ID)).isEqualTo(initialNumberOfTicketsInCategory - 2*TICKET_QUANTITY);

                closeDriver(driver2);
            }
        });
    }

    private void goToCartPages(CartPage cartPage1, CartPage cartPage2) {
        cartPage1.go();
        cartPage2.go();
        cartPage1.isAt();
        cartPage2.isAt();
    }

    private void checkoutBoth(CartPage cartPage1, CartPage cartPage2) {
        cartPage1.payWithCreditCard();
        cartPage2.payWithCreditCard();
    }

    private void addTicketsToCartOnBothDrivers(EventPage eventPage1, EventPage eventPage2) {
        eventPage1.addTicketsToCartForCategory(TICKET_CATEGORY_ID, TICKET_QUANTITY);
        eventPage2.addTicketsToCartForCategory(TICKET_CATEGORY_ID, TICKET_QUANTITY);
    }

    private void closeDriver(FirefoxDriver driver) {
        driver.close();
    }

    private void loginWithDifferentUsersOnBothDrivers(LoginPage loginPage1, LoginPage loginPage2) {
        loginPage1.go();
        loginPage2.go();
        loginPage1.isAt();
        loginPage2.isAt();
        loginPage1.performLogin(VALID_USER_EMAIL_1, PASSWORD);
        loginPage2.performLogin(VALID_USER_EMAIL_2, PASSWORD);
    }

    private void goToEventPages(EventPage eventPage1, EventPage eventPage2) {
        eventPage1.go();
        eventPage2.go();
        eventPage1.isAt();
        eventPage2.isAt();
        eventPage1.waitUntilCategoriesHasSize(2);
        eventPage2.waitUntilCategoriesHasSize(2);
    }
}
