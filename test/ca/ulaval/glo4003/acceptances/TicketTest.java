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

public class TicketTest extends FluentTest {

    public static final int A_TICKET_TYPE_SEAT_ID = 1450;
    public static final int ANOTHER_TICKET_TYPE_SEAT_ID = 1445;
    public static final int AN_EVENT_ID = 2;

    @Test
    public void requiredInfosAreDisplayed() {
        running(testServer(3333, fakeApplication(new TestGlobal())), FIREFOX, new F.Callback<TestBrowser>() {
            public void invoke(TestBrowser browser) {
                TicketPage ticketPage = new TicketPage(browser.getDriver(), A_TICKET_TYPE_SEAT_ID, AN_EVENT_ID);
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
                EventPage eventPage = new EventPage(browser.getDriver(), AN_EVENT_ID);
                TicketPage ticketPage = new TicketPage(browser.getDriver(), ANOTHER_TICKET_TYPE_SEAT_ID, AN_EVENT_ID);
                ticketPage.go();
                ticketPage.isAt();

                ticketPage.clickEventDetailsButton();
                eventPage.isAt();
                assertEquals(eventPage.getUrl(), browser.url());
            }
        });
    }
}
