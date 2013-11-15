package ca.ulaval.glo4003.services;

import ca.ulaval.glo4003.models.Transaction;
import ca.ulaval.glo4003.models.User;

public interface CheckoutService {

    // TODO Not required in the scope of that story
    //void freezeTickets(Long eventId, Long categoryId, int quantity);

    Transaction startNewTransaction(User userId);

    void fulfillTransaction(Transaction transaction);
}
