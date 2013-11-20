package ca.ulaval.glo4003.unittests.controllers;

import ca.ulaval.glo4003.ConstantsManager;
import ca.ulaval.glo4003.controllers.CheckoutController;
import ca.ulaval.glo4003.controllers.TicketsController;
import ca.ulaval.glo4003.dataaccessobjects.EventDao;
import ca.ulaval.glo4003.dataaccessobjects.UserDao;
import ca.ulaval.glo4003.exceptions.RecordNotFoundException;
import ca.ulaval.glo4003.models.Transaction;
import ca.ulaval.glo4003.models.User;
import ca.ulaval.glo4003.services.CheckoutService;
import com.google.inject.Inject;
import org.jukito.JukitoModule;
import org.jukito.JukitoRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import play.mvc.Result;
import play.test.Helpers;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static play.mvc.Results.*;

@RunWith(JukitoRunner.class)
public class CheckoutControllerTest extends BaseControllerTest {

    private static String USER_EMAIL = "user@email.com";
    private static Long USER_ID = 666L;

    @Inject
    private CheckoutController checkoutController;

    @Before
    public void setup(CheckoutService mockedCheckoutSvc, UserDao userDao) throws RecordNotFoundException {
        User user = new User();
        user.setId(USER_ID);
        user.setEmail(USER_EMAIL);

        when(mockedSession.get(ConstantsManager.COOKIE_SESSION_FIELD_NAME)).thenReturn(String.valueOf(USER_EMAIL));
        when(userDao.read(USER_ID)).thenReturn(user);
        when(userDao.findByEmail(USER_EMAIL)).thenReturn(user);
        when(mockedCheckoutSvc.startNewTransaction(user)).thenReturn(new Transaction(user));
    }

    @Test
    public void checkoutWhenTicketsCheckoutReturnsNotFound(TicketsController mockedTicketsController, CheckoutService mockedCheckoutSvc) throws RecordNotFoundException {
        when(mockedTicketsController.checkout()).thenReturn(notFound());

        Result result = checkoutController.index();

        assertEquals(Helpers.NOT_FOUND, Helpers.status(result));
        verify(mockedTicketsController).checkout();
        verify(mockedCheckoutSvc, never()).fulfillTransaction(any(Transaction.class));
    }

    @Test
    public void checkoutWhenTicketsCheckoutReturnsOk(TicketsController mockedTicketsController, CheckoutService mockedCheckoutSvc) throws RecordNotFoundException {
        when(mockedTicketsController.checkout()).thenReturn(ok());

        Result result = checkoutController.index();

        assertEquals(Helpers.OK, Helpers.status(result));
        verify(mockedTicketsController).checkout();
        verify(mockedCheckoutSvc).fulfillTransaction(any(Transaction.class));
    }

    @Test
    public void checkoutWhenTicketsCheckoutReturnInternalServerError(TicketsController mockedTicketsController, CheckoutService mockedCheckoutSvc) throws RecordNotFoundException {
        when(mockedTicketsController.checkout()).thenReturn(internalServerError());

        Result result = checkoutController.index();

        assertEquals(Helpers.INTERNAL_SERVER_ERROR, Helpers.status(result));
        verify(mockedCheckoutSvc, never()).fulfillTransaction(any(Transaction.class));
    }

    @Test
    public void checkoutWhenRecordNotFoundExceptionIsThrown(UserDao mockedUserDao, CheckoutService mockedCheckoutSvc) throws RecordNotFoundException {
        doThrow(new RecordNotFoundException()).when(mockedUserDao).findByEmail(anyString());

        when(mockedSession.get(ConstantsManager.COOKIE_SESSION_FIELD_NAME)).thenReturn(USER_EMAIL);

        Result result = checkoutController.index();

        assertEquals(Helpers.NOT_FOUND, Helpers.status(result));
        verify(mockedUserDao).findByEmail(anyString());
        verify(mockedCheckoutSvc, never()).fulfillTransaction(any(Transaction.class));
    }

    public static class TestModule extends JukitoModule {

        @Override
        protected void configureTest() {
            forceMock(EventDao.class);
            forceMock(UserDao.class);
            forceMock(TicketsController.class);
        }
    }

}
