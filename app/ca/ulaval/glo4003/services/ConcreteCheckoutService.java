package ca.ulaval.glo4003.services;

import ca.ulaval.glo4003.models.Transaction;

public class ConcreteCheckoutService implements CheckoutService {
    @Override
    public Transaction startNewTransaction() {
        return new Transaction();
    }

    @Override
    public void fulfillTransaction(Transaction transaction) {
        transaction.fulfill();
        // TODO Send email
    }
}
