package ca.ulaval.glo4003.dataaccessobjects;

import ca.ulaval.glo4003.exceptions.RecordNotFoundException;
import ca.ulaval.glo4003.models.Team;

public interface TeamDao extends DataAccessObject<Team> {

    Team findByName(String name) throws RecordNotFoundException;
}
