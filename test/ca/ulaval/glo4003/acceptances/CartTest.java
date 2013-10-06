package ca.ulaval.glo4003.acceptances;

import ca.ulaval.glo4003.TestGlobal;
import ca.ulaval.glo4003.acceptances.pages.CartPage;
import ca.ulaval.glo4003.acceptances.pages.EventPage;
import ca.ulaval.glo4003.acceptances.pages.PaymentResultPage;
import org.junit.Test;
import play.libs.F;
import play.test.TestBrowser;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.*;

public class CartTest {

    private static final int FIRST_ITEM_INDEX = 0;
    private static final int FIRST_EVENT = 1;

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
                assertThat(eventPage1.getCartSize()).isEqualTo(2);
                // Buy five tickets from events/1, category 1
                eventPage1.buyTicketsForCategory(1, 5);
                assertThat(eventPage1.getCartSize()).isEqualTo(7);
                // Buy one ticket from event #2, category 0
                goToEventPage(eventPage2);
                eventPage2.buyTicketsForCategory(0, 1);
                assertThat(eventPage2.getCartSize()).isEqualTo(8);

                goToCartPage(cartPage, 3);

                // Remove one item
                cartPage.removeItem(FIRST_ITEM_INDEX);
                cartPage.waitUntilItemsHasSize(2);
                assertThat(cartPage.getCartSize()).isEqualTo(6);

                // Remove all items
                cartPage.removeAllItems();
                cartPage.waitUntilItemsHasSize(0);
                assertThat(eventPage1.getCartSize()).isEqualTo(0);
            }
        });
    }

    @Test
    public void buyASelectedTicketThenCountDecrements() {
        running(testServer(3333, fakeApplication(new TestGlobal())), FIREFOX, new F.Callback<TestBrowser>() {
            @Override
            public void invoke(TestBrowser browser) {
                EventPage eventPage1 = new EventPage(browser.getDriver(), FIRST_EVENT);
                CartPage cartPage = new CartPage(browser.getDriver());
                PaymentResultPage resultPage = new PaymentResultPage(browser.getDriver());

                goToEventPage(eventPage1);

                int cat1TicketNumber = eventPage1.getTicketNumberForCategory(0);

                eventPage1.buyTicketsForCategory(0, 1);
                eventPage1.buyTicketsForCategory(1, 1);
                assertThat(eventPage1.getCartSize()).isEqualTo(2);

                goToCartPage(cartPage, 2);

                cartPage.selectItem(0);
                payCartWithCard(cartPage, "Vasi");
                resultPage.isAt();

                goToEventPage(eventPage1);
                assertThat(eventPage1.getTicketNumberForCategory(0)).isEqualTo(cat1TicketNumber - 1);
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
                PaymentResultPage resultPage = new PaymentResultPage(browser.getDriver());

                goToEventPage(eventPage1);

                int cat1TicketNumber = eventPage1.getTicketNumberForCategory(0);
                int cat2TicketNumber = eventPage1.getTicketNumberForCategory(1);

                eventPage1.buyTicketsForCategory(0, 1);
                eventPage1.buyTicketsForCategory(1, 1);
                assertThat(eventPage1.getCartSize()).isEqualTo(2);

                goToCartPage(cartPage, 2);

                cartPage.selectAllItem();
                payCartWithCard(cartPage, "Vasi");
                resultPage.isAt();

                goToEventPage(eventPage1);
                assertThat(eventPage1.getTicketNumberForCategory(0)).isEqualTo(cat1TicketNumber - 1);
                assertThat(eventPage1.getTicketNumberForCategory(1)).isEqualTo(cat2TicketNumber - 1);
            }
        });
    }

    private void payCartWithCard(CartPage cartPage, String cardName) {
        cartPage.selectComboLabel(cardName);
        cartPage.fillCreditCardNumber();
        cartPage.fillCvv();
        cartPage.selectExpirationMonth("01");
        cartPage.selectExpirationYear("2015");
        cartPage.sendPaymentForm();
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
}
