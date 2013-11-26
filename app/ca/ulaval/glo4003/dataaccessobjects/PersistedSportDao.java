package ca.ulaval.glo4003.dataaccessobjects;

import ca.ulaval.glo4003.models.Sport;
import ca.ulaval.glo4003.services.DaoPersistenceService;

import javax.inject.Inject;

public class PersistedSportDao extends PersistedDao<Sport> implements SportDao {

    @Inject
    public PersistedSportDao(DaoPersistenceService persistenceService, UniqueConstraintValidator<Sport>
            uniqueConstraintValidator) {
        super(persistenceService, uniqueConstraintValidator);
    }
}
