package ca.ulaval.glo4003.acceptances;

import ca.ulaval.glo4003.TestGlobal;
import ca.ulaval.glo4003.acceptances.pages.EventPage;
import ca.ulaval.glo4003.acceptances.pages.TicketPage;
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

    @Test
    public void selectASeatThenValidateTicketInfos() {
        running(testServer(3333, fakeApplication(new TestGlobal())), FIREFOX, new F.Callback<TestBrowser>() {
            public void invoke(TestBrowser browser) {
                EventPage eventPage = new EventPage(browser.getDriver(), 2);
                TicketPage ticketPage = new TicketPage(browser.getDriver(), -1L);

                eventPage.go();
                eventPage.isAt();

                eventPage.waitUntilCategoriesHasSize(2);

                eventPage.selectCombo("sectionList1", "Niveau 100");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                eventPage.selectCombo("ticketList1", "2");
                ticketPage.setTicketId(Integer.parseInt(eventPage.getComboOptionValue("ticketList1","2")));

                eventPage.clickOnButton(".btn-details1");
                ticketPage.isAt();
                assertTrue(browser.url().equals(ticketPage.getUrl()));

                assertTrue(ticketPage.isTdValueExists("2"));
            }
        });
    }
}
