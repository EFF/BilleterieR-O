package ca.ulaval.glo4003.unittests.controllers;

import ca.ulaval.glo4003.controllers.Facets;
import ca.ulaval.glo4003.dataaccessobjects.SportDao;
import ca.ulaval.glo4003.models.Gender;
import ca.ulaval.glo4003.models.Sport;
import com.google.inject.Inject;
import org.codehaus.jackson.JsonNode;
import org.jukito.JukitoRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import play.libs.Json;
import play.mvc.Result;
import play.test.Helpers;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JukitoRunner.class)
public class FacetsTest extends BaseControllerTest {

    @Inject
    private Facets facets;

    private List<Sport> tempList;

    @Before
    public void setup(SportDao mockedSportDao) {
        // Cannot use mocks here because Json.toJson on mocks failed
        Sport sport = new Sport("golf");
        Sport sport1 = new Sport("soccer");

        tempList = new ArrayList<>();
        tempList.add(sport);
        tempList.add(sport1);

        when(mockedSportDao.list()).thenReturn(tempList);
    }

    @Test
    public void indexReturnsAllFacets(SportDao mockedSportDao) throws Exception {
        Result result = facets.index();
        String json = Helpers.contentAsString(result);
        JsonNode jsonNode = Json.parse(json);

        // Assert
        assertEquals(Helpers.OK, Helpers.status(result));
        assertEquals("application/json", Helpers.contentType(result));

        assertTrue(jsonNode.get("gender").isArray());
        assertNotSame(Gender.values(), jsonNode.get("gender"));
        assertEquals(2, jsonNode.get("gender").size());

        assertTrue(jsonNode.get("sport").isArray());

        //Json.toJson always create new Objects.
        assertNotSame(tempList, jsonNode.get("sport"));
        assertEquals(tempList.size(), jsonNode.get("sport").size());

        verify(mockedSportDao).list();
    }
}

