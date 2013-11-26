package ca.ulaval.glo4003.domain.ticketing;

import ca.ulaval.glo4003.ConstantsManager;
import ca.ulaval.glo4003.domain.RecordNotFoundException;
import ca.ulaval.glo4003.domain.user.User;
import ca.ulaval.glo4003.domain.EmailService;
import com.google.inject.Inject;

import java.util.List;

public class CheckoutInteractor {

    private final TicketsInteractor ticketsInteractor;
    private final EmailService emailService;
    private final TransactionFactory transactionFactory;
    private final TransactionDao transactionDao;

    @Inject
    public CheckoutInteractor(TicketsInteractor ticketsInteractor, TransactionDao transactionDao,
                              EmailService emailService, TransactionFactory transactionFactory) {
        this.transactionDao = transactionDao;
        this.ticketsInteractor = ticketsInteractor;
        this.emailService = emailService;
        this.transactionFactory = transactionFactory;
    }

    public Transaction executeTransaction(User buyer, List<Long> ticketIds) {
        Transaction transaction = transactionFactory.createTransaction(buyer);
        transactionDao.create(transaction);

        try {
            for (Long ticketId : ticketIds) {
                ticketsInteractor.buyATicket(ticketId);
            }
            transaction.fulfill();
            transactionDao.update(transaction);
            emailService.sendSystemEmail(transaction.getUser().getEmail(),
                    ConstantsManager.CHECKOUT_CONFIRMATION_EMAIL + transaction.getId());
        } catch (RecordNotFoundException | UpdateTicketStateUnauthorizedException ignored) {
            transaction.fail();
        }
        return transaction;
    }

}
