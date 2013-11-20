package ca.ulaval.glo4003.acceptances;

import ca.ulaval.glo4003.TestGlobal;
import ca.ulaval.glo4003.acceptances.pages.EventPage;
import ca.ulaval.glo4003.acceptances.pages.TicketPage;
import org.fluentlenium.adapter.FluentTest;
import org.junit.Test;
import play.libs.F;
import play.test.TestBrowser;

import static org.junit.Assert.assertTrue;
import static play.test.Helpers.*;


public class EventTest extends FluentTest {

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

    @Test
    public void selectASeatThenValidateTicketInfos() {
        running(testServer(3333, fakeApplication(new TestGlobal())), FIREFOX, new F.Callback<TestBrowser>() {
            public void invoke(TestBrowser browser) {
                EventPage eventPage = new EventPage(browser.getDriver(), 2);
                TicketPage ticketPage = new TicketPage(browser.getDriver(), -1L);
                String sectionListId = "#sectionList1";
                String ticketListId = "#ticketList1";
                String ticketSeatToSelect = "2";

                eventPage.go();
                eventPage.isAt();

                eventPage.waitUntilCategoriesHasSize(2);

                eventPage.selectClickOnValue(sectionListId, "Niveau 100");
                eventPage.waitUntilSelectIsPopulated(ticketListId);
                eventPage.selectClickOnValue(ticketListId, ticketSeatToSelect);
                Long ticketId = Long.parseLong(eventPage.getSelectOptionValue(ticketListId, ticketSeatToSelect));
                ticketPage.setTicketId(ticketId);

                eventPage.clickOnButton(".btn-details1");
                ticketPage.isAt();
                assertTrue(browser.url().equals(ticketPage.getUrl()));

                assertTrue(ticketPage.isSeat(ticketSeatToSelect));
            }
        });
    }
}
