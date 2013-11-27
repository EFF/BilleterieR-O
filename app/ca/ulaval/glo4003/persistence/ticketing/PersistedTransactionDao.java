package ca.ulaval.glo4003.persistence.ticketing;

import ca.ulaval.glo4003.persistence.PersistedDao;
import ca.ulaval.glo4003.domain.ticketing.TransactionDao;
import ca.ulaval.glo4003.persistence.UniqueConstraintValidator;
import ca.ulaval.glo4003.domain.ticketing.Transaction;
import ca.ulaval.glo4003.persistence.DaoPersistenceService;
import com.google.inject.Inject;

public class PersistedTransactionDao extends PersistedDao<Transaction> implements TransactionDao {

    @Inject
    public PersistedTransactionDao(DaoPersistenceService persistenceService, UniqueConstraintValidator<Transaction>
            uniqueConstraintValidator) {
        super(persistenceService, uniqueConstraintValidator);
    }
}
