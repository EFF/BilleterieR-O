package ca.ulaval.glo4003.unittests.controllers;

import ca.ulaval.glo4003.controllers.Events;
import ca.ulaval.glo4003.dataaccessobjects.EventDao;
import ca.ulaval.glo4003.dataaccessobjects.EventDaoInMemory;
import ca.ulaval.glo4003.models.Event;
import ca.ulaval.glo4003.unittests.helpers.EventsTestHelper;
import org.codehaus.jackson.JsonNode;
import org.fest.assertions.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;

import java.util.HashMap;

import static org.powermock.api.mockito.PowerMockito.mock;

public class EventsTest {

    private EventDao eventDao;
    private Events events;

    @Before
    public void setup() {
        eventDao = new EventDaoInMemory();
        events = new Events(eventDao);
    }

    @After
    public void tearDown() {
        Http.Context.current.remove();
    }

    @Test
    public void indexReturnsAllEvents() throws Exception {
        //TODO in order to test with querystring we need to mock play.api.mvc.RequestHeader.queryString() to return Map<String, Seq<String>>
        Http.Context.current.set(new Http.Context((long) 1, mock(play.api.mvc.RequestHeader.class), mock(Http.Request.class), new HashMap<String, String>(), new HashMap<String, String>(), new HashMap<String, Object>()));
        // Arrange
        Event firstEvent = EventsTestHelper.createRandomEventGivenSport(1, EventsTestHelper.FIRST_RANDOM_SPORT);
        Event secondEvent = EventsTestHelper.createRandomEventGivenSport(2, EventsTestHelper.SECOND_RANDOM_SPORT);

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
        Http.Context.current.set(new Http.Context((long) 1, mock(play.api.mvc.RequestHeader.class), mock(Http.Request.class), new HashMap<String, String>(), new HashMap<String, String>(), new HashMap<String, Object>()));
        // Arrange
        Event firstEvent = EventsTestHelper.createRandomEventGivenSport(1, EventsTestHelper.FIRST_RANDOM_SPORT);
        Event secondEvent = EventsTestHelper.createRandomEventGivenSport(2, EventsTestHelper.SECOND_RANDOM_SPORT);

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
