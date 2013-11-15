package ca.ulaval.glo4003.services;

import ca.ulaval.glo4003.ConstantsManager;
import ca.ulaval.glo4003.dataaccessobjects.TransactionDao;
import ca.ulaval.glo4003.models.Transaction;
import ca.ulaval.glo4003.models.User;
import com.google.inject.Inject;

public class ConcreteCheckoutService implements CheckoutService {

    private TransactionDao transactionDao;
    private EmailService emailService;

    @Inject
    public ConcreteCheckoutService(TransactionDao transactionDao, EmailService emailService) {
        this.transactionDao = transactionDao;
        this.emailService = emailService;
    }

    @Override
    public Transaction startNewTransaction(User user) {
        Transaction transaction = new Transaction(user);

        this.transactionDao.create(transaction);

        return transaction;
    }

    @Override
    public void fulfillTransaction(Transaction transaction) {
        transaction.fulfill();
        emailService.sendEmail(transaction.getUser().getEmail(), "noreply@glo4003.com",
                ConstantsManager.CHECKOUT_CONFIRMATION_EMAIL + transaction.getId());
    }
}
