package ca.ulaval.glo4003.services;

import ca.ulaval.glo4003.models.Transaction;

public interface CheckoutService {

    void freezeTickets(Long eventId, Long categoryId, int quantity);

    Transaction startNewTransaction();
}
