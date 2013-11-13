package ca.ulaval.glo4003.models;

import org.joda.time.LocalDateTime;

public class Transaction extends Record {

    private User user;

    private LocalDateTime startedOn;

    private LocalDateTime endedOn;

    private TransactionState state;

    public Transaction(User user) {
        this.startedOn = new LocalDateTime();
        this.state = TransactionState.Unfulfilled;
        this.user = user;
    }

    public void fulfill() {
        this.state = TransactionState.Fulfilled;
        this.endedOn = new LocalDateTime();
    }

    public void fail() {
        this.state = TransactionState.Failed;
        this.endedOn = new LocalDateTime();
    }

    public LocalDateTime getStartedOn() {
        return startedOn;
    }

    public TransactionState getState() {
        return state;
    }

    public LocalDateTime getEndedOn() {
        return endedOn;
    }

    public User getUser() {
        return user;
    }
}
