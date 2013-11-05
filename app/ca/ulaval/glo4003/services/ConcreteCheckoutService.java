package ca.ulaval.glo4003.services;

import ca.ulaval.glo4003.models.Transaction;

public class ConcreteCheckoutService implements CheckoutService {
    @Override
    public void freezeTickets(Long eventId, Long categoryId, int quantity) {
        // TODO Not required in the scope of that story
    }

    @Override
    public Transaction startNewTransaction() {
        return new Transaction();
    }
}
