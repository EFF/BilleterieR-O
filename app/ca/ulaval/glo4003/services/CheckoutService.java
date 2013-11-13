package ca.ulaval.glo4003.services;

import ca.ulaval.glo4003.models.Transaction;

public interface CheckoutService {

    // TODO Not required in the scope of that story
    //void freezeTickets(Long eventId, Long categoryId, int quantity);

    Transaction startNewTransaction(long userId);

    void fulfillTransaction(Transaction transaction);
}
