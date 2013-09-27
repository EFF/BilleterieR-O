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

                // No filter => 2 results
                eventsPage.waitUntilEventsHasSize(2);
                assertTrue(eventsPage.eventHas(0, "Soccer", "Masculin", 1320));
                assertTrue(eventsPage.eventHas(1, "Soccer", "Féminin", 1320));

                // Select Golf => no result
                eventsPage.selectSport("Golf");
                eventsPage.waitUntilEventsHasSize(0);
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

    @Test
    public void dateFiltersEventList() {
        running(testServer(3333, fakeApplication(new TestGlobal())), FIREFOX, new F.Callback<TestBrowser>() {
            @Override
            public void invoke(TestBrowser browser) {
                EventsPage eventsPage = new EventsPage(browser.getDriver());
                eventsPage.go();
                eventsPage.isAt();

                // No filter => 2 results
                eventsPage.waitUntilEventsHasSize(2);
                assertTrue(eventsPage.eventHas(0, "Soccer", "Masculin", 1320));
                assertTrue(eventsPage.eventHas(1, "Soccer", "Féminin", 1320));

                // Late date start => 0 results
                eventsPage.selectDateStart("2015-09-09");
                eventsPage.waitUntilEventsHasSize(0);
                assertThat(eventsPage.getEmptyAlert()).isDisplayed();

                // Early date start => 2 results
                eventsPage.selectDateStart("2011-09-09");
                eventsPage.waitUntilEventsHasSize(2);
                assertTrue(eventsPage.eventHas(0, "Soccer", "Masculin", 1320));
                assertTrue(eventsPage.eventHas(1, "Soccer", "Féminin", 1320));

                // Early date start, Early date end => 0 results
                eventsPage.selectDateStart("2011-09-09");
                eventsPage.selectDateEnd("2012-09-09");
                eventsPage.waitUntilEventsHasSize(0);
                assertThat(eventsPage.getEmptyAlert()).isDisplayed();

                // Early date start, Late date end => 2 results
                eventsPage.selectDateStart("2011-09-09");
                eventsPage.selectDateEnd("2015-09-09");
                eventsPage.waitUntilEventsHasSize(2);
                assertTrue(eventsPage.eventHas(0, "Soccer", "Masculin", 1320));
                assertTrue(eventsPage.eventHas(1, "Soccer", "Féminin", 1320));
            }
        });
    }
}
