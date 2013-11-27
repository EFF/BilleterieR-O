package ca.ulaval.glo4003.persistence.event;

import ca.ulaval.glo4003.persistence.PersistedDao;
import ca.ulaval.glo4003.persistence.UniqueConstraintValidator;
import ca.ulaval.glo4003.domain.event.Sport;
import ca.ulaval.glo4003.persistence.DaoPersistenceService;
import ca.ulaval.glo4003.domain.event.SportDao;

import javax.inject.Inject;

public class PersistedSportDao extends PersistedDao<Sport> implements SportDao {

    @Inject
    public PersistedSportDao(DaoPersistenceService persistenceService, UniqueConstraintValidator<Sport>
            uniqueConstraintValidator) {
        super(persistenceService, uniqueConstraintValidator);
    }
}
