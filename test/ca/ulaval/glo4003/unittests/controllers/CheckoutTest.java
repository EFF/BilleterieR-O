package ca.ulaval.glo4003.unittests.controllers;

import ca.ulaval.glo4003.ConstantsManager;
import ca.ulaval.glo4003.controllers.Checkout;
import ca.ulaval.glo4003.dataaccessobjects.EventDao;
import ca.ulaval.glo4003.dataaccessobjects.UserDao;
import ca.ulaval.glo4003.exceptions.MaximumExceededException;
import ca.ulaval.glo4003.exceptions.RecordNotFoundException;
import ca.ulaval.glo4003.models.EventSearchCriteria;
import ca.ulaval.glo4003.models.Transaction;
import ca.ulaval.glo4003.models.User;
import ca.ulaval.glo4003.services.CheckoutService;
import ca.ulaval.glo4003.unittests.helpers.EventsTestHelper;
import com.google.inject.Inject;
import org.codehaus.jackson.node.ObjectNode;
import org.jukito.JukitoModule;
import org.jukito.JukitoRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import play.libs.Json;
import play.mvc.Result;
import play.test.Helpers;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.*;

@RunWith(JukitoRunner.class)
public class CheckoutTest extends BaseControllerTest {

    private static String USER_EMAIL = "user@email.com";
    private static Long USER_ID = 666L;

    @Inject
    private Checkout checkout;

    @Before
    public void setup(CheckoutService mockedCheckoutSvc, UserDao userDao) throws RecordNotFoundException, MaximumExceededException {
        User user = new User();
        user.setId(USER_ID);
        user.setEmail(USER_EMAIL);

        when(mockedSession.get(ConstantsManager.COOKIE_SESSION_FIELD_NAME)).thenReturn(String.valueOf(USER_EMAIL));
        when(userDao.read(USER_ID)).thenReturn(user);
        when(mockedCheckoutSvc.startNewTransaction(user)).thenReturn(new Transaction(user));
    }

    @Test
    public void checkoutDoNothingWhenThereIsNoItems(EventDao mockedEventDao) throws RecordNotFoundException, MaximumExceededException {
        doThrow(new RecordNotFoundException()).when(mockedEventDao).decrementEventCategoryNumberOfTickets(anyLong(), anyLong(), anyInt());

        Result result = checkout.index();

        assertEquals(Helpers.OK, Helpers.status(result));
        verify(mockedEventDao, never()).decrementEventCategoryNumberOfTickets(anyLong(), anyLong(), anyInt());
    }

    @Test
    public void checkoutWithSeveralItems(EventDao mockedEventDao, CheckoutService mockedCheckoutSvc) throws RecordNotFoundException, MaximumExceededException {
        long eventId1 = 1;
        long categoryId1 = 1;
        int quantity1 = 10;
        long eventId2 = eventId1 + 1;
        long categoryId2 = categoryId1 + 1;
        int quantity2 = quantity1 * 2;

        ObjectNode jsonMaster = Json.newObject();
        ObjectNode json1 = Json.newObject();
        ObjectNode json2 = Json.newObject();
        json1.put(ConstantsManager.EVENT_ID_FIELD_NAME, eventId1);
        json1.put(ConstantsManager.CATEGORY_ID_FIELD_NAME, categoryId1);
        json1.put(ConstantsManager.QUANTITY_FIELD_NAME, quantity1);
        jsonMaster.put("1", json1);
        doNothing().when(mockedEventDao).decrementEventCategoryNumberOfTickets(eventId1, categoryId1, quantity1);
        json2.put(ConstantsManager.EVENT_ID_FIELD_NAME, eventId2);
        json2.put(ConstantsManager.CATEGORY_ID_FIELD_NAME, categoryId2);
        json2.put(ConstantsManager.QUANTITY_FIELD_NAME, quantity2);
        jsonMaster.put("2", json2);
        doNothing().when(mockedEventDao).decrementEventCategoryNumberOfTickets(eventId2, categoryId2, quantity2);

        when(mockedBody.asJson()).thenReturn(jsonMaster);

        Result result = checkout.index();

        assertEquals(Helpers.OK, Helpers.status(result));
        verify(mockedEventDao, times(2)).decrementEventCategoryNumberOfTickets(anyLong(), anyLong(), anyInt());
        verify(mockedEventDao).decrementEventCategoryNumberOfTickets(eventId1, categoryId1, quantity1);
        verify(mockedEventDao).decrementEventCategoryNumberOfTickets(eventId2, categoryId2, quantity2);
        verify(mockedCheckoutSvc).fulfillTransaction(any(Transaction.class));
    }

    @Test
    public void checkoutWhenMaximumIsExceeded(EventDao mockedEventDao, CheckoutService mockedCheckoutSvc) throws RecordNotFoundException, MaximumExceededException {
        long eventId1 = 1;
        long categoryId1 = 1;
        int quantity1 = 10;

        ObjectNode jsonMaster = Json.newObject();
        ObjectNode json1 = Json.newObject();
        json1.put(ConstantsManager.EVENT_ID_FIELD_NAME, eventId1);
        json1.put(ConstantsManager.CATEGORY_ID_FIELD_NAME, categoryId1);
        json1.put(ConstantsManager.QUANTITY_FIELD_NAME, quantity1);
        jsonMaster.put("1", json1);
        doThrow(new MaximumExceededException()).when(mockedEventDao).decrementEventCategoryNumberOfTickets(eventId1, categoryId1, quantity1);

        when(mockedBody.asJson()).thenReturn(jsonMaster);

        Result result = checkout.index();

        assertEquals(Helpers.BAD_REQUEST, Helpers.status(result));
        verify(mockedEventDao).decrementEventCategoryNumberOfTickets(anyLong(), anyLong(), anyInt());
        verify(mockedEventDao).decrementEventCategoryNumberOfTickets(eventId1, categoryId1, quantity1);
        verify(mockedCheckoutSvc, never()).fulfillTransaction(any(Transaction.class));
    }

    @Test
    public void checkoutWhenRecordNotFoundExceptionIsThrown(EventDao mockedEventDao, CheckoutService mockedCheckoutSvc) throws RecordNotFoundException, MaximumExceededException {
        reset(mockedEventDao);
        long eventId1 = 1;
        long categoryId1 = 1;
        int quantity1 = 10;

        ObjectNode jsonMaster = Json.newObject();
        ObjectNode json1 = Json.newObject();
        json1.put(ConstantsManager.EVENT_ID_FIELD_NAME, eventId1);
        json1.put(ConstantsManager.CATEGORY_ID_FIELD_NAME, categoryId1);
        json1.put(ConstantsManager.QUANTITY_FIELD_NAME, quantity1);
        jsonMaster.put("1", json1);
        doThrow(new RecordNotFoundException()).when(mockedEventDao).decrementEventCategoryNumberOfTickets(eventId1, categoryId1, quantity1);

        when(mockedBody.asJson()).thenReturn(jsonMaster);

        Result result = checkout.index();

        assertEquals(Helpers.NOT_FOUND, Helpers.status(result));
        verify(mockedEventDao).decrementEventCategoryNumberOfTickets(anyLong(), anyLong(), anyInt());
        verify(mockedEventDao).decrementEventCategoryNumberOfTickets(eventId1, categoryId1, quantity1);
        verify(mockedCheckoutSvc, never()).fulfillTransaction(any(Transaction.class));
    }

    public static class TestModule extends JukitoModule {

        @Override
        protected void configureTest() {
            forceMock(EventDao.class);
        }
    }

}
