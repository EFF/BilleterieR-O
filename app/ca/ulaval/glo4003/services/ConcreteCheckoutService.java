package ca.ulaval.glo4003.services;

import ca.ulaval.glo4003.dataaccessobjects.TransactionDao;
import ca.ulaval.glo4003.models.Transaction;

public class ConcreteCheckoutService implements CheckoutService {

    private TransactionDao transactionDao;

    public ConcreteCheckoutService(TransactionDao transactionDao) {
        this.transactionDao = transactionDao;
    }

    @Override
    public Transaction startNewTransaction(long userId) {
        Transaction transaction = new Transaction(userId);

        this.transactionDao.create(transaction);

        return transaction;
    }

    @Override
    public void fulfillTransaction(Transaction transaction) {
        transaction.fulfill();
        // TODO Send email
    }
}
