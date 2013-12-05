package ca.ulaval.glo4003.unittests.api.ticketing;

import ca.ulaval.glo4003.ConstantsManager;
import ca.ulaval.glo4003.api.ticketing.CheckoutController;
import ca.ulaval.glo4003.domain.RecordNotFoundException;
import ca.ulaval.glo4003.domain.ticketing.CheckoutInteractor;
import ca.ulaval.glo4003.domain.user.UsersInteractor;
import ca.ulaval.glo4003.domain.ticketing.Transaction;
import ca.ulaval.glo4003.domain.ticketing.TransactionState;
import ca.ulaval.glo4003.domain.user.User;
import ca.ulaval.glo4003.unittests.api.BaseControllerTest;
import com.google.inject.Inject;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.jukito.JukitoModule;
import org.jukito.JukitoRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.status;

@RunWith(JukitoRunner.class)
public class CheckoutControllerTest extends BaseControllerTest {

    private final static long TICKET_ID_1 = 1;
    private final static long TICKET_ID_2 = 123;
    private final static String EMAIL = "user1@example.com";
    private final static String PASSWORD = "secret";

    @Inject
    private CheckoutController checkoutController;

    @Test
    public void returnTransactionWhenTransactionIsFulfilled(CheckoutInteractor mockedCheckoutInteractor,
                                                            UsersInteractor mockedUsersInteractor) throws RecordNotFoundException {
        User fakeUser = getFakeUser();

        when(mockedRequest.username()).thenReturn(fakeUser.getEmail());
        when(mockedUsersInteractor.getByEmail(fakeUser.getEmail())).thenReturn(fakeUser);

        addJsonBody(TICKET_ID_1, TICKET_ID_2);

        List<Long> idsList = createFakeTicketIdsList(TICKET_ID_1, TICKET_ID_2);

        Transaction fakeTransaction = new Transaction(fakeUser);
        fakeTransaction.fulfill();
        when(mockedCheckoutInteractor.executeTransaction(fakeUser, idsList)).thenReturn(fakeTransaction);

        Result result = checkoutController.index();

        assertEquals(Http.Status.OK, status(result));
        JsonNode jsonResponse = Json.parse(contentAsString(result));
        assertTrue(jsonResponse.has("id"));
        assertTrue(jsonResponse.has("startedOn"));
        assertTrue(jsonResponse.has("endedOn"));
        assertEquals(TransactionState.Fulfilled.name(), jsonResponse.get("state").asText());
        assertEquals(fakeUser.getEmail(), jsonResponse.get("user").get("email").asText());
    }

    @Test
    public void returnTransactionWhenTransactionIsFailed(CheckoutInteractor mockedCheckoutInteractor,
                                                         UsersInteractor mockedUsersInteractor) throws RecordNotFoundException {
        User fakeUser = getFakeUser();

        when(mockedRequest.username()).thenReturn(fakeUser.getEmail());
        when(mockedUsersInteractor.getByEmail(fakeUser.getEmail())).thenReturn(fakeUser);

        addJsonBody(TICKET_ID_1, TICKET_ID_2);

        List<Long> idsList = createFakeTicketIdsList(TICKET_ID_1, TICKET_ID_2);

        Transaction fakeTransaction = new Transaction(fakeUser);
        fakeTransaction.fail();
        when(mockedCheckoutInteractor.executeTransaction(fakeUser, idsList)).thenReturn(fakeTransaction);

        Result result = checkoutController.index();

        assertEquals(Http.Status.OK, status(result));
        JsonNode jsonResponse = Json.parse(contentAsString(result));
        assertTrue(jsonResponse.has("id"));
        assertTrue(jsonResponse.has("startedOn"));
        assertTrue(jsonResponse.has("endedOn"));
        assertEquals(TransactionState.Failed.name(), jsonResponse.get("state").asText());
        assertEquals(fakeUser.getEmail(), jsonResponse.get("user").get("email").asText());
    }

    @Test
    public void returnNotFoundIfUserIsNotFound(CheckoutInteractor mockedCheckoutInteractor,
                                               UsersInteractor mockedUsersInteractor) throws RecordNotFoundException {
        User fakeUser = getFakeUser();

        when(mockedRequest.username()).thenReturn(fakeUser.getEmail());
        doThrow(new RecordNotFoundException()).when(mockedUsersInteractor).getByEmail(fakeUser.getEmail());

        addJsonBody(TICKET_ID_1, TICKET_ID_2);

        Result result = checkoutController.index();

        assertEquals(Http.Status.NOT_FOUND, status(result));
    }

    @Test
    public void returnInternalServerErrorIfTheTransactionIsNotFulfilledNorFailed(CheckoutInteractor
                                                                                         mockedCheckoutInteractor,
                                                                                 UsersInteractor mockedUsersInteractor)
            throws RecordNotFoundException {
        User fakeUser = getFakeUser();

        when(mockedRequest.username()).thenReturn(fakeUser.getEmail());
        when(mockedUsersInteractor.getByEmail(fakeUser.getEmail())).thenReturn(fakeUser);

        addJsonBody(TICKET_ID_1, TICKET_ID_2);

        List<Long> idsList = createFakeTicketIdsList(TICKET_ID_1, TICKET_ID_2);

        Transaction fakeTransaction = new Transaction(fakeUser);
        when(mockedCheckoutInteractor.executeTransaction(fakeUser, idsList)).thenReturn(fakeTransaction);

        Result result = checkoutController.index();

        assertEquals(Http.Status.INTERNAL_SERVER_ERROR, status(result));
    }

    private List<Long> createFakeTicketIdsList(long id1, long id2) {
        List<Long> idsList = new ArrayList<>();
        idsList.add(id1);
        idsList.add(id2);
        return idsList;
    }

    private void addJsonBody(long id1, long id2) {
        ObjectNode jsonBody = JsonNodeFactory.instance.objectNode();
        ArrayNode ids = JsonNodeFactory.instance.arrayNode();
        ids.add(id1);
        ids.add(id2);
        jsonBody.put(ConstantsManager.TICKET_IDS_FIELD_NAME, ids);
        when(mockedBody.asJson()).thenReturn(jsonBody);
    }

    private User getFakeUser() {
        return new User(EMAIL, PASSWORD, false);
    }

    public static class TestModule extends JukitoModule {

        @Override
        protected void configureTest() {
            forceMock(CheckoutInteractor.class);
            forceMock(UsersInteractor.class);
        }
    }
}
