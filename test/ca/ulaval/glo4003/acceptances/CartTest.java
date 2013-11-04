package ca.ulaval.glo4003.acceptances;

import ca.ulaval.glo4003.TestGlobal;
import ca.ulaval.glo4003.acceptances.pages.CartPage;
import ca.ulaval.glo4003.acceptances.pages.EventPage;
import ca.ulaval.glo4003.acceptances.pages.LoginPage;
import ca.ulaval.glo4003.acceptances.pages.PaymentResultPage;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import play.libs.F;
import play.test.TestBrowser;

import static org.junit.Assert.assertEquals;
import static play.test.Helpers.*;

public class CartTest {

    private static final String EMAIL = "user@example.com";
    private static final String PASSWORD = "secret";
    private static final int FIRST_ITEM_INDEX = 0;
    private static final int FIRST_EVENT = 1;
    private static final String A_CVV = "123";
    private static final String A_CARD_NUMBER = "12345678901234";
    private static final String A_MONTH = "01";
    private static final String A_YEAR = "2015";
    private static final String A_CARD_TYPE = "Vasi";

    @Test
    public void putManyItemsFromManyEventsIntoTheCartAndRemoveThem() {
        running(testServer(3333, fakeApplication(new TestGlobal())), FIREFOX, new F.Callback<TestBrowser>() {
            @Override
            public void invoke(TestBrowser browser) {
                EventPage eventPage1 = new EventPage(browser.getDriver(), FIRST_EVENT);
                EventPage eventPage2 = new EventPage(browser.getDriver(), FIRST_EVENT + 1);
                CartPage cartPage = new CartPage(browser.getDriver());

                goToEventPage(eventPage1);
                // Buy two tickets from events/1, category 0
                eventPage1.buyTicketsForCategory(0, 2);
                assertEquals(2, eventPage1.getCartSize());
                // Buy five tickets from events/1, category 1
                eventPage1.buyTicketsForCategory(1, 5);
                assertEquals(7, eventPage1.getCartSize());
                // Buy one ticket from event #2, category 0
                goToEventPage(eventPage2);
                eventPage2.buyTicketsForCategory(0, 1);
                assertEquals(8, eventPage2.getCartSize());

                goToCartPage(cartPage, 3);

                // Remove one item
                cartPage.removeItem(FIRST_ITEM_INDEX);
                cartPage.waitUntilItemsHasSize(2);
                assertEquals(6, cartPage.getCartSize());

                // Remove all items
                cartPage.removeAllItems();
                cartPage.waitUntilItemsHasSize(0);
                assertEquals(0, eventPage1.getCartSize());
            }
        });
    }

    @Test
    public void putTheSameItemManyTimesIntoTheCartShouldOnlyCreateOneItemInCart() {
        running(testServer(3333, fakeApplication(new TestGlobal())), FIREFOX, new F.Callback<TestBrowser>() {
            @Override
            public void invoke(TestBrowser browser) {
                EventPage eventPage = new EventPage(browser.getDriver(), FIRST_EVENT);
                CartPage cartPage = new CartPage(browser.getDriver());

                goToEventPage(eventPage);

                // Buy one ticket from events #1, category 0
                eventPage.buyTicketsForCategory(0, 1);

                // Buy another two tickets for the same category and the same event
                eventPage.buyTicketsForCategory(0, 2);

                // Should have one item with quantity equals to 3
                goToCartPage(cartPage, 1);
                cartPage.itemHas(0, 3);
            }
        });
    }

    @Test
    public void buyASelectedTicketThenCountDecrements() {
        running(testServer(3333, fakeApplication(new TestGlobal())), FIREFOX, new F.Callback<TestBrowser>() {
            @Override
            public void invoke(TestBrowser browser) {
                LoginPage loginPage = new LoginPage(browser.getDriver());
                EventPage eventPage1 = new EventPage(browser.getDriver(), FIRST_EVENT);
                CartPage cartPage = new CartPage(browser.getDriver());
                PaymentResultPage resultPage = new PaymentResultPage(browser.getDriver());

                goToLoginPage(loginPage);

                loginPage.performLogin(EMAIL, PASSWORD);
                loginPage.waitUntilLoginIsDone();

                goToEventPage(eventPage1);

                int firstCategoryTicketCount = eventPage1.getTicketNumberForCategory(0);

                eventPage1.buyTicketsForCategory(0, 1);
                eventPage1.buyTicketsForCategory(1, 1);
                assertEquals(2, eventPage1.getCartSize());

                goToCartPage(cartPage, 2);

                cartPage.selectItem(0);
                payCartWithCard(cartPage, A_CARD_TYPE, browser.getDriver());
                cartPage.confirm(browser.getDriver());
                resultPage.isAt();

                goToEventPage(eventPage1);
                assertEquals(firstCategoryTicketCount - 1, eventPage1.getTicketNumberForCategory(0));
            }
        });
    }

    @Test
    public void buyMultipleSelectedTicket() {
        running(testServer(3333, fakeApplication(new TestGlobal())), FIREFOX, new F.Callback<TestBrowser>() {
            @Override
            public void invoke(TestBrowser browser) {
                EventPage eventPage1 = new EventPage(browser.getDriver(), FIRST_EVENT);
                CartPage cartPage = new CartPage(browser.getDriver());
                LoginPage loginPage = new LoginPage(browser.getDriver());
                PaymentResultPage resultPage = new PaymentResultPage(browser.getDriver());

                goToLoginPage(loginPage);

                loginPage.performLogin(EMAIL, PASSWORD);
                loginPage.waitUntilLoginIsDone();

                goToEventPage(eventPage1);

                int cart1TicketNumber = eventPage1.getTicketNumberForCategory(0);
                int cart2TicketNumber = eventPage1.getTicketNumberForCategory(1);

                eventPage1.buyTicketsForCategory(0, 1);
                eventPage1.buyTicketsForCategory(1, 1);
                assertEquals(2, eventPage1.getCartSize());

                goToCartPage(cartPage, 2);

                payCartWithCard(cartPage, A_CARD_TYPE, browser.getDriver());
                cartPage.confirm(browser.getDriver());
                resultPage.isAt();

                goToEventPage(eventPage1);
                assertEquals(cart1TicketNumber - 1, eventPage1.getTicketNumberForCategory(0));
                assertEquals(cart2TicketNumber - 1, eventPage1.getTicketNumberForCategory(1));
            }
        });
    }

    @Test
    public void confirmPerformsTheTransaction() {
        running(testServer(3333, fakeApplication(new TestGlobal())), FIREFOX, new F.Callback<TestBrowser>() {
            @Override
            public void invoke(TestBrowser browser) {
                LoginPage loginPage = new LoginPage(browser.getDriver());
                EventPage eventPage1 = new EventPage(browser.getDriver(), FIRST_EVENT);
                CartPage cartPage = new CartPage(browser.getDriver());
                PaymentResultPage resultPage = new PaymentResultPage(browser.getDriver());

                goToLoginPage(loginPage);

                loginPage.performLogin(EMAIL, PASSWORD);

                goToEventPage(eventPage1);
                eventPage1.buyTicketsForCategory(0, 1);

                goToCartPage(cartPage, 1);
                payCartWithCard(cartPage, A_CARD_TYPE, browser.getDriver());
                cartPage.confirm(browser.getDriver());
                assertEquals(0, resultPage.getCartSize());
            }
        });
    }

    @Test
    public void declineDoesntPerformTheTransaction() {
        running(testServer(3333, fakeApplication(new TestGlobal())), FIREFOX, new F.Callback<TestBrowser>() {
            @Override
            public void invoke(TestBrowser browser) {
                LoginPage loginPage = new LoginPage(browser.getDriver());
                EventPage eventPage1 = new EventPage(browser.getDriver(), FIRST_EVENT);
                CartPage cartPage = new CartPage(browser.getDriver());
                PaymentResultPage resultPage = new PaymentResultPage(browser.getDriver());

                goToLoginPage(loginPage);

                loginPage.performLogin(EMAIL, PASSWORD);

                goToEventPage(eventPage1);
                eventPage1.buyTicketsForCategory(0, 1);

                goToCartPage(cartPage, 1);
                payCartWithCard(cartPage, A_CARD_TYPE, browser.getDriver());
                cartPage.dismiss(browser.getDriver());
                assertEquals(1, resultPage.getCartSize());
            }
        });
    }

    @Test
    public void connectionRequiredMessage(){
        running(testServer(3333, fakeApplication(new TestGlobal())), FIREFOX, new F.Callback<TestBrowser>() {
            @Override
            public void invoke(TestBrowser browser) {
                EventPage eventPage1 = new EventPage(browser.getDriver(), FIRST_EVENT);
                CartPage cartPage = new CartPage(browser.getDriver());

                goToEventPage(eventPage1);

                eventPage1.buyTicketsForCategory(0, 1);

                goToCartPage(cartPage, 1);

                payCartWithCard(cartPage, A_CARD_TYPE, browser.getDriver());

                String message = cartPage.waitAndGetAlert().getText();
                String expectedMessage = "Vous devez vous connecter avant de procéder au paiement";
                assertEquals(expectedMessage, message);
            }
        });
    }

    private void payCartWithCard(CartPage cartPage, String cardName, WebDriver driver) {
        cartPage.selectComboLabel(cardName);
        cartPage.fillCreditCardNumber(A_CARD_NUMBER);
        cartPage.fillCvv(A_CVV);
        cartPage.selectExpirationMonth(A_MONTH);
        cartPage.selectExpirationYear(A_YEAR);
        cartPage.checkout();
    }

    private void goToCartPage(CartPage cartPage, int itemSize) {
        cartPage.go();
        cartPage.isAt();
        cartPage.waitUntilItemsHasSize(itemSize);
    }

    private void goToEventPage(EventPage eventPage) {
        eventPage.go();
        eventPage.isAt();
        eventPage.waitUntilCategoriesHasSize(2);
    }

    private void goToLoginPage(LoginPage loginPage) {
        loginPage.go();
        loginPage.isAt();
    }
}
