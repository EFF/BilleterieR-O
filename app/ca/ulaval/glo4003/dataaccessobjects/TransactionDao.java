package ca.ulaval.glo4003.dataaccessobjects;

import ca.ulaval.glo4003.models.Transaction;
import ca.ulaval.glo4003.services.DaoPersistenceService;
import com.google.inject.Inject;

public class TransactionDao extends PersistedDao<Transaction> {

    @Inject
    public TransactionDao(DaoPersistenceService persistenceService, UniqueConstraintValidator<Transaction>
            uniqueConstraintValidator) {
        super(persistenceService, uniqueConstraintValidator);
    }

}
