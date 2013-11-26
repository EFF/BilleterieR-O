package ca.ulaval.glo4003.acceptances;

import ca.ulaval.glo4003.TestGlobal;
import ca.ulaval.glo4003.acceptances.pages.EventPage;
import ca.ulaval.glo4003.acceptances.pages.TicketPage;
import org.fluentlenium.adapter.FluentTest;
import org.junit.Test;
import play.libs.F;
import play.test.TestBrowser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static play.test.Helpers.*;

public class TicketTest  extends FluentTest {

    @Test
    public void requiredInfosAreDisplayed() {
        running(testServer(3333, fakeApplication(new TestGlobal())), FIREFOX, new F.Callback<TestBrowser>() {
            public void invoke(TestBrowser browser) {
                TicketPage ticketPage = new TicketPage(browser.getDriver(), 1450, 2);
                ticketPage.go();
                ticketPage.isAt();

                assertTrue(ticketPage.requiredInfoAreDisplayed());
            }
        });
    }

    @Test
    public void clickOnEventDetailsShowEventPage() {
        running(testServer(3333, fakeApplication(new TestGlobal())), FIREFOX, new F.Callback<TestBrowser>() {
            public void invoke(TestBrowser browser) {
                EventPage eventPage = new EventPage(browser.getDriver(), 2);
                TicketPage ticketPage = new TicketPage(browser.getDriver(), 1445, 2);
                ticketPage.go();
                ticketPage.isAt();

                ticketPage.clickEventDetailsButton();
                eventPage.isAt();
                assertEquals(eventPage.getUrl(), browser.url());
            }
        });
    }
}
