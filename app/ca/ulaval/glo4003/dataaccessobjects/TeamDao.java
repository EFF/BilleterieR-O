package ca.ulaval.glo4003.dataaccessobjects;


import ca.ulaval.glo4003.exceptions.RecordNotFoundException;
import ca.ulaval.glo4003.models.Team;
import ca.ulaval.glo4003.services.DaoPersistenceService;
import com.google.inject.Inject;

public class TeamDao extends PersistedDao<Team> {

    @Inject
    public TeamDao(DaoPersistenceService persistenceService, UniqueConstraintValidator<Team>
            uniqueConstraintValidator){
        super(persistenceService, uniqueConstraintValidator);
    }

    public Team read(String name) throws RecordNotFoundException {
        for(Team team : super.list()){
            if(team.getName().equals(name)){
                return team;
            }
        }
        throw new RecordNotFoundException();
    }
}
