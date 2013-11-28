package ca.ulaval.glo4003.unittests.persistence.event;

import ca.ulaval.glo4003.persistence.event.PersistedTeamDao;
import ca.ulaval.glo4003.domain.event.TeamDao;
import ca.ulaval.glo4003.persistence.UniqueConstraintValidator;
import ca.ulaval.glo4003.domain.RecordNotFoundException;
import ca.ulaval.glo4003.domain.event.Team;
import ca.ulaval.glo4003.persistence.DaoPersistenceService;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

public class PersistedTeamDaoTest {
    private static final String A_TEAM_NAME = "Rouge et Or";
    private DaoPersistenceService daoPersistenceService;

    public void setup() {
        daoPersistenceService = mock(DaoPersistenceService.class);
    }

    @Test
    public void readExistingTeam() throws RecordNotFoundException {
        Team team = new Team(A_TEAM_NAME);
        PersistedTeamDao teamDao = new PersistedTeamDao(daoPersistenceService,
                new UniqueConstraintValidator<Team>());
        teamDao.create(team);

        Team result = teamDao.findByName(A_TEAM_NAME);
        assertNotNull(result);
        assertEquals(result.getName(),team.getName());
    }

    @Test(expected = RecordNotFoundException.class)
    public void readNotExistingTeamThrowsRecordNotFound() throws RecordNotFoundException {
        TeamDao teamDao = new PersistedTeamDao(daoPersistenceService,
                new UniqueConstraintValidator<Team>());

        teamDao.findByName(A_TEAM_NAME);
    }
}
