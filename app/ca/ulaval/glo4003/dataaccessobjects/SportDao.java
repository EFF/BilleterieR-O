package ca.ulaval.glo4003.dataaccessobjects;

import ca.ulaval.glo4003.models.Sport;
import ca.ulaval.glo4003.services.DaoPersistenceService;
import com.google.inject.Inject;

public class SportDao extends PersistedDao<Sport> {

    // Even if empty, needed with Guice.
    @Inject
    public SportDao(DaoPersistenceService persistenceService, UniqueConstraintValidator<Sport>
            uniqueConstraintValidator) {
        super(persistenceService, uniqueConstraintValidator);
    }
}
