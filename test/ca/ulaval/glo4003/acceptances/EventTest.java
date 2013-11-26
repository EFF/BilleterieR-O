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

    private final String A_SECTION_NAME = "Niveau 100";
    private final int A_CATEGORY_ID = 1;
    private final int AN_EVENT_ID = 1;
    private final int ANOTHER_EVENT_ID = 2;
    private final int A_TEMPORARY_INVALID_TICKET_ID = -1;

    @Test
    public void returnAnEventWithPricesAndNumberOfTicketsPerCategory() {
        running(testServer(3333, fakeApplication(new TestGlobal())), FIREFOX, new F.Callback<TestBrowser>() {
            public void invoke(TestBrowser browser) {
                EventPage eventPage = new EventPage(browser.getDriver(), AN_EVENT_ID);
                eventPage.go();
                eventPage.isAt();

                eventPage.waitUntilCategoriesHasSize(2);

                assertTrue(eventPage.categoryHas(0, "12$", 120));
                assertTrue(eventPage.categoryHas(1, "8$", 1200));
            }
        });
    }

    @Test
    public void clickOnDetailsButtonGoesToTicketInfoPage() {
        running(testServer(3333, fakeApplication(new TestGlobal())), FIREFOX, new F.Callback<TestBrowser>() {
            public void invoke(TestBrowser browser) {
                EventPage eventPage = new EventPage(browser.getDriver(), ANOTHER_EVENT_ID);
                TicketPage ticketPage = new TicketPage(browser.getDriver(), A_TEMPORARY_INVALID_TICKET_ID, ANOTHER_EVENT_ID);
                String ticketSeatToSelect = "2";

                eventPage.go();
                eventPage.isAt();

                //TODO : Rename/replace
                eventPage.waitUntilCategoriesHasSize(2);

                eventPage.selectSectionInSectionListForCategory(A_CATEGORY_ID, A_SECTION_NAME);
                eventPage.waitUntilTicketsListIsPopulated(A_CATEGORY_ID);
                eventPage.selectSeatInTicketsListForCategory(A_CATEGORY_ID, ticketSeatToSelect);
                Long ticketId = Long.parseLong(eventPage.getTicketIdOfSeatInTicketsListForCategory(A_CATEGORY_ID, ticketSeatToSelect));
                ticketPage.setTicketId(ticketId);

                eventPage.clickOnDetailsButton();
                ticketPage.isAt();

                assertEquals(ticketPage.getUrl(), browser.url());
            }
        });
    }

    @Test
    public void buyAllSeatTicketThenComboIsEmpty() {
        running(testServer(3333, fakeApplication(new TestGlobal())), FIREFOX, new F.Callback<TestBrowser>() {
            public void invoke(TestBrowser browser) {
                EventPage eventPage = new EventPage(browser.getDriver(), ANOTHER_EVENT_ID);

                eventPage.go();
                eventPage.isAt();
                eventPage.waitUntilCategoriesHasSize(2);

                eventPage.selectSectionInSectionListForCategory(A_CATEGORY_ID, A_SECTION_NAME);
                eventPage.waitUntilTicketsListIsPopulated(A_CATEGORY_ID);

                int ticketNumber = eventPage.getTicketNumberForCategory(A_CATEGORY_ID);
                int selectSize = eventPage.getTicketsListSizeForCategory(A_CATEGORY_ID);
                int quantityOfTicketsToBuy = selectSize;

                while (selectSize > 0) {
                    eventPage.clickOnFirstIndexValueOfTicketsListForCategory(A_CATEGORY_ID);
                    eventPage.clickOnAddButtonForCategory(A_CATEGORY_ID);
                    eventPage.waitUntilTicketsListForCategoryHasSize(A_CATEGORY_ID, selectSize - 1);
                    selectSize = eventPage.getTicketsListSizeForCategory(A_CATEGORY_ID);
                }
                assertEquals(0, eventPage.getTicketsListSizeForCategory(A_CATEGORY_ID));
                assertEquals(eventPage.getTicketNumberForCategory(A_CATEGORY_ID), ticketNumber - quantityOfTicketsToBuy);
            }
        });
    }
}
