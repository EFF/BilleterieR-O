package ca.ulaval.glo4003.persistence.event;


import ca.ulaval.glo4003.persistence.PersistedDao;
import ca.ulaval.glo4003.domain.event.TeamDao;
import ca.ulaval.glo4003.persistence.UniqueConstraintValidator;
import ca.ulaval.glo4003.domain.RecordNotFoundException;
import ca.ulaval.glo4003.domain.event.Team;
import ca.ulaval.glo4003.persistence.DaoPersistenceService;
import com.google.inject.Inject;

public class PersistedTeamDao extends PersistedDao<Team> implements TeamDao {

    @Inject
    public PersistedTeamDao(DaoPersistenceService persistenceService, UniqueConstraintValidator<Team>
            uniqueConstraintValidator) {
        super(persistenceService, uniqueConstraintValidator);
    }

    @Override
    public Team findByName(String name) throws RecordNotFoundException {
        for (Team team : super.list()) {
            if (team.getName().equals(name)) {
                return team;
            }
        }
        throw new RecordNotFoundException();
    }
}
