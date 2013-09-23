package ca.ulaval.glo4003.unittests.dataaccessobjects;

import ca.ulaval.glo4003.dataaccessobjects.EventDaoInMemory;
import ca.ulaval.glo4003.unittests.helpers.EventsTestHelper;
import ca.ulaval.glo4003.models.Event;
import ca.ulaval.glo4003.models.EventSearchCriteria;
import org.fest.assertions.Assertions;
import org.joda.time.LocalDate;
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
    public void testShow() {
        // Arrange
        Event event = EventsTestHelper.createRandomEventGivenSport(1, EventsTestHelper.FIRST_RANDOM_SPORT);

        eventDao.create(event);

        // Act
        Event result = eventDao.read(1);

        // Assert
        Assertions.assertThat(result.getId()).isEqualTo(event.getId());
        Assertions.assertThat(result.getSport().getName()).isEqualTo(event.getSport().getName());
    }

    @Test
    public void testList() {
        // Arrange
        Event firstEvent = EventsTestHelper.createRandomEventGivenSport(1, EventsTestHelper.FIRST_RANDOM_SPORT);
        Event secondEvent = EventsTestHelper.createRandomEventGivenSport(2, EventsTestHelper.SECOND_RANDOM_SPORT);

        eventDao.create(firstEvent);
        eventDao.create(secondEvent);

        // Act
        List<Event> result = eventDao.search(new EventSearchCriteria());

        // Assert
        Assertions.assertThat(result.size()).isEqualTo(2);
    }

    @Test
    public void searchAllThenReturnsAllEvents() {
        // Arrange
        Event firstEvent = EventsTestHelper.createRandomEventGivenSport(1, EventsTestHelper.FIRST_RANDOM_SPORT);
        Event secondEvent = EventsTestHelper.createRandomEventGivenSport(2, EventsTestHelper.SECOND_RANDOM_SPORT);

        eventDao.create(firstEvent);
        eventDao.create(secondEvent);

        // Act
        List<Event> result = eventDao.search(new EventSearchCriteria());

        // Assert
        Assertions.assertThat(result.size()).isEqualTo(2);
    }

    @Test
    public void searchSportThenReturnsFilteredResults() {
        // Arrange
        Event firstEvent = EventsTestHelper.createRandomEventGivenSport(1, EventsTestHelper.FIRST_RANDOM_SPORT);
        Event secondEvent = EventsTestHelper.createRandomEventGivenSport(2, EventsTestHelper.SECOND_RANDOM_SPORT);

        eventDao.create(firstEvent);
        eventDao.create(secondEvent);

        // Act
        EventSearchCriteria eventSearchCriteria = new EventSearchCriteria();
        eventSearchCriteria.setSportName(EventsTestHelper.SECOND_RANDOM_SPORT);

        List<Event> result = eventDao.search(eventSearchCriteria);

        // Assert
        Assertions.assertThat(result.size()).isEqualTo(1);
        Assertions.assertThat(result.get(0).getSport().getName()).isEqualTo(EventsTestHelper.SECOND_RANDOM_SPORT);
    }

    @Test
    public void searchTeamThenReturnsFilteredResults() {
        // Arrange
        Event firstEvent = EventsTestHelper.createRandomEventGivenTeam(1, EventsTestHelper.A_RANDOM_TEAM_NAME);
        Event secondEvent = EventsTestHelper.createRandomEventGivenTeam(2, EventsTestHelper.A_SECOND_RANDOM_TEAM_NAME);

        eventDao.create(firstEvent);
        eventDao.create(secondEvent);

        // Act
        EventSearchCriteria eventSearchCriteria = new EventSearchCriteria();
        eventSearchCriteria.setTeamName(EventsTestHelper.A_RANDOM_TEAM_NAME);

        List<Event> result = eventDao.search(eventSearchCriteria);

        // Assert
        Assertions.assertThat(result.size()).isEqualTo(1);
        Assertions.assertThat(result.get(0).getTeam().getName()).isEqualTo(EventsTestHelper.A_RANDOM_TEAM_NAME);
    }

    @Test
    public void searchDateStartWhenDateIsBeforeEventThenReturnsFilteredResults() {
        // Arrange
        Event firstEvent = EventsTestHelper.createRandomEventGivenSport(1, EventsTestHelper.FIRST_RANDOM_SPORT);
        Event secondEvent = EventsTestHelper.createRandomEventGivenSport(2, EventsTestHelper.SECOND_RANDOM_SPORT);

        LocalDate dayOne = new LocalDate();
        LocalDate dayTwo = dayOne.plusDays(1);
        LocalDate dayThree = dayOne.plusDays(3);

        firstEvent.setDate(dayOne);
        secondEvent.setDate(dayThree);

        eventDao.create(firstEvent);
        eventDao.create(secondEvent);

        // Act
        EventSearchCriteria eventSearchCriteria = new EventSearchCriteria();
        eventSearchCriteria.setDateStart(dayTwo);

        List<Event> result = eventDao.search(eventSearchCriteria);

        // Assert
        Assertions.assertThat(result.size()).isEqualTo(1);
        Assertions.assertThat(result.get(0).getId()).isEqualTo(secondEvent.getId());
    }

    @Test
    public void searchDateStartWhenDateIsSameDayAsEventThenReturnsFilteredResults() {
        // Arrange
        Event firstEvent = EventsTestHelper.createRandomEventGivenSport(1, EventsTestHelper.FIRST_RANDOM_SPORT);
        Event secondEvent = EventsTestHelper.createRandomEventGivenSport(2, EventsTestHelper.SECOND_RANDOM_SPORT);

        LocalDate dayOne = new LocalDate();
        LocalDate dayThree = dayOne.plusDays(3);

        firstEvent.setDate(dayOne);
        secondEvent.setDate(dayThree);

        eventDao.create(firstEvent);
        eventDao.create(secondEvent);

        // Act
        EventSearchCriteria eventSearchCriteria = new EventSearchCriteria();
        eventSearchCriteria.setDateStart(dayThree);

        List<Event> result = eventDao.search(eventSearchCriteria);

        // Assert
        Assertions.assertThat(result.size()).isEqualTo(1);
        Assertions.assertThat(result.get(0).getId()).isEqualTo(secondEvent.getId());
    }

    @Test
    public void searchDateEndWhenDateIsAfterEventThenReturnsFilteredResults() {
        // Arrange
        Event firstEvent = EventsTestHelper.createRandomEventGivenSport(1, EventsTestHelper.FIRST_RANDOM_SPORT);
        Event secondEvent = EventsTestHelper.createRandomEventGivenSport(2, EventsTestHelper.SECOND_RANDOM_SPORT);

        LocalDate dayOne = new LocalDate();
        LocalDate veryFarAway = dayOne.plusDays(90);
        LocalDate evenMoreFarAway = veryFarAway.plusDays(1);

        firstEvent.setDate(dayOne);
        secondEvent.setDate(evenMoreFarAway);

        eventDao.create(firstEvent);
        eventDao.create(secondEvent);

        // Act
        EventSearchCriteria eventSearchCriteria = new EventSearchCriteria();
        eventSearchCriteria.setDateEnd(veryFarAway);

        List<Event> result = eventDao.search(eventSearchCriteria);

        // Assert
        Assertions.assertThat(result.size()).isEqualTo(1);
        Assertions.assertThat(result.get(0).getId()).isEqualTo(firstEvent.getId());
    }

    @Test
    public void searchDateEndWhenDateIsSameDayAsEventThenReturnsFilteredResults() {
        // Arrange
        Event firstEvent = EventsTestHelper.createRandomEventGivenSport(1, EventsTestHelper.FIRST_RANDOM_SPORT);
        Event secondEvent = EventsTestHelper.createRandomEventGivenSport(2, EventsTestHelper.SECOND_RANDOM_SPORT);

        LocalDate dayOne = new LocalDate();
        LocalDate dayThree = dayOne.plusDays(3);

        firstEvent.setDate(dayOne);
        secondEvent.setDate(dayThree);

        eventDao.create(firstEvent);
        eventDao.create(secondEvent);

        // Act
        EventSearchCriteria eventSearchCriteria = new EventSearchCriteria();
        eventSearchCriteria.setDateEnd(dayOne);

        List<Event> result = eventDao.search(eventSearchCriteria);

        // Assert
        Assertions.assertThat(result.size()).isEqualTo(1);
        Assertions.assertThat(result.get(0).getId()).isEqualTo(firstEvent.getId());
    }

    @Test
    public void searchDatesRangeThenReturnsFilteredResultsWithinDateRange() {
        // Arrange
        Event firstEvent = EventsTestHelper.createRandomEventGivenSport(1, EventsTestHelper.FIRST_RANDOM_SPORT);
        Event secondEvent = EventsTestHelper.createRandomEventGivenSport(2, EventsTestHelper.SECOND_RANDOM_SPORT);
        Event thirdEvent = EventsTestHelper.createRandomEventGivenSport(3, EventsTestHelper.SECOND_RANDOM_SPORT);

        LocalDate beforeRange = new LocalDate();
        LocalDate whithinRange = beforeRange.plusDays(2);
        LocalDate afterRange = whithinRange.plusDays(2);

        firstEvent.setDate(beforeRange);
        secondEvent.setDate(whithinRange);
        thirdEvent.setDate(afterRange);

        eventDao.create(firstEvent);
        eventDao.create(secondEvent);

        // Act
        EventSearchCriteria eventSearchCriteria = new EventSearchCriteria();
        eventSearchCriteria.setDateStart(whithinRange.minusDays(1));
        eventSearchCriteria.setDateEnd(whithinRange.plusDays(1));

        List<Event> result = eventDao.search(eventSearchCriteria);

        // Assert
        Assertions.assertThat(result.size()).isEqualTo(1);
        Assertions.assertThat(result.get(0).getId()).isEqualTo(secondEvent.getId());
    }

    @Test
    public void searchDateStartAndEndWhenDateIsSameDayAsEventThenReturnsFilteredResults() {
        // Arrange
        Event firstEvent = EventsTestHelper.createRandomEventGivenSport(1, EventsTestHelper.FIRST_RANDOM_SPORT);
        Event secondEvent = EventsTestHelper.createRandomEventGivenSport(2, EventsTestHelper.SECOND_RANDOM_SPORT);

        LocalDate dayOne = new LocalDate();
        LocalDate dayThree = dayOne.plusDays(3);

        firstEvent.setDate(dayOne);
        secondEvent.setDate(dayThree);

        eventDao.create(firstEvent);
        eventDao.create(secondEvent);

        // Act
        EventSearchCriteria eventSearchCriteria = new EventSearchCriteria();
        eventSearchCriteria.setDateStart(dayThree);
        eventSearchCriteria.setDateEnd(dayThree);

        List<Event> result = eventDao.search(eventSearchCriteria);

        // Assert
        Assertions.assertThat(result.size()).isEqualTo(1);
        Assertions.assertThat(result.get(0).getId()).isEqualTo(secondEvent.getId());
    }
}
