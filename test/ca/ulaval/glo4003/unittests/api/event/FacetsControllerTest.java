package ca.ulaval.glo4003.unittests.api.event;

import ca.ulaval.glo4003.ConstantsManager;
import ca.ulaval.glo4003.api.event.FacetsController;
import ca.ulaval.glo4003.domain.event.FacetsInteractor;
import ca.ulaval.glo4003.domain.event.Gender;
import ca.ulaval.glo4003.domain.event.Sport;
import ca.ulaval.glo4003.unittests.api.BaseControllerTest;
import com.google.inject.Inject;
import org.codehaus.jackson.JsonNode;
import org.jukito.JukitoModule;
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
public class FacetsControllerTest extends BaseControllerTest {

    @Inject
    private FacetsController facetsController;
    private List<Sport> tempList;

    @Before
    public void setup(FacetsInteractor mockedFacetsInteractor) {
        // Cannot use mocks here because Json.toJson on mocks failed
        Sport sport = new Sport("golf");
        Sport sport1 = new Sport("soccer");

        tempList = new ArrayList<>();
        tempList.add(sport);
        tempList.add(sport1);

        when(mockedFacetsInteractor.sports()).thenReturn(tempList);
        when(mockedFacetsInteractor.genders()).thenCallRealMethod();
    }

    @Test
    public void indexReturnsAllFacets(FacetsInteractor mockedFacetsInteractor) throws Exception {
        Result result = facetsController.index();
        String json = Helpers.contentAsString(result);
        JsonNode jsonNode = Json.parse(json);

        // Assert
        assertEquals(Helpers.OK, Helpers.status(result));
        assertEquals("application/json", Helpers.contentType(result));

        assertTrue(jsonNode.get(ConstantsManager.FACET_GENDER).isArray());
        assertNotSame(Gender.values(), jsonNode.get(ConstantsManager.FACET_GENDER));
        assertEquals(2, jsonNode.get(ConstantsManager.FACET_GENDER).size());

        assertTrue(jsonNode.get(ConstantsManager.FACET_SPORT).isArray());

        //Json.toJson always create new Objects.
        assertNotSame(tempList, jsonNode.get(ConstantsManager.FACET_SPORT));
        assertEquals(tempList.size(), jsonNode.get(ConstantsManager.FACET_SPORT).size());

        verify(mockedFacetsInteractor).sports();
        verify(mockedFacetsInteractor).genders();
    }

    public static class TestModule extends JukitoModule {

        @Override
        protected void configureTest() {
            forceMock(FacetsInteractor.class);
        }
    }
}

