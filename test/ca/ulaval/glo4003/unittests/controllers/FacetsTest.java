package ca.ulaval.glo4003.unittests.controllers;

import ca.ulaval.glo4003.controllers.Facets;
import ca.ulaval.glo4003.dataaccessobjects.SportDao;
import ca.ulaval.glo4003.dataaccessobjects.SportDaoInMemory;
import ca.ulaval.glo4003.models.Sport;
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

public class FacetsTest {

    private SportDao sportDao;
    private Facets facets;

    @Before
    public void setup() {
        sportDao = new SportDaoInMemory();
        facets = new Facets(sportDao);
    }

    @After
    public void tearDown() {
        Http.Context.current.remove();
    }

    @Test
    public void indexReturnsAllEventsWhenNoParameters() throws Exception {
        Http.Context.current.set(new Http.Context((long) 1, mock(play.api.mvc.RequestHeader.class),
                mock(Http.Request.class), new HashMap<String, String>(), new HashMap<String, String>(),
                new HashMap<String, Object>()));
        // Arrange
        Sport sport = new Sport("golf");
        Sport sport1 = new Sport("soccer");

        sportDao.create(sport);
        sportDao.create(sport1);

        Result result = facets.index();
        String json = Helpers.contentAsString(result);
        JsonNode jsonNode = Json.parse(json);

        // Assert
        assertEquals(Helpers.OK, Helpers.status(result));
        assertEquals("application/json", Helpers.contentType(result));

        assert(jsonNode.get("gender").isArray());
        assertEquals(2, jsonNode.get("gender").size());

        assert(jsonNode.get("sport").isArray());
        assertEquals(sportDao.list().size(), jsonNode.get("sport").size());
    }
}

