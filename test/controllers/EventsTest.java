package controllers;

import helpers.StatusCode;

import org.codehaus.jackson.JsonNode;
import org.junit.*;

import play.GlobalSettings;
import settings.TestGlobal;
import play.libs.Json;
import play.mvc.*;
import play.test.*;
import static play.test.Helpers.*;
import static org.fest.assertions.Assertions.*;

public class EventsTest {
	private GlobalSettings global;

	@Before
	public void setUp() {
		global = new TestGlobal();
	}

	@Test
	public void index() {
		running(fakeApplication(global), new Runnable() {
			public void run() {
				FakeRequest fakeRequest = fakeRequest("GET", "/events");
				Result result = callAction(
						controllers.routes.ref.Events.index(), fakeRequest);

				assertThat(status(result)).isEqualTo(StatusCode.OK);
				assertThat(contentType(result)).isEqualTo("application/json");

				String json = contentAsString(result);
				JsonNode jsonNode = Json.parse(json);

				assertThat(jsonNode.isArray());

				JsonNode event1 = jsonNode.get(0);
				assertThat(event1.get("id").asLong()).isEqualTo(1);
				assertThat(event1.get("totalNumberOfTickets").asInt())
						.isEqualTo(1320);

				JsonNode event2 = jsonNode.get(1);
				assertThat(event2.get("id").asLong()).isEqualTo(2);
				assertThat(event2.get("totalNumberOfTickets").asInt())
						.isEqualTo(1320);
			}
		});
	}

	@Test
	public void show() {
		running(fakeApplication(global), new Runnable() {
			public void run() {
				FakeRequest fakeRequest = fakeRequest("GET", "/events/1");
				Result result = callAction(
						controllers.routes.ref.Events.show(1), fakeRequest);

				assertThat(status(result)).isEqualTo(StatusCode.OK);
				assertThat(contentType(result)).isEqualTo("application/json");

				String json = contentAsString(result);
				JsonNode jsonNode = Json.parse(json);
				assertThat(jsonNode.get("id").asLong()).isEqualTo(1);
				assertThat(jsonNode.get("categories").isArray());
				JsonNode category1 = jsonNode.get("categories").get(0);
				assertThat(category1.get("id").asLong()).isEqualTo(1);
				assertThat(category1.get("price").asDouble()).isEqualTo(12.0);
				assertThat(category1.get("numberOfTickets").asInt()).isEqualTo(
						120);

				JsonNode category2 = jsonNode.get("categories").get(1);
				assertThat(category2.get("id").asLong()).isEqualTo(2);
				assertThat(category2.get("price").asDouble()).isEqualTo(8.0);
				assertThat(category2.get("numberOfTickets").asInt()).isEqualTo(
						1200);
			}
		});
	}
}
