package ca.ulaval.glo4003.controllers;

import ca.ulaval.glo4003.dataaccessobjects.EventDao;
import ca.ulaval.glo4003.dataaccessobjects.EventDaoInMemory;
import ca.ulaval.glo4003.models.Category;
import ca.ulaval.glo4003.models.Event;
import ca.ulaval.glo4003.models.Gender;
import ca.ulaval.glo4003.models.Sport;
import org.codehaus.jackson.JsonNode;
import org.fest.assertions.Assertions;
import org.junit.Before;
import org.junit.Test;
import play.libs.Json;
import play.mvc.Result;
import play.test.Helpers;

public class EventsTest {

    private EventDao eventDao;
    private Events events;
    private static final String FIRST_RANDOM_SPORT = "Football";
    private static final String SECOND_RANDOM_SPORT = "Rugby";

    @Before
    public void setup() {
        eventDao = new EventDaoInMemory();
        events = new Events(eventDao);
    }

    @Test
    public void indexReturnsAllEvents() {
        // Arrange
        Event firstEvent = EventsTestHelper.createRandomEventGivenSport(1, FIRST_RANDOM_SPORT);
        Event secondEvent = EventsTestHelper.createRandomEventGivenSport(2, SECOND_RANDOM_SPORT);

        eventDao.create(firstEvent);
        eventDao.create(secondEvent);

        // Act
        Result result = events.index();
        Assertions.assertThat(Helpers.status(result)).isEqualTo(Helpers.OK);
        Assertions.assertThat(Helpers.contentType(result)).isEqualTo("application/json");

        String json = Helpers.contentAsString(result);
        JsonNode jsonNode = Json.parse(json);

        Assertions.assertThat(jsonNode.isArray());

        // Assert
        JsonNode event1 = jsonNode.get(0);
        Assertions.assertThat(event1.get("id").asLong()).isEqualTo(1);
        Assertions.assertThat(event1.get("sport").get("name").asText()).isEqualTo(firstEvent.getSport().getName());

        JsonNode event2 = jsonNode.get(1);
        Assertions.assertThat(event2.get("id").asLong()).isEqualTo(2);
        Assertions.assertThat(event2.get("sport").get("name").asText()).isEqualTo(secondEvent.getSport().getName());
    }

    @Test
    public void showReturnsEvent() {
        // Arrange
        Event firstEvent = EventsTestHelper.createRandomEventGivenSport(1, FIRST_RANDOM_SPORT);
        Event secondEvent = EventsTestHelper.createRandomEventGivenSport(2, SECOND_RANDOM_SPORT);

        eventDao.create(firstEvent);
        eventDao.create(secondEvent);

        // Act
        Result result = events.show(firstEvent.getId());
        Assertions.assertThat(Helpers.status(result)).isEqualTo(Helpers.OK);
        Assertions.assertThat(Helpers.contentType(result)).isEqualTo("application/json");

        String json = Helpers.contentAsString(result);
        JsonNode jsonNode = Json.parse(json);

        // Assert
        Assertions.assertThat(jsonNode.get("id").asLong()).isEqualTo(1);
    }

}
