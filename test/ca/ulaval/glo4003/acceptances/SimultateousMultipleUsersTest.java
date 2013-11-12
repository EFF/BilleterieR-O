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
    public void ensureLoginWithTwoUserAtTheSameTime() {
        running(testServer(3333, fakeApplication(new TestGlobal())), FIREFOX, new F.Callback<TestBrowser>() {
            @Override
            public void invoke(TestBrowser browser) {
                WebDriver driver1 = browser.getDriver();
                //Create a second driver so we can test in simili parallel
                FirefoxDriver driver2 = new FirefoxDriver();
                LoginPage loginPage1 = new LoginPage(driver1);
                LoginPage loginPage2 = new LoginPage(driver2);

                loginWithDifferentUsersOnBothDrivers(loginPage1, loginPage2);

                assertThat(loginPage1.displayedUsername()).isEqualTo(VALID_USER_EMAIL_1);
                assertThat(loginPage2.displayedUsername()).isEqualTo(VALID_USER_EMAIL_2);

                closeDriver(driver2);
            }
        });
    }

    @Test
    public void ensureDifferentSessionsHaveSameNumberOfTicketForCategory() {
        running(testServer(3333, fakeApplication(new TestGlobal())), FIREFOX, new F.Callback<TestBrowser>() {
            @Override
            public void invoke(TestBrowser browser) {
                WebDriver driver1 = browser.getDriver();
                //Create a second driver so we can test in simili parallel
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
                //Create a second driver so we can test in simili parallel
                FirefoxDriver driver2 = new FirefoxDriver();
                EventPage eventPage1 = new EventPage(driver1, EVENT_INDEX);
                EventPage eventPage2 = new EventPage(driver2, EVENT_INDEX);

                loginWithDifferentUsersOnBothDrivers(loginPage1, loginPAge2);
                goToEventPages(eventPage1, eventPage2);
                addTicketsToCarts(eventPage1, eventPage2);
                checkoutBoth(cartPage1, cartPage2);

                assertThat(eventPage1.getTicketNumberForCategory(1)).isEqualTo(eventPage2.getTicketNumberForCategory(1));

                closeDriver(driver2);
            }
        });
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

    private void EnsureAbleToBuyTicketsWithBothDrivers(WebDriver driver1, FirefoxDriver driver2) {
        CartPage cartPage1 = new CartPage(driver1);
        CartPage cartPage2 = new CartPage(driver2);
        cartPage1.go();
        cartPage2.go();
        cartPage1.isAt();
        cartPage2.isAt();


        cartPage1.checkout();
        cartPage2.checkout();
    }

    private void goToEventPages(EventPage eventPage1, EventPage eventPage2) {
        eventPage1.go();
        eventPage2.go();
        eventPage1.isAt();
        eventPage2.isAt();
    }
}
