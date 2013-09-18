package ca.ulaval.glo4003.controllers;

import org.codehaus.jackson.JsonNode;
import org.junit.*;

public class HelloWorldTest  extends BaseTest {
	@Test
	public void indexWithName() {
		Helpers.running(Helpers.fakeApplication(), new Runnable() {
            public void run() {
                JsonNode jsonNode = getFakeJsonResponseFrom("/?name=Jack");

                Assertions.assertThat(jsonNode.get("message").asText()).isEqualTo(
                        "Hello Jack!");
            }
        });
	}
	
	@Test
	public void indexWithDefaultValue() {
		Helpers.running(Helpers.fakeApplication(), new Runnable() {
            public void run() {
                JsonNode jsonNode = getFakeJsonResponseFrom("/");

                Assertions.assertThat(jsonNode.get("message").asText()).isEqualTo(
                        "Hello world!");
            }
        });
	}
}
