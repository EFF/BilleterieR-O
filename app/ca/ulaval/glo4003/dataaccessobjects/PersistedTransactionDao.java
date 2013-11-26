package ca.ulaval.glo4003.dataaccessobjects;

import ca.ulaval.glo4003.models.Transaction;
import ca.ulaval.glo4003.services.DaoPersistenceService;
import com.google.inject.Inject;

public class PersistedTransactionDao extends PersistedDao<Transaction> implements TransactionDao {

    @Inject
    public PersistedTransactionDao(DaoPersistenceService persistenceService, UniqueConstraintValidator<Transaction>
            uniqueConstraintValidator) {
        super(persistenceService, uniqueConstraintValidator);
    }
}
