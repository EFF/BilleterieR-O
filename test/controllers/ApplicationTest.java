package controllers;

import org.codehaus.jackson.JsonNode;
import org.junit.*;

import play.mvc.*;
import play.test.*;
import play.libs.Json;
import static play.test.Helpers.*;
import static org.fest.assertions.Assertions.*;

public class ApplicationTest {
	@Test
	public void index() {
		running(fakeApplication(), new Runnable() {
			public void run() {
				FakeRequest fakeRequest = fakeRequest("GET", "/?name=Jack");
				Result result = callAction(
						controllers.routes.ref.Application.index(), fakeRequest);

				assertThat(status(result)).isEqualTo(200);
				assertThat(contentType(result)).isEqualTo("application/json");

				String json = contentAsString(result);
				JsonNode jsonNode = Json.parse(json);
				assertThat(jsonNode.get("message").asText()).isEqualTo(
						"Hello Jack!");
			}
		});
	}
}
