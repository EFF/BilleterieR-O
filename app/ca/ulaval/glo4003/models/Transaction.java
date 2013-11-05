package ca.ulaval.glo4003.models;

import org.joda.time.LocalDateTime;

import java.util.UUID;

public class Transaction {

    private LocalDateTime startedOn;

    private LocalDateTime endedOn;

    private TransactionState state;

    private UUID id;

    public Transaction() {
        this.startedOn = new LocalDateTime();
        this.state = TransactionState.Unfulfilled;
        this.id = UUID.randomUUID();
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

    public UUID getId() {
        return id;
    }

    public LocalDateTime getEndedOn() {
        return endedOn;
    }
}
