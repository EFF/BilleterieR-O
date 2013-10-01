package ca.ulaval.glo4003.acceptances;

import ca.ulaval.glo4003.TestGlobal;
import ca.ulaval.glo4003.acceptances.pages.CartPage;
import ca.ulaval.glo4003.acceptances.pages.EventPage;
import org.junit.Test;
import play.libs.F;
import play.test.TestBrowser;

import static play.test.Helpers.*;

public class BuyTest {

    @Test
    public void buyTest() {
        running(testServer(3333, fakeApplication(new TestGlobal())), FIREFOX, new F.Callback<TestBrowser>() {
            @Override
            public void invoke(TestBrowser browser) {
                EventPage eventPage1 = new EventPage(browser.getDriver(), 1);
                EventPage eventPage2 = new EventPage(browser.getDriver(), 2);
                CartPage cartPage = new CartPage(browser.getDriver());

                eventPage1.go();
                eventPage1.isAt();
                eventPage1.waitUntilCategoriesHasSize(2);
                // TODO: Add a visual feedback when you add an item in the cart
                // TODO: Validate # of items in cart menu
                // TODO: Prevent negative or zero quantity
                // Buy two tickets from events/1, category 0
                eventPage1.buyTicketsForCategory(0, 2);
                // Buy five tickets from events/1, category 1
                eventPage1.buyTicketsForCategory(1, 5);
                // Buy one ticket from event #2, category 0
                eventPage2.go();
                eventPage2.isAt();
                eventPage2.buyTicketsForCategory(0, 1);
                // Check cart
                cartPage.go();
                cartPage.isAt();
                cartPage.waitUntilItemsHasSize(3);
            }
        });
    }
}
