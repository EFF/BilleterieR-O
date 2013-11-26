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
                String ticketSeatToSelect = "2";

                eventPage.go();
                eventPage.isAt();

                eventPage.waitUntilCategoriesHasSize(2);

                eventPage.clickOnValueOfFirstSectionSelect("Niveau 100");
                eventPage.waitUntilTicketSelectIsPopulated();
                eventPage.selectSeatOfFirstTicketList(ticketSeatToSelect);
                Long ticketId = Long.parseLong(eventPage.getTicketIdOfSeatInFirstTicketSelect(ticketSeatToSelect));
                ticketPage.setTicketId(ticketId);

                eventPage.clickOnDetailsButton();
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

                eventPage.go();
                eventPage.isAt();
                eventPage.waitUntilCategoriesHasSize(2);

                eventPage.clickOnValueOfFirstSectionSelect("Niveau 100");
                eventPage.waitUntilTicketSelectIsPopulated();

                int ticketNumber = eventPage.getTicketNumberForCategory(1);
                int selectSize = eventPage.getTicketSelectSize();
                int quantityOfTicketsToBuy = selectSize;

                while (selectSize > 0) {
                    eventPage.clickOnFirstIndexValueOfFirstTicketSelect();
                    eventPage.clickOnAddButtonForCategory(1);
                    eventPage.waitUntilFirstTicketSelectSizeIs(selectSize - 1);
                    selectSize = eventPage.getTicketSelectSize();
                }
                assertEquals(0, eventPage.getTicketSelectSize());
                assertEquals(eventPage.getTicketNumberForCategory(1), ticketNumber - quantityOfTicketsToBuy);
            }
        });
    }
}
