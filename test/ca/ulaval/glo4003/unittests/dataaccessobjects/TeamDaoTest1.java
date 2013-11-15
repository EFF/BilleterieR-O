package ca.ulaval.glo4003.unittests.dataaccessobjects;

import ca.ulaval.glo4003.dataaccessobjects.TeamDao;
import ca.ulaval.glo4003.dataaccessobjects.UniqueConstraintValidator;
import ca.ulaval.glo4003.exceptions.RecordNotFoundException;
import ca.ulaval.glo4003.models.Team;
import ca.ulaval.glo4003.services.DaoPersistenceService;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

public class TeamDaoTest1 {
    private static final String A_TEAM_NAME = "Rouge et Or";
    private DaoPersistenceService daoPersistenceService;

    public void setup() {
        daoPersistenceService = mock(DaoPersistenceService.class);
    }

    @Test
    public void readExistingTeam() throws RecordNotFoundException {
        Team team = new Team(A_TEAM_NAME);
        TeamDao teamDao = new TeamDao(daoPersistenceService,
                new UniqueConstraintValidator<Team>());
        teamDao.create(team);

        Team result = teamDao.read(A_TEAM_NAME);
        assertNotNull(result);
        assertEquals(result.getName(),team.getName());
    }

    @Test(expected = RecordNotFoundException.class)
    public void readNotExistingTeamThrowsRecordNotFound() throws RecordNotFoundException {
        TeamDao teamDao = new TeamDao(daoPersistenceService,
                new UniqueConstraintValidator<Team>());

        teamDao.read(A_TEAM_NAME);
    }
}
