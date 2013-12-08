package ca.ulaval.glo4003.unittests.api.ticketing;

import ca.ulaval.glo4003.api.ticketing.ApiTicketingConstantsManager;
import ca.ulaval.glo4003.api.ticketing.TicketsController;
import ca.ulaval.glo4003.domain.ticketing.MaximumExceededException;
import ca.ulaval.glo4003.domain.RecordNotFoundException;
import ca.ulaval.glo4003.domain.ticketing.UpdateTicketStateUnauthorizedException;
import ca.ulaval.glo4003.domain.ticketing.TicketsInteractor;
import ca.ulaval.glo4003.domain.ticketing.Ticket;
import ca.ulaval.glo4003.domain.ticketing.TicketSearchCriteria;
import ca.ulaval.glo4003.domain.ticketing.TicketState;
import ca.ulaval.glo4003.unittests.api.BaseControllerTest;
import com.google.inject.Inject;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.jukito.JukitoModule;
import org.jukito.JukitoRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import play.libs.Json;
import play.mvc.Result;
import play.test.Helpers;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.refEq;
import static org.mockito.Mockito.*;

@RunWith(JukitoRunner.class)
public class TicketsControllerTest extends BaseControllerTest {
    private static final long AN_EVENT_ID = 1;
    private static final long A_CATEGORY_ID = 1;
    @Inject
    private TicketsController ticketsController = null;

    @Test
    public void indexReturnsOnlyTicketCorrespondingToParameters(TicketsInteractor mockedTicketsInteractor) {
        TicketSearchCriteria ticketSearchCriteria = new TicketSearchCriteria();
        ticketSearchCriteria.setEventId(AN_EVENT_ID);
        ticketSearchCriteria.setCategoryId(A_CATEGORY_ID);
        ticketSearchCriteria.addState(TicketState.AVAILABLE);
        List<Ticket> tempFilteredTicketList = new ArrayList<>();
        tempFilteredTicketList.add(createFakeTicket());
        when(mockedTicketsInteractor.search(refEq(ticketSearchCriteria))).thenReturn(tempFilteredTicketList);

        when(mockedRequest.getQueryString(ApiTicketingConstantsManager.QUERY_STRING_EVENT_ID_PARAM_NAME)).thenReturn(String.valueOf(AN_EVENT_ID));
        when(mockedRequest.getQueryString(ApiTicketingConstantsManager.QUERY_STRING_CATEGORY_ID_PARAM_NAME)).thenReturn(String.valueOf(A_CATEGORY_ID));
        when(mockedRequest.getQueryString(ApiTicketingConstantsManager.QUERY_STRING_TICKET_STATE_PARAM_NAME)).thenReturn(String.valueOf(TicketState.AVAILABLE));
        when(mockedRequest.getQueryString(ApiTicketingConstantsManager.QUERY_STRING_STATE_PARAM_NAME)).thenReturn(TicketState.AVAILABLE.toString());
        Result result = ticketsController.index();

        assertEquals(Helpers.OK, Helpers.status(result));
        assertEquals("application/json", Helpers.contentType(result));

        String json = Helpers.contentAsString(result);
        JsonNode jsonNode = Json.parse(json);

        assertTrue(jsonNode.isArray());
        JsonNode ticket1 = jsonNode.get(0);

        assertEquals(AN_EVENT_ID, ticket1.get("eventId").asLong());
        assertEquals(A_CATEGORY_ID, ticket1.get("categoryId").asLong());

        verify(mockedTicketsInteractor).search(refEq(ticketSearchCriteria));
    }

    @Test
    public void indexThrowsExceptionWithInvalidParameters(TicketsInteractor mockedTicketsInteractor) {
        TicketSearchCriteria ticketSearchCriteria = new TicketSearchCriteria();
        ticketSearchCriteria.setEventId(AN_EVENT_ID);
        ticketSearchCriteria.setCategoryId(A_CATEGORY_ID);
        when(mockedTicketsInteractor.search(refEq(ticketSearchCriteria))).thenThrow(new InvalidParameterException("Test"));

        when(mockedRequest.getQueryString(ApiTicketingConstantsManager.QUERY_STRING_EVENT_ID_PARAM_NAME)).thenReturn(String.valueOf(AN_EVENT_ID));
        when(mockedRequest.getQueryString(ApiTicketingConstantsManager.QUERY_STRING_CATEGORY_ID_PARAM_NAME)).thenReturn(String.valueOf(A_CATEGORY_ID));

        Result result = ticketsController.index();

        assertEquals(Helpers.BAD_REQUEST, Helpers.status(result));
        verify(mockedTicketsInteractor).search(refEq(ticketSearchCriteria));
    }

    @Test
    public void showReturnsATicket(TicketsInteractor mockedTicketsInteractor) throws RecordNotFoundException {
        Ticket mockedTicket = createFakeTicket();
        when(mockedTicketsInteractor.getById(mockedTicket.getId())).thenReturn(mockedTicket);

        Result result = ticketsController.show(mockedTicket.getId());

        assertEquals(Helpers.OK, Helpers.status(result));
        assertEquals("application/json", Helpers.contentType(result));
        String json = Helpers.contentAsString(result);
        JsonNode jsonNode = Json.parse(json);
        assertEquals(mockedTicket.getId(), jsonNode.get("id").asLong());
        verify(mockedTicketsInteractor).getById(mockedTicket.getId());
    }

    @Test
    public void showReturnsNotFoundWhenRecordNotFound(TicketsInteractor mockedTicketsInteractor) throws RecordNotFoundException {
        long ticketId = 1;
        when(mockedTicketsInteractor.getById(ticketId)).thenThrow(new RecordNotFoundException());

        Result result = ticketsController.show(ticketId);
        assertEquals(Helpers.NOT_FOUND, Helpers.status(result));
        assertEquals(null, Helpers.contentType(result));

        verify(mockedTicketsInteractor).getById(ticketId);
    }

    @Test
    public void freeTicketWhenExists(TicketsInteractor mockedTicketsInteractor) throws RecordNotFoundException, UpdateTicketStateUnauthorizedException {
        long ticketId = 1;
        ObjectNode json = Json.newObject();
        ArrayNode node = json.putArray(ApiTicketingConstantsManager.TICKET_IDS_FIELD_NAME);
        node.add(ticketId);
        when(mockedBody.asJson()).thenReturn(json);

        Result result = ticketsController.free();

        assertEquals(Helpers.OK, Helpers.status(result));
        verify(mockedTicketsInteractor).freeATicket(ticketId);
    }

    @Test
    public void freeTicketWhenDoesNotExistsThenReturnsNotFound(TicketsInteractor mockedTicketsInteractor) throws
            RecordNotFoundException, UpdateTicketStateUnauthorizedException {
        long ticketId = 1;
        doThrow(new RecordNotFoundException()).when(mockedTicketsInteractor).freeATicket(ticketId);

        ObjectNode json = Json.newObject();
        ArrayNode node = json.putArray(ApiTicketingConstantsManager.TICKET_IDS_FIELD_NAME);
        node.add(ticketId);
        when(mockedBody.asJson()).thenReturn(json);

        Result result = ticketsController.free();

        assertEquals(Helpers.NOT_FOUND, Helpers.status(result));
    }

    @Test
    public void reserveTicketWhenExists(TicketsInteractor mockedTicketsInteractor) throws RecordNotFoundException, MaximumExceededException, UpdateTicketStateUnauthorizedException {
        long ticketId = 1;
        ObjectNode json = Json.newObject();
        ArrayNode node = json.putArray(ApiTicketingConstantsManager.TICKET_IDS_FIELD_NAME);
        node.add(ticketId);
        when(mockedBody.asJson()).thenReturn(json);

        Result result = ticketsController.reserve();

        assertEquals(Helpers.OK, Helpers.status(result));
        verify(mockedTicketsInteractor).reserveATicket(ticketId);
    }

    @Test
    public void reserveTicketWhenDoesNotExistsThenReturnsNotFound(TicketsInteractor mockedTicketsInteractor) throws RecordNotFoundException, UpdateTicketStateUnauthorizedException {
        long ticketId = 1;
        doThrow(new RecordNotFoundException()).when(mockedTicketsInteractor).reserveATicket(ticketId);

        ObjectNode json = Json.newObject();
        ArrayNode node = json.putArray(ApiTicketingConstantsManager.TICKET_IDS_FIELD_NAME);
        node.add(ticketId);
        when(mockedBody.asJson()).thenReturn(json);

        Result result = ticketsController.reserve();

        assertEquals(Helpers.NOT_FOUND, Helpers.status(result));
    }

    @Test
    public void reserveTooMuchTicketsReturnsException(TicketsInteractor mockedTicketsInteractor) throws
            RecordNotFoundException, MaximumExceededException {
        //TODO
    }

    @Test
    public void showEventSectionsWhenTicketExists(TicketsInteractor mockedTicketsInteractor) throws RecordNotFoundException {
        Ticket mockedTicket = createFakeTicket();
        TicketSearchCriteria ticketSearchCriteria = new TicketSearchCriteria();
        ticketSearchCriteria.setEventId(mockedTicket.getEventId());
        ticketSearchCriteria.setCategoryId(mockedTicket.getCategoryId());
        List<Ticket> tempTicketList = new ArrayList<>();
        tempTicketList.add(mockedTicket);
        when(mockedTicketsInteractor.search(refEq(ticketSearchCriteria))).thenReturn(tempTicketList);

        Result result = ticketsController.showEventSections(mockedTicket.getEventId());

        assertEquals(Helpers.OK, Helpers.status(result));
        assertEquals("application/json", Helpers.contentType(result));
    }

    private Ticket createFakeTicket() {
        return new Ticket(1, 1, TicketState.AVAILABLE, "Section A", 100000);
    }

    public static class TestModule extends JukitoModule {

        @Override
        protected void configureTest() {
            forceMock(TicketsInteractor.class);
        }
    }
}
