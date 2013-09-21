package ca.ulaval.glo4003.unittests.dataaccessobjects;

import ca.ulaval.glo4003.unittests.helpers.EventsTestHelper;
import ca.ulaval.glo4003.models.Event;
import ca.ulaval.glo4003.models.EventSearchCriteria;
import org.fest.assertions.Assertions;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class EventDaoInMemoryTest {
    EventDaoInMemory eventDao;

    @Before
    public void setup() {
        eventDao = new EventDaoInMemory();
    }

    @Test
    public void searchAllThenReturnsAllEvents() {
        Event firstEvent = EventsTestHelper.createRandomEventGivenSport(1, EventsTestHelper.FIRST_RANDOM_SPORT);
        Event secondEvent = EventsTestHelper.createRandomEventGivenSport(2, EventsTestHelper.SECOND_RANDOM_SPORT);

        eventDao.create(firstEvent);
        eventDao.create(secondEvent);

        List<Event> result = eventDao.search(new EventSearchCriteria());
        Assertions.assertThat(result.size()).isEqualTo(2);
    }

    @Test
    public void searchSportThenReturnsFilteredResults() {
        Event firstEvent = EventsTestHelper.createRandomEventGivenSport(1, EventsTestHelper.FIRST_RANDOM_SPORT);
        Event secondEvent = EventsTestHelper.createRandomEventGivenSport(2, EventsTestHelper.SECOND_RANDOM_SPORT);

        eventDao.create(firstEvent);
        eventDao.create(secondEvent);

        EventSearchCriteria eventSearchCriteria = new EventSearchCriteria();
        eventSearchCriteria.setSportName(EventsTestHelper.SECOND_RANDOM_SPORT);

        List<Event> result = eventDao.search(eventSearchCriteria);
        Assertions.assertThat(result.size()).isEqualTo(1);
        Assertions.assertThat(result.get(0).getSport().getName()).isEqualTo(EventsTestHelper.SECOND_RANDOM_SPORT);
    }

    @Test
    public void searchTeamThenReturnsFilteredResults() {
        Event firstEvent = EventsTestHelper.createRandomEventGivenTeam(1, EventsTestHelper.A_RANDOM_TEAM_NAME);
        Event secondEvent = EventsTestHelper.createRandomEventGivenTeam(2, EventsTestHelper.A_SECOND_RANDOM_TEAM_NAME);

        eventDao.create(firstEvent);
        eventDao.create(secondEvent);

        EventSearchCriteria eventSearchCriteria = new EventSearchCriteria();
        eventSearchCriteria.setTeamName(EventsTestHelper.A_RANDOM_TEAM_NAME);

        List<Event> result = eventDao.search(eventSearchCriteria);
        Assertions.assertThat(result.size()).isEqualTo(1);
        Assertions.assertThat(result.get(0).getTeam().getName()).isEqualTo(EventsTestHelper.A_RANDOM_TEAM_NAME);
    }


}
