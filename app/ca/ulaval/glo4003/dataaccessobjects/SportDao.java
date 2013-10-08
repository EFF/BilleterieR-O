package ca.ulaval.glo4003.dataaccessobjects;

import ca.ulaval.glo4003.models.Sport;
import ca.ulaval.glo4003.services.DaoPersistenceService;

public class SportDao extends PersistedDao<Sport> {

    public SportDao(DaoPersistenceService persistenceService) {
        super(persistenceService);
    }

}
