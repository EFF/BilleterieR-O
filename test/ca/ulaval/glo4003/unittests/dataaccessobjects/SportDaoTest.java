package ca.ulaval.glo4003.unittests.dataaccessobjects;

import ca.ulaval.glo4003.dataaccessobjects.SportDao;
import ca.ulaval.glo4003.exceptions.RecordNotFoundException;
import ca.ulaval.glo4003.models.Sport;
import ca.ulaval.glo4003.services.DaoPersistenceService;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class SportDaoTest {

    private DaoPersistenceService daoPersistenceService;

    public void setUp() {
        daoPersistenceService = mock(DaoPersistenceService.class);
    }

    @Test
    public void readFirstSportWhenDaoContainsOneSport() throws RecordNotFoundException {
        Sport sport = new Sport("test");

        SportDao sportDao = new SportDao(daoPersistenceService);
        sportDao.create(sport);

        Sport result = sportDao.read(1L);

        assertNotNull(result);
        assertEquals(sport.getName(), result.getName());
    }
}
