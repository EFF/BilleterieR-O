package ca.ulaval.glo4003.acceptances;

import ca.ulaval.glo4003.TestGlobal;
import ca.ulaval.glo4003.acceptances.pages.*;
import org.fluentlenium.adapter.FluentTest;
import org.junit.Test;
import play.libs.F;
import play.test.TestBrowser;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static play.test.Helpers.*;

public class CartTest extends FluentTest {

    private final int FIRST_CART_ITEM_INDEX = 0;
    private final int VALID_TICKET_QUANTITY = 10;
    private final int EXCEEDED_TICKET_QUANTITY = 30000;
    private final String EMAIL = "user1@example.com";
    private final String PASSWORD = "secret";
    private final int FIRST_ITEM_INDEX = 0;
    private final int FIRST_EVENT_ID = 1;
    private final int SECOND_EVENT_ID = 2;
    private final int FIRST_CATEGORY_INDEX = 0;
    private final int SECOND_CATEGORY_INDEX = 1;

    @Test
    public void putManyItemsFromManyEventsIntoTheCartAndRemoveThem() {
        running(testServer(3333, fakeApplication(new TestGlobal())), FIREFOX, new F.Callback<TestBrowser>() {
            @Override
            public void invoke(TestBrowser browser) {
                EventPage eventPage1 = new EventPage(browser.getDriver(), FIRST_EVENT_ID);
                EventPage eventPage2 = new EventPage(browser.getDriver(), SECOND_EVENT_ID);
                CartPage cartPage = new CartPage(browser.getDriver());

                goToEventPage(eventPage1);

                eventPage1.addGeneralAdmissionsToCartForCategory(FIRST_CATEGORY_INDEX, 2);
                eventPage1.waitUntilCartHasSize(2);
                assertEquals(2, eventPage1.getCartSize());

                eventPage1.addGeneralAdmissionsToCartForCategory(SECOND_CATEGORY_INDEX, 5);
                eventPage1.waitUntilCartHasSize(7);
                assertEquals(7, eventPage1.getCartSize());

                goToEventPage(eventPage2);
                eventPage2.addGeneralAdmissionsToCartForCategory(FIRST_CATEGORY_INDEX, 1);
                eventPage2.waitUntilCartHasSize(8);
                assertEquals(8, eventPage2.getCartSize());

                goToCartPage(cartPage, 3);

                cartPage.removeItem(FIRST_ITEM_INDEX);
                cartPage.waitUntilItemsHasSize(2);
                cartPage.waitUntilCartHasSize(6);
                assertEquals(6, cartPage.getCartSize());

                cartPage.removeAllItems();
                cartPage.waitUntilItemsHasSize(0);
                cartPage.waitUntilCartHasSize(0);
                assertEquals(0, eventPage1.getCartSize());
            }
        });
    }

    @Test
    public void putTheSameItemManyTimesIntoTheCartShouldOnlyCreateOneItemInCart() {
        running(testServer(3333, fakeApplication(new TestGlobal())), FIREFOX, new F.Callback<TestBrowser>() {
            @Override
            public void invoke(TestBrowser browser) {
                EventPage eventPage = new EventPage(browser.getDriver(), FIRST_EVENT_ID);
                CartPage cartPage = new CartPage(browser.getDriver());
                goToEventPage(eventPage);

                eventPage.addGeneralAdmissionsToCartForCategory(FIRST_CATEGORY_INDEX, 1);
                eventPage.addGeneralAdmissionsToCartForCategory(FIRST_CATEGORY_INDEX, 2);
                goToCartPage(cartPage, 1);

                // Should have one item with quantity equals to 3
                assertThat(cartPage.getNumberOfItems()).isEqualTo(1);
                assertThat(cartPage.getQuantityForItem(FIRST_CART_ITEM_INDEX)).isEqualTo(3);
            }
        });
    }

    @Test
    public void buyASelectedTicketThenCountDecrements() {
        running(testServer(3333, fakeApplication(new TestGlobal())), FIREFOX, new F.Callback<TestBrowser>() {
            @Override
            public void invoke(TestBrowser browser) {
                LoginPage loginPage = new LoginPage(browser.getDriver());
                EventPage eventPage1 = new EventPage(browser.getDriver(), FIRST_EVENT_ID);
                CartPage cartPage = new CartPage(browser.getDriver());
                PaymentResultPage resultPage = new PaymentResultPage(browser.getDriver());

                goToLoginPage(loginPage);

                loginPage.performLogin(EMAIL, PASSWORD);

                goToEventPage(eventPage1);

                int firstCategoryTicketCount = eventPage1.getTicketNumberForCategory(FIRST_CATEGORY_INDEX);

                eventPage1.addGeneralAdmissionsToCartForCategory(FIRST_CATEGORY_INDEX, 2);
                eventPage1.waitUntilCartHasSize(2);
                assertEquals(2, eventPage1.getCartSize());

                goToCartPage(cartPage, 1);

                cartPage.selectItem(FIRST_CART_ITEM_INDEX);
                cartPage.payWithCreditCard();
                cartPage.confirm(browser.getDriver());
                resultPage.isAt();

                goToEventPage(eventPage1);
                assertEquals(firstCategoryTicketCount - 2, eventPage1.getTicketNumberForCategory(FIRST_CATEGORY_INDEX));
            }
        });
    }

    @Test
    public void buyMultipleSelectedTicket() {
        running(testServer(3333, fakeApplication(new TestGlobal())), FIREFOX, new F.Callback<TestBrowser>() {
            @Override
            public void invoke(TestBrowser browser) {
                EventPage eventPage1 = new EventPage(browser.getDriver(), FIRST_EVENT_ID);
                CartPage cartPage = new CartPage(browser.getDriver());
                LoginPage loginPage = new LoginPage(browser.getDriver());
                PaymentResultPage resultPage = new PaymentResultPage(browser.getDriver());

                goToLoginPage(loginPage);

                loginPage.performLogin(EMAIL, PASSWORD);

                goToEventPage(eventPage1);

                int cart1TicketNumber = eventPage1.getTicketNumberForCategory(0);
                int cart2TicketNumber = eventPage1.getTicketNumberForCategory(1);

                eventPage1.addGeneralAdmissionsToCartForCategory(FIRST_CATEGORY_INDEX, 1);
                eventPage1.addGeneralAdmissionsToCartForCategory(SECOND_CATEGORY_INDEX, 1);
                eventPage1.waitUntilCartHasSize(2);
                assertEquals(2, eventPage1.getCartSize());

                goToCartPage(cartPage, 2);

                cartPage.payWithCreditCard();
                cartPage.confirm(browser.getDriver());
                resultPage.isAt();

                goToEventPage(eventPage1);
                assertEquals(cart1TicketNumber - 1, eventPage1.getTicketNumberForCategory(FIRST_CATEGORY_INDEX));
                assertEquals(cart2TicketNumber - 1, eventPage1.getTicketNumberForCategory(SECOND_CATEGORY_INDEX));
            }
        });
    }

    @Test
    public void confirmPerformsTheTransaction() {
        running(testServer(3333, fakeApplication(new TestGlobal())), FIREFOX, new F.Callback<TestBrowser>() {
            @Override
            public void invoke(TestBrowser browser) {
                LoginPage loginPage = new LoginPage(browser.getDriver());
                EventPage eventPage1 = new EventPage(browser.getDriver(), FIRST_EVENT_ID);
                CartPage cartPage = new CartPage(browser.getDriver());
                PaymentResultPage resultPage = new PaymentResultPage(browser.getDriver());

                goToLoginPage(loginPage);

                loginPage.performLogin(EMAIL, PASSWORD);

                goToEventPage(eventPage1);
                eventPage1.addGeneralAdmissionsToCartForCategory(FIRST_CATEGORY_INDEX, 1);

                goToCartPage(cartPage, 1);
                cartPage.payWithCreditCard();
                cartPage.confirm(browser.getDriver());
                cartPage.waitUntilCartHasSize(0);
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
                EventPage eventPage1 = new EventPage(browser.getDriver(), FIRST_EVENT_ID);
                CartPage cartPage = new CartPage(browser.getDriver());
                PaymentResultPage resultPage = new PaymentResultPage(browser.getDriver());

                goToLoginPage(loginPage);

                loginPage.performLogin(EMAIL, PASSWORD);

                goToEventPage(eventPage1);
                eventPage1.addGeneralAdmissionsToCartForCategory(FIRST_CATEGORY_INDEX, 1);

                goToCartPage(cartPage, 1);
                cartPage.payWithCreditCard();
                cartPage.dismiss(browser.getDriver());
                resultPage.waitUntilCartHasSize(1);
                assertEquals(1, resultPage.getCartSize());
            }
        });
    }

    @Test
    public void connectionRequiredMessage() {
        running(testServer(3333, fakeApplication(new TestGlobal())), FIREFOX, new F.Callback<TestBrowser>() {
            @Override
            public void invoke(TestBrowser browser) {
                EventPage eventPage1 = new EventPage(browser.getDriver(), FIRST_EVENT_ID);
                CartPage cartPage = new CartPage(browser.getDriver());

                goToEventPage(eventPage1);
                eventPage1.addGeneralAdmissionsToCartForCategory(FIRST_CATEGORY_INDEX, 1);

                goToCartPage(cartPage, 1);
                cartPage.payWithCreditCard();
                cartPage.waitForInfoMessageToDisplay();
            }
        });
    }

    @Test
    public void modifyNumberOfTicketsIsPossible() {
        running(testServer(3333, fakeApplication(new TestGlobal())), FIREFOX, new F.Callback<TestBrowser>() {
            @Override
            public void invoke(TestBrowser browser) {
                EventPage eventPage1 = new EventPage(browser.getDriver(), FIRST_EVENT_ID);
                CartPage cartPage = new CartPage(browser.getDriver());
                goToEventPage(eventPage1);

                eventPage1.addGeneralAdmissionsToCartForCategory(FIRST_CATEGORY_INDEX, 1);
                goToCartPage(cartPage, 1);
                cartPage.modifyNumberOfTicketsForItem(FIRST_CART_ITEM_INDEX, VALID_TICKET_QUANTITY);

                cartPage.waitUntilItemsHasSize(1);
                assertEquals(cartPage.getQuantityForItem(FIRST_CART_ITEM_INDEX), VALID_TICKET_QUANTITY);
            }
        });
    }

    @Test
    public void aWarningMessageIsDisplayedWhenMaximumNumberOfTicketsIsExeeded() {
        running(testServer(3333, fakeApplication(new TestGlobal())), FIREFOX, new F.Callback<TestBrowser>() {
            @Override
            public void invoke(TestBrowser browser) {
                EventPage eventPage1 = new EventPage(browser.getDriver(), FIRST_EVENT_ID);
                CartPage cartPage = new CartPage(browser.getDriver());
                goToEventPage(eventPage1);

                eventPage1.addGeneralAdmissionsToCartForCategory(FIRST_CATEGORY_INDEX, 1);
                goToCartPage(cartPage, 1);
                cartPage.modifyNumberOfTicketsForItem(FIRST_CART_ITEM_INDEX, EXCEEDED_TICKET_QUANTITY);

                assertTrue(cartPage.isWarningMessageDisplayed());
            }
        });
    }

    @Test
    public void notPossibleToAddMoreThanMaximumNumberOfTickets() {
        running(testServer(3333, fakeApplication(new TestGlobal())), FIREFOX, new F.Callback<TestBrowser>() {
            @Override
            public void invoke(TestBrowser browser) {
                EventPage eventPage1 = new EventPage(browser.getDriver(), FIRST_EVENT_ID);
                CartPage cartPage = new CartPage(browser.getDriver());
                goToEventPage(eventPage1);

                eventPage1.addGeneralAdmissionsToCartForCategory(FIRST_CATEGORY_INDEX, 1);
                goToCartPage(cartPage, 1);
                cartPage.modifyNumberOfTicketsForItem(FIRST_CART_ITEM_INDEX, EXCEEDED_TICKET_QUANTITY);
                eventPage1.waitForErrorMessageToDisplay();

                assertThat(cartPage.getQuantityForItem(FIRST_CART_ITEM_INDEX)).isNotEqualTo(EXCEEDED_TICKET_QUANTITY);
            }
        });
    }

    @Test
    public void ensureThatAQuantityOfZeroRemoveTheItemFromTheCart() {
        running(testServer(3333, fakeApplication(new TestGlobal())), FIREFOX, new F.Callback<TestBrowser>() {
            @Override
            public void invoke(TestBrowser browser) {
                EventPage eventPage1 = new EventPage(browser.getDriver(), FIRST_EVENT_ID);
                CartPage cartPage = new CartPage(browser.getDriver());
                goToEventPage(eventPage1);

                eventPage1.addGeneralAdmissionsToCartForCategory(FIRST_CATEGORY_INDEX, 1);
                goToCartPage(cartPage, 1);
                cartPage.modifyNumberOfTicketsForItem(FIRST_CART_ITEM_INDEX, 0);
                cartPage.waitUntilItemsHasSize(0);

                assertThat(cartPage.getNumberOfItems()).isEqualTo(0);
            }
        });
    }

    @Test
    public void ensureThatTheCartIsEmptiedOnLogout() {
        running(testServer(3333, fakeApplication(new TestGlobal())), FIREFOX, new F.Callback<TestBrowser>() {
            @Override
            public void invoke(TestBrowser browser) {
                EventPage eventPage = new EventPage(browser.getDriver(), FIRST_EVENT_ID);
                LoginPage loginPage = new LoginPage(browser.getDriver());

                goToLoginPage(loginPage);
                loginPage.performLogin(EMAIL, PASSWORD);

                goToEventPage(eventPage);
                eventPage.addGeneralAdmissionsToCartForCategory(FIRST_CATEGORY_INDEX, 1);
                eventPage.waitUntilCartHasSize(1);

                eventPage.clickLogoutButton();
                loginPage.isLoggedOut();
                eventPage.waitUntilCartHasSize(0);
            }
        });
    }

    private void goToCartPage(CartPage cartPage, int itemSize) {
        cartPage.go();
        cartPage.isAt();
        cartPage.waitUntilItemsHasSize(itemSize);
    }

    private void goToEventPage(EventPage eventPage) {
        eventPage.go();
        eventPage.isAt();
    }

    private void goToLoginPage(LoginPage loginPage) {
        loginPage.go();
        loginPage.isAt();
    }
}
