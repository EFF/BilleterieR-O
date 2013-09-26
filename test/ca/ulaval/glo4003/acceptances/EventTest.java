package ca.ulaval.glo4003.acceptances;

import ca.ulaval.glo4003.TestGlobal;
import ca.ulaval.glo4003.acceptances.pages.EventPage;
import org.junit.Test;
import play.libs.F;
import play.test.TestBrowser;

import static org.junit.Assert.assertTrue;
import static play.test.Helpers.*;


public class EventTest {

    @Test
    public void returnAnEventWithPricesAndNumberOfTicketsPerCategory() {
        running(testServer(3333, fakeApplication(new TestGlobal())), FIREFOX, new F.Callback<TestBrowser>() {
            public void invoke(TestBrowser browser) {
                EventPage eventPage = new EventPage(browser.getDriver(), 1);
                eventPage.go();
                eventPage.isAt();

                eventPage.waitUntilCategoriesHasSize(2);

                assertTrue(eventPage.categoryHas(0, "12$", 120));
                assertTrue(eventPage.categoryHas(1, "8$", 1200));
            }
        });
    }
}
