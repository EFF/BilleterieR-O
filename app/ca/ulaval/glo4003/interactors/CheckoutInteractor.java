package ca.ulaval.glo4003.interactors;

import ca.ulaval.glo4003.exceptions.RecordNotFoundException;
import ca.ulaval.glo4003.exceptions.UpdateTicketStateUnauthorizedException;
import ca.ulaval.glo4003.models.Transaction;
import ca.ulaval.glo4003.models.User;
import ca.ulaval.glo4003.services.CheckoutService;
import com.google.inject.Inject;

import java.util.List;

public class CheckoutInteractor {

    private final CheckoutService checkoutService;
    private final TicketsInteractor ticketsInteractor;

    @Inject
    public CheckoutInteractor(CheckoutService checkoutService, TicketsInteractor ticketsInteractor) {
        this.checkoutService = checkoutService;
        this.ticketsInteractor = ticketsInteractor;
    }

    public Transaction executeTransaction(User buyer, List<Long> ticketIds) {
        Transaction transaction = checkoutService.startNewTransaction(buyer);

        try {
            for (Long ticketId : ticketIds) {
                ticketsInteractor.buyATicket(ticketId);
            }
            checkoutService.fulfillTransaction(transaction);
        } catch (RecordNotFoundException | UpdateTicketStateUnauthorizedException ignored) {
            transaction.fail();
        }
        return transaction;
    }
}
