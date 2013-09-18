package controllers;

import org.codehaus.jackson.JsonNode;
import org.junit.*;
import static play.test.Helpers.*;
import static org.fest.assertions.Assertions.*;

public class HelloWorldTest  extends BaseTest {
	@Test
	public void indexWithName() {
		running(fakeApplication(), new Runnable() {
			public void run() {
				JsonNode jsonNode = getFakeJsonResponseFrom("/?name=Jack");
				
				assertThat(jsonNode.get("message").asText()).isEqualTo(
						"Hello Jack!");
			}
		});
	}
	
	@Test
	public void indexWithDefaultValue() {
		running(fakeApplication(), new Runnable() {
			public void run() {
				JsonNode jsonNode = getFakeJsonResponseFrom("/");
				
				assertThat(jsonNode.get("message").asText()).isEqualTo(
						"Hello world!");
			}
		});
	}
}
