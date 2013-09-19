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

public class EventsTest{

    private EventDao eventDao;
    private Events events;

    private static final long EXISTING_EVENT_ID = 1;

    @Before
    public void setUp(){
        eventDao = new EventDaoInMemory();
        events = new Events(eventDao);

        Sport soccer = new Sport(1, "Soccer");
        Event event1 = new Event(1, soccer, Gender.MALE);
        Category category1 = new Category(1, 12.0, 120);
        Category category2 = new Category(2, 8.0, 1200);

        event1.addCategory(category1);
        event1.addCategory(category2);
        eventDao.create(event1);

        Event event2 = new Event(2, soccer, Gender.FEMALE);
        Category category3 = new Category(3, 12.0, 120);
        Category category4 = new Category(4, 8.0, 1200);

        event2.addCategory(category3);
        event2.addCategory(category4);

        eventDao.create(event2);
    }

    @Test
    public void indexReturnsAllEvents() {
        Result result = events.index();
        Assertions.assertThat(Helpers.status(result)).isEqualTo(Helpers.OK);
        Assertions.assertThat(Helpers.contentType(result)).isEqualTo("application/json");

        String json = Helpers.contentAsString(result);
        JsonNode jsonNode = Json.parse(json);

        Assertions.assertThat(jsonNode.isArray());

        JsonNode event1 = jsonNode.get(0);
        Assertions.assertThat(event1.get("id").asLong()).isEqualTo(1);
        Assertions.assertThat(event1.get("totalNumberOfTickets").asInt()).isEqualTo(1320);

        JsonNode event2 = jsonNode.get(1);
        Assertions.assertThat(event2.get("id").asLong()).isEqualTo(2);
        Assertions.assertThat(event2.get("totalNumberOfTickets").asInt()).isEqualTo(1320);

    }

    @Test
    public void showReturnsEvent() {
        Result result = events.show(EXISTING_EVENT_ID);
        Assertions.assertThat(Helpers.status(result)).isEqualTo(Helpers.OK);
        Assertions.assertThat(Helpers.contentType(result)).isEqualTo("application/json");

        String json = Helpers.contentAsString(result);
        System.out.println(json);
        JsonNode jsonNode = Json.parse(json);

        Assertions.assertThat(jsonNode.get("id").asLong()).isEqualTo(1);
        Assertions.assertThat(jsonNode.get("categories").isArray());

        JsonNode category1 = jsonNode.get("categories").get(0);
        Assertions.assertThat(category1.get("id").asLong()).isEqualTo(1);
        Assertions.assertThat(category1.get("price").asDouble()).isEqualTo(12.0);
        Assertions.assertThat(category1.get("numberOfTickets").asInt()).isEqualTo(120);

        JsonNode category2 = jsonNode.get("categories").get(1);
        Assertions.assertThat(category2.get("id").asLong()).isEqualTo(2);
        Assertions.assertThat(category2.get("price").asDouble()).isEqualTo(8.0);
        Assertions.assertThat(category2.get("numberOfTickets").asInt()).isEqualTo(1200);
    }
}
