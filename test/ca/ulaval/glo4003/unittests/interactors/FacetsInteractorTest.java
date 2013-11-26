package ca.ulaval.glo4003.unittests.interactors;

import ca.ulaval.glo4003.dataaccessobjects.SportDao;
import ca.ulaval.glo4003.interactors.FacetsInteractor;
import ca.ulaval.glo4003.models.Gender;
import ca.ulaval.glo4003.models.Sport;
import com.google.inject.Inject;
import org.jukito.JukitoModule;
import org.jukito.JukitoRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(JukitoRunner.class)
public class FacetsInteractorTest {

    @Inject
    private FacetsInteractor facetsInteractor;

    @Test
    public void sportsFacet(SportDao mockedSportDao) {
        List<Sport> emptySportList = new ArrayList<>();
        when(mockedSportDao.list()).thenReturn(emptySportList);

        List<Sport> result = facetsInteractor.sports();

        assertEquals(emptySportList, result);
        verify(mockedSportDao, times(1)).list();
    }

    @Test
    public void gendersFacet() {
        List<Gender> result = facetsInteractor.genders();

        assertEquals(2, result.size());
    }

    public static class TestModule extends JukitoModule {

        @Override
        protected void configureTest() {
            forceMock(SportDao.class);
        }
    }
}
