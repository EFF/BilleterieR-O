package ca.ulaval.glo4003.controllers;

import org.codehaus.jackson.JsonNode;
import org.junit.Before;
import play.GlobalSettings;
import play.libs.Json;
import play.mvc.Result;
import ca.ulaval.glo4003.settings.TestGlobal;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.*;

public abstract class BaseTest {
    protected GlobalSettings global;

    @Before
    public void setUp() {
        global = new TestGlobal();
    }

    protected JsonNode getFakeJsonResponseFrom(String url) {
        return getFakeJsonResponseFrom(url, OK);
    }

    protected JsonNode getFakeJsonResponseFrom(String url, int expectedStatusCode) {
        Result result = Helpers.route(Helpers.fakeRequest(Helpers.GET, url));

        Assertions.assertThat(Helpers.status(result)).isEqualTo(expectedStatusCode);
        Assertions.assertThat(Helpers.contentType(result)).isEqualTo("application/json");

        String json = Helpers.contentAsString(result);
        return Json.parse(json);
    }
}

