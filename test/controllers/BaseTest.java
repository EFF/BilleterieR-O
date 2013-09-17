package controllers;

import org.codehaus.jackson.JsonNode;
import org.junit.Before;
import play.GlobalSettings;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import settings.TestGlobal;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.*;

public abstract class BaseTest {
    protected GlobalSettings global;

    @Before
    public void setUp() {
        global = new TestGlobal();
    }

    protected JsonNode getFakeJsonResponseFrom(String url) {
        return getFakeJsonResponseFrom(url, Http.Status.OK);
    }

    protected JsonNode getFakeJsonResponseFrom(String url, int expectedStatusCode) {
        Result result = route(fakeRequest(GET, url));

        assertThat(status(result)).isEqualTo(expectedStatusCode);
        assertThat(contentType(result)).isEqualTo("application/json");

        String json = contentAsString(result);
        return Json.parse(json);
    }
}

