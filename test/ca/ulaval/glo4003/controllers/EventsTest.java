package ca.ulaval.glo4003.controllers;

import org.codehaus.jackson.JsonNode;
import org.junit.Test;

public class EventsTest extends BaseTest {

    @Test
    public void indexReturnsAllEvents() {
        Helpers.running(Helpers.fakeApplication(global), new Runnable() {
            public void run() {
                JsonNode jsonNode = getFakeJsonResponseFrom("/events");

                Assertions.assertThat(jsonNode.isArray());

                JsonNode event1 = jsonNode.get(0);
                Assertions.assertThat(event1.get("id").asLong()).isEqualTo(1);
                Assertions.assertThat(event1.get("totalNumberOfTickets").asInt()).isEqualTo(1320);

                JsonNode event2 = jsonNode.get(1);
                Assertions.assertThat(event2.get("id").asLong()).isEqualTo(2);
                Assertions.assertThat(event2.get("totalNumberOfTickets").asInt()).isEqualTo(1320);
            }
        });
    }

    @Test
    public void showReturnsEvent() {
        Helpers.running(Helpers.fakeApplication(global), new Runnable() {
            public void run() {
                JsonNode jsonNode = getFakeJsonResponseFrom("/events/1");

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
        });
    }
}
