package ca.ulaval.glo4003.domain.event;

import ca.ulaval.glo4003.domain.Dao;
import ca.ulaval.glo4003.domain.RecordNotFoundException;

public interface TeamDao extends Dao<Team> {

    Team findByName(String name) throws RecordNotFoundException;
}
