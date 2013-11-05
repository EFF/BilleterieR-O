package ca.ulaval.glo4003.unittests.services;

import ca.ulaval.glo4003.models.Transaction;
import ca.ulaval.glo4003.models.TransactionState;
import ca.ulaval.glo4003.services.CheckoutService;
import ca.ulaval.glo4003.services.ConcreteCheckoutService;
import org.fest.assertions.Assertions;
import org.joda.time.LocalDateTime;
import org.joda.time.Period;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class CheckoutServiceTest {

    private CheckoutService checkoutService;

    @Before
    public void setup() {
        this.checkoutService = new ConcreteCheckoutService();
    }

    @Test
    public void startingANewTransaction() {
        Transaction transaction = this.checkoutService.startNewTransaction();
        assertNotNull(transaction);
    }



}
