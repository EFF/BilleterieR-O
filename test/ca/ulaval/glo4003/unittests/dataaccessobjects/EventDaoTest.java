package ca.ulaval.glo4003.unittests.dataaccessobjects;

import ca.ulaval.glo4003.dataaccessobjects.EventDao;
import ca.ulaval.glo4003.dataaccessobjects.UniqueConstraintValidator;
import ca.ulaval.glo4003.exceptions.MaximumExceededException;
import ca.ulaval.glo4003.exceptions.RecordNotFoundException;
import ca.ulaval.glo4003.models.Category;
import ca.ulaval.glo4003.models.Event;
import ca.ulaval.glo4003.models.EventSearchCriteria;
import ca.ulaval.glo4003.services.InMemoryDaoPersistenceService;
import ca.ulaval.glo4003.unittests.helpers.EventsTestHelper;
import org.fest.assertions.Assertions;
import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class EventDaoTest {

    private EventDao eventDao;

    @Before
    public void setup() {
        eventDao = new EventDao(new InMemoryDaoPersistenceService(), new UniqueConstraintValidator<Event>());
    }

    @Test
    public void listReturnsAllEvents() {
        Event firstEvent = EventsTestHelper.createRandomEventtWithCategoryGivenSport(EventsTestHelper
                .FIRST_RANDOM_SPORT);
        Event secondEvent = EventsTestHelper.createRandomEventtWithCategoryGivenSport(EventsTestHelper
                .SECOND_RANDOM_SPORT);
        eventDao.create(firstEvent);
        eventDao.create(secondEvent);

        List<Event> result = eventDao.search(new EventSearchCriteria());

        assertEquals(2, result.size());
    }


    @Test
    public void searchAllThenReturnsAllEvents() {
        Event firstEvent = EventsTestHelper.createRandomEventtWithCategoryGivenSport(EventsTestHelper
                .FIRST_RANDOM_SPORT);
        Event secondEvent = EventsTestHelper.createRandomEventtWithCategoryGivenSport(EventsTestHelper
                .SECOND_RANDOM_SPORT);

        eventDao.create(firstEvent);
        eventDao.create(secondEvent);

        List<Event> result = eventDao.search(new EventSearchCriteria());

        assertEquals(2, result.size());
    }

    @Test
    public void searchSportThenReturnsFilteredResults() {
        Event firstEvent = EventsTestHelper.createRandomEventtWithCategoryGivenSport(EventsTestHelper
                .FIRST_RANDOM_SPORT);
        Event secondEvent = EventsTestHelper.createRandomEventtWithCategoryGivenSport(EventsTestHelper
                .SECOND_RANDOM_SPORT);
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
        Event firstEvent = EventsTestHelper.createRandomEventGivenTeam(EventsTestHelper.A_RANDOM_TEAM_NAME);
        Event secondEvent = EventsTestHelper.createRandomEventGivenTeam(EventsTestHelper.A_SECOND_RANDOM_TEAM_NAME);
        eventDao.create(firstEvent);
        eventDao.create(secondEvent);

        EventSearchCriteria eventSearchCriteria = new EventSearchCriteria();
        eventSearchCriteria.setTeamName(EventsTestHelper.A_RANDOM_TEAM_NAME);
        List<Event> result = eventDao.search(eventSearchCriteria);

        Assertions.assertThat(result.size()).isEqualTo(1);
        Assertions.assertThat(result.get(0).getHomeTeam().getName()).isEqualTo(EventsTestHelper.A_RANDOM_TEAM_NAME);
    }

    @Test
    public void searchDateStartWhenDateIsBeforeEventThenReturnsFilteredResults() {
        Event firstEvent = EventsTestHelper.createRandomEventtWithCategoryGivenSport(EventsTestHelper
                .FIRST_RANDOM_SPORT);
        Event secondEvent = EventsTestHelper.createRandomEventtWithCategoryGivenSport(EventsTestHelper
                .SECOND_RANDOM_SPORT);
        LocalDateTime dayOne = new LocalDateTime();
        LocalDateTime dayTwo = dayOne.plusDays(1);
        LocalDateTime dayThree = dayOne.plusDays(3);
        firstEvent.setDate(dayOne);
        secondEvent.setDate(dayThree);
        eventDao.create(firstEvent);
        eventDao.create(secondEvent);

        EventSearchCriteria eventSearchCriteria = new EventSearchCriteria();
        eventSearchCriteria.setDateStart(dayTwo);
        List<Event> result = eventDao.search(eventSearchCriteria);

        Assertions.assertThat(result.size()).isEqualTo(1);
        Assertions.assertThat(result.get(0).getId()).isEqualTo(secondEvent.getId());
    }

    @Test
    public void searchDateStartWhenDateIsSameDayAsEventThenReturnsFilteredResults() {
        Event firstEvent = EventsTestHelper.createRandomEventtWithCategoryGivenSport(EventsTestHelper
                .FIRST_RANDOM_SPORT);
        Event secondEvent = EventsTestHelper.createRandomEventtWithCategoryGivenSport(EventsTestHelper
                .SECOND_RANDOM_SPORT);
        LocalDateTime dayOne = new LocalDateTime();
        LocalDateTime dayThree = dayOne.plusDays(3);
        firstEvent.setDate(dayOne);
        secondEvent.setDate(dayThree);
        eventDao.create(firstEvent);
        eventDao.create(secondEvent);

        EventSearchCriteria eventSearchCriteria = new EventSearchCriteria();
        eventSearchCriteria.setDateStart(dayThree);
        List<Event> result = eventDao.search(eventSearchCriteria);

        Assertions.assertThat(result.size()).isEqualTo(1);
        Assertions.assertThat(result.get(0).getId()).isEqualTo(secondEvent.getId());
    }

    @Test
    public void searchDateEndWhenDateIsAfterEventThenReturnsFilteredResults() {
        Event firstEvent = EventsTestHelper.createRandomEventtWithCategoryGivenSport(EventsTestHelper
                .FIRST_RANDOM_SPORT);
        Event secondEvent = EventsTestHelper.createRandomEventtWithCategoryGivenSport(EventsTestHelper
                .SECOND_RANDOM_SPORT);
        LocalDateTime dayOne = new LocalDateTime();
        LocalDateTime veryFarAway = dayOne.plusDays(90);
        LocalDateTime evenMoreFarAway = veryFarAway.plusDays(1);
        firstEvent.setDate(dayOne);
        secondEvent.setDate(evenMoreFarAway);
        eventDao.create(firstEvent);
        eventDao.create(secondEvent);

        EventSearchCriteria eventSearchCriteria = new EventSearchCriteria();
        eventSearchCriteria.setDateEnd(veryFarAway);
        List<Event> result = eventDao.search(eventSearchCriteria);

        Assertions.assertThat(result.size()).isEqualTo(1);
        Assertions.assertThat(result.get(0).getId()).isEqualTo(firstEvent.getId());
    }

    @Test
    public void searchDateEndWhenDateIsSameDayAsEventThenReturnsFilteredResults() {
        Event firstEvent = EventsTestHelper.createRandomEventtWithCategoryGivenSport(EventsTestHelper
                .FIRST_RANDOM_SPORT);
        Event secondEvent = EventsTestHelper.createRandomEventtWithCategoryGivenSport(EventsTestHelper
                .SECOND_RANDOM_SPORT);
        LocalDateTime dayOne = new LocalDateTime();
        LocalDateTime dayThree = dayOne.plusDays(3);
        firstEvent.setDate(dayOne);
        secondEvent.setDate(dayThree);
        eventDao.create(firstEvent);
        eventDao.create(secondEvent);

        EventSearchCriteria eventSearchCriteria = new EventSearchCriteria();
        eventSearchCriteria.setDateEnd(dayOne);
        List<Event> result = eventDao.search(eventSearchCriteria);

        Assertions.assertThat(result.size()).isEqualTo(1);
        Assertions.assertThat(result.get(0).getId()).isEqualTo(firstEvent.getId());
    }

    @Test
    public void searchDatesRangeThenReturnsFilteredResultsWithinDateRange() {
        Event firstEvent = EventsTestHelper.createRandomEventtWithCategoryGivenSport(EventsTestHelper
                .FIRST_RANDOM_SPORT);
        Event secondEvent = EventsTestHelper.createRandomEventtWithCategoryGivenSport(EventsTestHelper
                .SECOND_RANDOM_SPORT);
        Event thirdEvent = EventsTestHelper.createRandomEventtWithCategoryGivenSport(EventsTestHelper
                .SECOND_RANDOM_SPORT);
        LocalDateTime beforeRange = new LocalDateTime();
        LocalDateTime whithinRange = beforeRange.plusDays(2);
        LocalDateTime afterRange = whithinRange.plusDays(2);
        firstEvent.setDate(beforeRange);
        secondEvent.setDate(whithinRange);
        thirdEvent.setDate(afterRange);
        eventDao.create(firstEvent);
        eventDao.create(secondEvent);

        EventSearchCriteria eventSearchCriteria = new EventSearchCriteria();
        eventSearchCriteria.setDateStart(whithinRange.minusDays(1));
        eventSearchCriteria.setDateEnd(whithinRange.plusDays(1));
        List<Event> result = eventDao.search(eventSearchCriteria);

        Assertions.assertThat(result.size()).isEqualTo(1);
        Assertions.assertThat(result.get(0).getId()).isEqualTo(secondEvent.getId());
    }

    @Test
    public void searchDateStartAndEndWhenDateIsSameDayAsEventThenReturnsFilteredResults() {
        Event firstEvent = EventsTestHelper.createRandomEventtWithCategoryGivenSport(EventsTestHelper
                .FIRST_RANDOM_SPORT);
        Event secondEvent = EventsTestHelper.createRandomEventtWithCategoryGivenSport(EventsTestHelper
                .SECOND_RANDOM_SPORT);
        LocalDateTime dayOne = new LocalDateTime();
        LocalDateTime dayThree = dayOne.plusDays(3);
        firstEvent.setDate(dayOne);
        secondEvent.setDate(dayThree);
        eventDao.create(firstEvent);
        eventDao.create(secondEvent);

        EventSearchCriteria eventSearchCriteria = new EventSearchCriteria();
        eventSearchCriteria.setDateStart(dayThree);
        eventSearchCriteria.setDateEnd(dayThree);
        List<Event> result = eventDao.search(eventSearchCriteria);

        Assertions.assertThat(result.size()).isEqualTo(1);
        Assertions.assertThat(result.get(0).getId()).isEqualTo(secondEvent.getId());
    }

    @Test(expected = RecordNotFoundException.class)
    public void findCategoryThrowsExceptionWhenCategoryDoesntExist() throws RecordNotFoundException {
        Event event = EventsTestHelper.createRandomEventGivenTeam(EventsTestHelper.A_RANDOM_TEAM_NAME);
        eventDao.create(event);

        Category category = eventDao.findCategory(event.getId(), EventsTestHelper.A_CATEGORY_ID);
    }

    @Test
    public void findCategoryReturnsTheCategoryWhenExists() throws RecordNotFoundException {
        Event event = EventsTestHelper.createRandomEventtWithCategoryGivenSport((EventsTestHelper.FIRST_RANDOM_SPORT));
        eventDao.create(event);

        Category category = eventDao.findCategory(event.getId(), EventsTestHelper.A_CATEGORY_ID);

        Assertions.assertThat(category.getId()).isEqualTo(EventsTestHelper.A_CATEGORY_ID);
    }

    @Test(expected = MaximumExceededException.class)
    public void decrementEventCategoryNumberOfTicketsThrowsExceptionWhenNoneAvailable() throws RecordNotFoundException, MaximumExceededException {
        Category category = new Category(EventsTestHelper.A_DOUBLE, EventsTestHelper.AN_INT,
                EventsTestHelper.A_CATEGORY_ID);
        Event event = EventsTestHelper.createRandomEventGivenACategory(category);
        eventDao.create(event);

        eventDao.decrementEventCategoryNumberOfTickets(event.getId(), category.getId(), Integer.MAX_VALUE);
    }

    @Test
    public void decrementEventCategoryNumberOfTicketsDecrements() throws MaximumExceededException,
            RecordNotFoundException {
        Category category = new Category(EventsTestHelper.A_DOUBLE, EventsTestHelper.AN_INT,
                EventsTestHelper.A_CATEGORY_ID);
        Event event = EventsTestHelper.createRandomEventGivenACategory(category);
        eventDao.create(event);

        eventDao.decrementEventCategoryNumberOfTickets(event.getId(), category.getId(), EventsTestHelper.AN_INT);

        Assertions.assertThat(eventDao.findCategory(event.getId(), category.getId()).getNumberOfTickets()).isEqualTo(0);
    }
}
