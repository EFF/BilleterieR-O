package ca.ulaval.glo4003.dataaccessobjects;

import ca.ulaval.glo4003.models.Transaction;
import ca.ulaval.glo4003.services.DaoPersistenceService;

public class TransactionDao extends PersistedDao<Transaction>{

    public TransactionDao(DaoPersistenceService persistenceService) {
        super(persistenceService);
    }

}
