package ca.ulaval.glo4003.acceptances;

import ca.ulaval.glo4003.TestGlobal;
import ca.ulaval.glo4003.acceptances.pages.CartPage;
import ca.ulaval.glo4003.acceptances.pages.EventPage;
import org.junit.Test;
import play.libs.F;
import play.test.TestBrowser;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.*;

public class CartTest {

    private static final int FIRST_ITEM_INDEX = 0;

    @Test
    public void putManyItemsFromManyEventsIntoTheCartAndRemoveThem() {
        running(testServer(3333, fakeApplication(new TestGlobal())), FIREFOX, new F.Callback<TestBrowser>() {
            @Override
            public void invoke(TestBrowser browser) {
                EventPage eventPage1 = new EventPage(browser.getDriver(), 1);
                EventPage eventPage2 = new EventPage(browser.getDriver(), 2);
                CartPage cartPage = new CartPage(browser.getDriver());

                eventPage1.go();
                eventPage1.isAt();
                eventPage1.waitUntilCategoriesHasSize(2);
                // Buy two tickets from events/1, category 0
                eventPage1.buyTicketsForCategory(0, 2);
                assertThat(eventPage1.getCartSize()).isEqualTo(2);
                // Buy five tickets from events/1, category 1
                eventPage1.buyTicketsForCategory(1, 5);
                assertThat(eventPage1.getCartSize()).isEqualTo(7);
                // Buy one ticket from event #2, category 0
                eventPage2.go();
                eventPage2.isAt();
                eventPage2.waitUntilCategoriesHasSize(2);
                eventPage2.buyTicketsForCategory(0, 1);
                assertThat(eventPage2.getCartSize()).isEqualTo(8);
                // Check cart (3 items)
                cartPage.go();
                cartPage.isAt();
                cartPage.waitUntilItemsHasSize(3);

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
}
