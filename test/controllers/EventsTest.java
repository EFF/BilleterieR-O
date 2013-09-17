package controllers;

import org.codehaus.jackson.JsonNode;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;

public class EventsTest extends BaseTest {

	@Test
    public void indexReturnsAllEvents() {
        running(fakeApplication(global), new Runnable() {
            public void run() {
                JsonNode jsonNode = getFakeJsonResponseFrom("/events");

                assertThat(jsonNode.isArray());

                JsonNode event1 = jsonNode.get(0);
                assertThat(event1.get("id").asLong()).isEqualTo(1);
                assertThat(event1.get("totalNumberOfTickets").asInt()).isEqualTo(1320);

                JsonNode event2 = jsonNode.get(1);
                assertThat(event2.get("id").asLong()).isEqualTo(2);
                assertThat(event2.get("totalNumberOfTickets").asInt()).isEqualTo(1320);
            }
        });
    }

    @Test
    public void showReturnsEvent() {
        running(fakeApplication(global), new Runnable() {
            public void run() {
                JsonNode jsonNode = getFakeJsonResponseFrom("/events/1");

                assertThat(jsonNode.get("id").asLong()).isEqualTo(1);
                assertThat(jsonNode.get("categories").isArray());

                JsonNode category1 = jsonNode.get("categories").get(0);
                assertThat(category1.get("id").asLong()).isEqualTo(1);
                assertThat(category1.get("price").asDouble()).isEqualTo(12.0);
                assertThat(category1.get("numberOfTickets").asInt()).isEqualTo(120);

                JsonNode category2 = jsonNode.get("categories").get(1);
                assertThat(category2.get("id").asLong()).isEqualTo(2);
                assertThat(category2.get("price").asDouble()).isEqualTo(8.0);
                assertThat(category2.get("numberOfTickets").asInt()).isEqualTo(1200);
            }
        });
    }
}
