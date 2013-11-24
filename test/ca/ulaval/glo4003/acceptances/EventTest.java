package ca.ulaval.glo4003.acceptances;

import ca.ulaval.glo4003.TestGlobal;
import ca.ulaval.glo4003.acceptances.pages.EventPage;
import ca.ulaval.glo4003.acceptances.pages.TicketPage;
import org.fluentlenium.adapter.FluentTest;
import org.junit.Test;
import play.libs.F;
import play.test.TestBrowser;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
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
                TicketPage ticketPage = new TicketPage(browser.getDriver(), -1, 2);
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

                assertEquals(ticketPage.getUrl(), browser.url());
                assertTrue(ticketPage.isSection("Niveau 100"));
                assertTrue(ticketPage.isSeat(ticketSeatToSelect));
            }
        });
    }

    @Test
    public void buyAllSeatTicketThenComboIsEmpty() {
        running(testServer(3333, fakeApplication(new TestGlobal())), FIREFOX, new F.Callback<TestBrowser>() {
            public void invoke(TestBrowser browser) {
                EventPage eventPage = new EventPage(browser.getDriver(), 2);
                String sectionListId = "#sectionList1";
                String ticketListId = "#ticketList1";
                final int DEFAULT_OPTION = 1;  // the "siege disponible" item

                eventPage.go();
                eventPage.isAt();
                eventPage.waitUntilCategoriesHasSize(2);

                eventPage.selectClickOnValue(sectionListId, "Niveau 100");
                eventPage.waitUntilSelectIsPopulated(ticketListId);

                int ticketNumber = eventPage.getTicketNumberForCategory(1);
                int selectSize = eventPage.getSelectSize(ticketListId) - DEFAULT_OPTION;

                for (int i = 0; i < selectSize; i++) {
                    eventPage.waitUntilSelectIsPopulated(ticketListId);
                    eventPage.selectClickOnFirstIndexValue(ticketListId);
                    eventPage.clickOnAddButtonForCategory(1);
                    if (i < (selectSize - 1)) {
                        eventPage.waitUntilCategoriesHasSize(2);
                        eventPage.waitUntilSelectIsPopulated(ticketListId);
                    }
                }
                browser.fluentWait().withTimeout(1, TimeUnit.SECONDS);
                assertEquals(DEFAULT_OPTION, eventPage.getSelectSize(ticketListId));
                assertEquals(eventPage.getTicketNumberForCategory(1), ticketNumber - selectSize);
            }
        });
    }
}
