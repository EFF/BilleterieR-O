package ca.ulaval.glo4003.dataaccessobjects;


import ca.ulaval.glo4003.exceptions.RecordNotFoundException;
import ca.ulaval.glo4003.models.Team;
import ca.ulaval.glo4003.services.DaoPersistenceService;

public class TeamDao extends PersistedDao<Team>{
    public TeamDao(DaoPersistenceService persistenceService){
        super(persistenceService);
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
