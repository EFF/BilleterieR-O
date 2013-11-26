package ca.ulaval.glo4003.dataaccessobjects;


import ca.ulaval.glo4003.exceptions.RecordNotFoundException;
import ca.ulaval.glo4003.models.Team;
import ca.ulaval.glo4003.services.DaoPersistenceService;
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
