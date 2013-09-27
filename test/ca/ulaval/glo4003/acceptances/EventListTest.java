package ca.ulaval.glo4003.acceptances;

import ca.ulaval.glo4003.TestGlobal;
import ca.ulaval.glo4003.acceptances.pages.EventsPage;
import org.junit.Test;
import play.libs.F;
import play.test.TestBrowser;

import static org.fest.assertions.fluentlenium.FluentLeniumAssertions.assertThat;
import static org.junit.Assert.assertTrue;
import static play.test.Helpers.*;

public class EventListTest {

    @Test
    public void filtersEventList() {
        running(testServer(3333, fakeApplication(new TestGlobal())), FIREFOX, new F.Callback<TestBrowser>() {
            @Override
            public void invoke(TestBrowser browser) {
                EventsPage eventsPage = new EventsPage(browser.getDriver());
                eventsPage.go();
                eventsPage.isAt();

                assertThat(eventsPage.getLoadingAlert()).isDisplayed();
                assertThat(eventsPage.getEmptyAlert()).isNotDisplayed();

                // No filter => 2 results
                eventsPage.waitUntilEventsHasSize(2);
                assertTrue(eventsPage.eventHas(0, "Soccer", "Masculin", 1320));
                assertTrue(eventsPage.eventHas(1, "Soccer", "Féminin", 1320));

                // Select Golf => no result
                eventsPage.selectSport("Golf");
                eventsPage.waitUntilEventsHasSize(0);
                assertThat(eventsPage.getEventsTable()).isNotDisplayed();
                assertThat(eventsPage.getEmptyAlert()).isDisplayed();

                // Select Soccer => 2 results
                eventsPage.selectSport("Soccer");
                eventsPage.waitUntilEventsHasSize(2);
                assertThat(eventsPage.getEmptyAlert()).isNotDisplayed();
                assertTrue(eventsPage.eventHas(0, "Soccer", "Masculin", 1320));
                assertTrue(eventsPage.eventHas(1, "Soccer", "Féminin", 1320));

                // Select Soccer masculin => 1 result
                eventsPage.selectGender("MALE");
                eventsPage.waitUntilEventsHasSize(1);
                assertThat(eventsPage.getEmptyAlert()).isNotDisplayed();
                assertTrue(eventsPage.eventHas(0, "Soccer", "Masculin", 1320));
            }
        });
    }
}
