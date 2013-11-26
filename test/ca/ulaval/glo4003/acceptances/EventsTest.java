package ca.ulaval.glo4003.acceptances;

import ca.ulaval.glo4003.TestGlobal;
import ca.ulaval.glo4003.acceptances.pages.EventPage;
import ca.ulaval.glo4003.acceptances.pages.EventsPage;
import ca.ulaval.glo4003.models.Gender;
import org.fluentlenium.adapter.FluentTest;
import org.junit.Test;
import play.libs.F;
import play.test.TestBrowser;

import static org.fest.assertions.fluentlenium.FluentLeniumAssertions.assertThat;
import static org.junit.Assert.assertTrue;
import static play.test.Helpers.*;

public class EventsTest extends FluentTest {

    public static final int EXPECTED_NUMBER_OF_TICKETS_ON_FIRST_EVENT = 1320;
    public static final int EXPECTED_NUMBER_OF_TICKETS_ON_SECOND_EVENT = 140;
    private final int AN_EVENT_ID = 1;
    private final String SOCCER_SPORT = "Soccer";
    private final String GOLF_SPORT = "Golf";
    private final String GENDER_MALE = "Masculin";
    private final String GENDER_FEMALE = "Féminin";
    private final int FIRST_EVENT_INDEX = 0;
    private final int SECOND_EVENT_INDEX = 1;

    @Test
    public void filtersEventsList() {
        running(testServer(3333, fakeApplication(new TestGlobal())), FIREFOX, new F.Callback<TestBrowser>() {
            @Override
            public void invoke(TestBrowser browser) {
                EventsPage eventsPage = new EventsPage(browser.getDriver());
                eventsPage.go();
                eventsPage.isAt();

                assertThat(eventsPage.getEmptyAlert()).isNotDisplayed();

                // No filter => 2 results
                eventsPage.waitUntilEventsHasSize(2);
                assertTrue(eventsPage.eventHas(FIRST_EVENT_INDEX, SOCCER_SPORT, GENDER_MALE, EXPECTED_NUMBER_OF_TICKETS_ON_FIRST_EVENT));
                assertTrue(eventsPage.eventHas(SECOND_EVENT_INDEX, SOCCER_SPORT, GENDER_FEMALE, EXPECTED_NUMBER_OF_TICKETS_ON_SECOND_EVENT));

                // Select Golf => no result
                eventsPage.selectSport(GOLF_SPORT);
                eventsPage.waitUntilEventsHasSize(0);
                assertThat(eventsPage.getEventsTable()).isNotDisplayed();
                assertThat(eventsPage.getEmptyAlert()).isDisplayed();

                // Select Soccer => 2 results
                eventsPage.selectSport(SOCCER_SPORT);
                eventsPage.waitUntilEventsHasSize(2);
                assertThat(eventsPage.getEmptyAlert()).isNotDisplayed();
                assertTrue(eventsPage.eventHas(FIRST_EVENT_INDEX, SOCCER_SPORT, GENDER_MALE, EXPECTED_NUMBER_OF_TICKETS_ON_FIRST_EVENT));
                assertTrue(eventsPage.eventHas(SECOND_EVENT_INDEX, SOCCER_SPORT, GENDER_FEMALE, EXPECTED_NUMBER_OF_TICKETS_ON_SECOND_EVENT));

                // Select Soccer masculin => 1 result
                eventsPage.selectGender(Gender.MALE.toString());
                eventsPage.waitUntilEventsHasSize(1);
                assertThat(eventsPage.getEmptyAlert()).isNotDisplayed();
                assertTrue(eventsPage.eventHas(FIRST_EVENT_INDEX, SOCCER_SPORT, GENDER_MALE, EXPECTED_NUMBER_OF_TICKETS_ON_FIRST_EVENT));
            }
        });
    }

    @Test
    public void dateFiltersEventsList() {
        running(testServer(3333, fakeApplication(new TestGlobal())), FIREFOX, new F.Callback<TestBrowser>() {
            @Override
            public void invoke(TestBrowser browser) {
                EventsPage eventsPage = new EventsPage(browser.getDriver());
                eventsPage.go();
                eventsPage.isAt();

                // No filter => 2 results
                eventsPage.waitUntilEventsHasSize(2);
                assertTrue(eventsPage.eventHas(FIRST_EVENT_INDEX, SOCCER_SPORT, GENDER_MALE, EXPECTED_NUMBER_OF_TICKETS_ON_FIRST_EVENT));
                assertTrue(eventsPage.eventHas(SECOND_EVENT_INDEX, SOCCER_SPORT, GENDER_FEMALE, EXPECTED_NUMBER_OF_TICKETS_ON_SECOND_EVENT));

                // Late date start => 0 results
                eventsPage.selectDateStart("2015-09-09");
                eventsPage.waitUntilEventsHasSize(0);
                assertThat(eventsPage.getEmptyAlert()).isDisplayed();

                // Early date start => 2 results
                eventsPage.selectDateStart("2011-09-09");
                eventsPage.waitUntilEventsHasSize(2);
                assertTrue(eventsPage.eventHas(FIRST_EVENT_INDEX, SOCCER_SPORT, GENDER_MALE, EXPECTED_NUMBER_OF_TICKETS_ON_FIRST_EVENT));
                assertTrue(eventsPage.eventHas(SECOND_EVENT_INDEX, SOCCER_SPORT, GENDER_FEMALE, EXPECTED_NUMBER_OF_TICKETS_ON_SECOND_EVENT));

                // Early date start, Early date end => 0 results
                eventsPage.selectDateStart("2011-09-09");
                eventsPage.selectDateEnd("2012-09-09");
                eventsPage.waitUntilEventsHasSize(0);
                assertThat(eventsPage.getEmptyAlert()).isDisplayed();

                // Early date start, Late date end => 2 results
                eventsPage.selectDateStart("2011-09-09");
                eventsPage.selectDateEnd("2015-09-09");
                eventsPage.waitUntilEventsHasSize(2);
                assertTrue(eventsPage.eventHas(FIRST_EVENT_INDEX, SOCCER_SPORT, GENDER_MALE, EXPECTED_NUMBER_OF_TICKETS_ON_FIRST_EVENT));
                assertTrue(eventsPage.eventHas(SECOND_EVENT_INDEX, SOCCER_SPORT, GENDER_FEMALE, EXPECTED_NUMBER_OF_TICKETS_ON_SECOND_EVENT));
            }
        });
    }

    @Test
    public void clickOnSeeEventAndValidateInfo() {
        running(testServer(3333, fakeApplication(new TestGlobal())), FIREFOX, new F.Callback<TestBrowser>() {
            @Override
            public void invoke(TestBrowser browser) {
                EventsPage eventsPage = new EventsPage(browser.getDriver());
                EventPage eventPage = new EventPage(browser.getDriver(), AN_EVENT_ID);
                eventsPage.go();
                eventsPage.isAt();

                eventsPage.waitUntilEventsHasSize(2);
                eventsPage.clickOnFirstEventButtonDetails();
                eventPage.isAt();

                assertTrue(browser.url().equals(eventPage.getUrl()));
            }
        });
    }
}
