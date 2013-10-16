package ca.ulaval.glo4003.unittests.controllers;

import ca.ulaval.glo4003.controllers.Events;
import ca.ulaval.glo4003.dataaccessobjects.EventDao;
import ca.ulaval.glo4003.models.Event;
import ca.ulaval.glo4003.services.InMemoryDaoPersistenceService;
import ca.ulaval.glo4003.unittests.helpers.EventsTestHelper;
import org.codehaus.jackson.JsonNode;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class EventsTest {

    private EventDao eventDao;
    private Events events;

    @Before
    public void setup() {
        eventDao = new EventDao(new InMemoryDaoPersistenceService());
        events = new Events(eventDao);
    }

    @After
    public void tearDown() {
        Http.Context.current.remove();
    }

    @Test
    public void indexReturnsAllEventsWhenNoParameters() throws Exception {
        //TODO in order to test with querystring we need to mock play.api.mvc.RequestHeader.queryString() to return
        // Map<String, Seq<String>>
        Http.Context.current.set(new Http.Context((long) 1, mock(play.api.mvc.RequestHeader.class),
                mock(Http.Request.class), new HashMap<String, String>(), new HashMap<String, String>(),
                new HashMap<String, Object>()));
        // Arrange
        Event firstEvent = EventsTestHelper.createRandomEventtWithCategoryGivenSport(EventsTestHelper.FIRST_RANDOM_SPORT);
        Event secondEvent = EventsTestHelper.createRandomEventtWithCategoryGivenSport(EventsTestHelper.SECOND_RANDOM_SPORT);

        eventDao.create(firstEvent);
        eventDao.create(secondEvent);

        // Act
        Result result = events.index();
        String json = Helpers.contentAsString(result);
        JsonNode jsonNode = Json.parse(json);
        JsonNode event1 = jsonNode.get(0);
        JsonNode event2 = jsonNode.get(1);

        // Assert
        assertEquals(Helpers.OK, Helpers.status(result));
        assertEquals("application/json", Helpers.contentType(result));
        assertTrue(jsonNode.isArray());

        assertEquals(1, event1.get("id").asLong());
        assertEquals(firstEvent.getSport().getName(), event1.get("sport").get("name").asText());

        assertEquals(2, event2.get("id").asLong());
        assertEquals(secondEvent.getSport().getName(), event2.get("sport").get("name").asText());
    }

    @Test
    public void showReturnsAnEvent() {
        // Arrange
        Http.Context.current.set(new Http.Context((long) 1, mock(play.api.mvc.RequestHeader.class),
                mock(Http.Request.class), new HashMap<String, String>(), new HashMap<String, String>(),
                new HashMap<String, Object>()));
        Event firstEvent = EventsTestHelper.createRandomEventtWithCategoryGivenSport(EventsTestHelper.FIRST_RANDOM_SPORT);
        Event secondEvent = EventsTestHelper.createRandomEventtWithCategoryGivenSport(EventsTestHelper.SECOND_RANDOM_SPORT);

        eventDao.create(firstEvent);
        eventDao.create(secondEvent);

        // Act
        Result result = events.show(firstEvent.getId());
        assertEquals(Helpers.OK, Helpers.status(result));
        assertEquals("application/json", Helpers.contentType(result));

        String json = Helpers.contentAsString(result);
        JsonNode jsonNode = Json.parse(json);

        // Assert
        assertEquals(1, jsonNode.get("id").asLong());
    }
    // TODO: Add tests for different queries on index
}
