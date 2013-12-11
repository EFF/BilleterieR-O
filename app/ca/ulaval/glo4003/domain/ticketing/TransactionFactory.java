package ca.ulaval.glo4003.domain.ticketing;

import ca.ulaval.glo4003.domain.user.User;

public class TransactionFactory {

    public Transaction createTransaction(User user) {
        return new Transaction(user);
    }
}
