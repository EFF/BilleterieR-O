package ca.ulaval.glo4003.unittests.controllers;

import ca.ulaval.glo4003.ConstantsManager;
import ca.ulaval.glo4003.controllers.TicketsController;
import ca.ulaval.glo4003.dataaccessobjects.EventDao;
import ca.ulaval.glo4003.dataaccessobjects.TicketDao;
import ca.ulaval.glo4003.exceptions.MaximumExceededException;
import ca.ulaval.glo4003.exceptions.RecordNotFoundException;
import ca.ulaval.glo4003.models.*;
import com.google.inject.Inject;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.jukito.JukitoModule;
import org.jukito.JukitoRunner;
import org.junit.Before;
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
    private static final String A_SECTION = "section 100";
    private static final long A_SEAT = 55;
    private static final long AN_EVENT_ID = 1;
    private static final long A_CATEGORY_ID = 1;
    private static final double A_TICKET_PRICE = 11.00;
    private static final int A_TICKET_NUMBER = 77;
    private static final long A_TICKET_ID = 1;
    private static final long ANOTHER_TICKET_ID = 2;
    private Ticket firstTicket;
    private Ticket secondTicket;
    private TicketSearchCriteria ticketSearchCriteria;
    private List<Ticket> tempTicketsList;

    @Inject
    private TicketsController ticketsController = null;

    @Before
    public void setup(TicketDao mockedTicketDao) {
        firstTicket = new Ticket(AN_EVENT_ID, A_CATEGORY_ID, A_SECTION, A_SEAT);
        firstTicket.setId(A_TICKET_ID);
        secondTicket = new Ticket(AN_EVENT_ID, A_CATEGORY_ID, A_SECTION, A_SEAT + 1);
        secondTicket.setId(ANOTHER_TICKET_ID);
        tempTicketsList = new ArrayList<>();
        tempTicketsList.add(firstTicket);
        tempTicketsList.add(secondTicket);

        ticketSearchCriteria = new TicketSearchCriteria();
        when(mockedTicketDao.search(refEq(ticketSearchCriteria))).thenReturn(tempTicketsList);
    }

    @Test
    public void indexReturnsOnlyTicketCorrespondingToParameters(TicketDao mockedTicketDao) {
        ticketSearchCriteria.setEventId(AN_EVENT_ID);
        ticketSearchCriteria.setCategoryId(A_CATEGORY_ID);
        ticketSearchCriteria.addState(TicketState.AVAILABLE);
        List<Ticket> tempFilteredTicketList = new ArrayList<>();
        tempFilteredTicketList.add(firstTicket);
        when(mockedTicketDao.search(refEq(ticketSearchCriteria))).thenReturn(tempFilteredTicketList);

        when(mockedRequest.getQueryString(ConstantsManager.EVENT_ID_FIELD_NAME)).thenReturn(String.valueOf(AN_EVENT_ID));
        when(mockedRequest.getQueryString(ConstantsManager.CATEGORY_ID_FIELD_NAME)).thenReturn(String.valueOf(A_CATEGORY_ID));
        when(mockedRequest.getQueryString(ConstantsManager.TICKET_STATE_FIELD_NAME)).thenReturn(String.valueOf(TicketState.AVAILABLE));
        when(mockedRequest.getQueryString(ConstantsManager.QUERY_STRING_STATE_PARAM_NAME)).thenReturn(TicketState.AVAILABLE.toString());
        Result result = ticketsController.index();

        assertEquals(Helpers.OK, Helpers.status(result));
        assertEquals("application/json", Helpers.contentType(result));

        String json = Helpers.contentAsString(result);
        JsonNode jsonNode = Json.parse(json);

        assertTrue(jsonNode.isArray());
        JsonNode ticket1 = jsonNode.get(0);

        assertEquals(AN_EVENT_ID, ticket1.get("eventId").asLong());
        assertEquals(A_CATEGORY_ID, ticket1.get("categoryId").asLong());

        verify(mockedTicketDao).search(refEq(ticketSearchCriteria));
    }

    @Test
    public void indexThrowsExceptionWithInvalidParameters(TicketDao mockedTicketDao) {
        ticketSearchCriteria.setEventId(AN_EVENT_ID);
        ticketSearchCriteria.setCategoryId(A_CATEGORY_ID);
        when(mockedTicketDao.search(refEq(ticketSearchCriteria))).thenThrow(new InvalidParameterException("Test"));

        when(mockedRequest.getQueryString(ConstantsManager.EVENT_ID_FIELD_NAME)).thenReturn(String.valueOf(AN_EVENT_ID));
        when(mockedRequest.getQueryString(ConstantsManager.CATEGORY_ID_FIELD_NAME)).thenReturn(String.valueOf(A_CATEGORY_ID));

        Result result = ticketsController.index();

        assertEquals(Helpers.INTERNAL_SERVER_ERROR, Helpers.status(result));
        verify(mockedTicketDao).search(refEq(ticketSearchCriteria));
    }

    @Test
    public void showReturnsATicket(TicketDao mockedTicketDao) throws RecordNotFoundException {
        when(mockedTicketDao.read(firstTicket.getId())).thenReturn(firstTicket);

        Result result = ticketsController.show(firstTicket.getId());
        assertEquals(Helpers.OK, Helpers.status(result));
        assertEquals("application/json", Helpers.contentType(result));

        String json = Helpers.contentAsString(result);
        JsonNode jsonNode = Json.parse(json);

        assertEquals(firstTicket.getId(), jsonNode.get("id").asLong());
        verify(mockedTicketDao).read(firstTicket.getId());
    }

    @Test
    public void showReturnsNotFoundWhenRecordNotFound(TicketDao mockedTicketDao) throws RecordNotFoundException {
        when(mockedTicketDao.read(firstTicket.getId())).thenThrow(new RecordNotFoundException());

        Result result = ticketsController.show(firstTicket.getId());
        assertEquals(Helpers.NOT_FOUND, Helpers.status(result));
        assertEquals(null, Helpers.contentType(result));

        verify(mockedTicketDao).read(firstTicket.getId());
    }

    @Test
    public void checkoutTicketWhenItExists(TicketDao mockedTicketDao, EventDao mockedEventDao) throws RecordNotFoundException {
        firstTicket.setState(TicketState.RESERVED);
        when(mockedTicketDao.read(firstTicket.getId())).thenReturn(firstTicket);
        when(mockedEventDao.findCategory(firstTicket.getEventId(), firstTicket.getCategoryId()))
                .thenReturn(new Category(A_TICKET_PRICE, A_TICKET_NUMBER, A_CATEGORY_ID,
                        CategoryType.GENERAL_ADMISSION));

        ObjectNode json = Json.newObject();
        ArrayNode node = json.putArray(ConstantsManager.TICKET_IDS_FIELD_NAME);
        node.add(firstTicket.getId());
        when(mockedBody.asJson()).thenReturn(json);

        Result result = ticketsController.checkout();

        assertEquals(Helpers.OK, Helpers.status(result));
    }

    @Test
    public void freeTicketWhenExists(TicketDao mockedTicketDao, EventDao mockedEventDao) throws RecordNotFoundException {
        when(mockedTicketDao.read(firstTicket.getId())).thenReturn(firstTicket);
        when(mockedEventDao.findCategory(firstTicket.getEventId(), firstTicket.getCategoryId()))
                .thenReturn(new Category(A_TICKET_PRICE, A_TICKET_NUMBER, A_CATEGORY_ID,
                        CategoryType.GENERAL_ADMISSION));

        ObjectNode json = Json.newObject();
        ArrayNode node = json.putArray(ConstantsManager.TICKET_IDS_FIELD_NAME);
        node.add(firstTicket.getId());
        when(mockedBody.asJson()).thenReturn(json);

        Result result = ticketsController.free();

        assertEquals(Helpers.OK, Helpers.status(result));
        verify(mockedEventDao).incrementEventCategoryNumberOfTickets(
                firstTicket.getEventId(),
                firstTicket.getCategoryId(), 1);
    }

    @Test
    public void freeTicketWhenWrongIdThenReturnsNotFound(TicketDao mockedTicketDao, EventDao mockedEventDao) throws RecordNotFoundException {
        when(mockedTicketDao.read(anyLong())).thenThrow(new RecordNotFoundException());

        ObjectNode json = Json.newObject();
        ArrayNode node = json.putArray(ConstantsManager.TICKET_IDS_FIELD_NAME);
        node.add(0L);
        when(mockedBody.asJson()).thenReturn(json);

        Result result = ticketsController.free();

        assertEquals(Helpers.NOT_FOUND, Helpers.status(result));
        assertEquals(null, Helpers.contentType(result));
        verify(mockedTicketDao, times(1)).read(anyLong());
    }

    @Test
    public void reserveTicketWhenExists(TicketDao mockedTicketDao, EventDao mockedEventDao) throws RecordNotFoundException, MaximumExceededException {
        when(mockedTicketDao.read(firstTicket.getId())).thenReturn(firstTicket);
        when(mockedEventDao.findCategory(firstTicket.getEventId(), firstTicket.getCategoryId()))
                .thenReturn(new Category(A_TICKET_PRICE, A_TICKET_NUMBER, A_CATEGORY_ID,
                        CategoryType.GENERAL_ADMISSION));

        ObjectNode json = Json.newObject();
        ArrayNode node = json.putArray(ConstantsManager.TICKET_IDS_FIELD_NAME);
        node.add(firstTicket.getId());
        when(mockedBody.asJson()).thenReturn(json);

        Result result = ticketsController.reserve();

        assertEquals(Helpers.OK, Helpers.status(result));
        verify(mockedEventDao).decrementEventCategoryNumberOfTickets(
                firstTicket.getEventId(),
                firstTicket.getCategoryId(), 1);
    }

    @Test
    public void reserveTicketWhenDoesNotExistsThenReturnsNotFound(TicketDao mockedTicketDao, EventDao mockedEventDao) throws RecordNotFoundException {
        when(mockedTicketDao.read(anyLong())).thenThrow(new RecordNotFoundException());
        ObjectNode json = Json.newObject();
        ArrayNode node = json.putArray(ConstantsManager.TICKET_IDS_FIELD_NAME);
        node.add(0L);
        when(mockedBody.asJson()).thenReturn(json);

        Result result = ticketsController.reserve();

        assertEquals(Helpers.NOT_FOUND, Helpers.status(result));
        verify(mockedTicketDao, times(1)).read(anyLong());
    }

    @Test
    public void reserveTooMuchTicketsWhenExistsThenReturnsException(TicketDao mockedTicketDao, EventDao mockedEventDao) throws RecordNotFoundException, MaximumExceededException {
        when(mockedTicketDao.read(firstTicket.getId())).thenReturn(firstTicket);
        when(mockedEventDao.findCategory(firstTicket.getEventId(), firstTicket.getCategoryId()))
                .thenReturn(new Category(A_TICKET_PRICE, A_TICKET_NUMBER, A_CATEGORY_ID,
                        CategoryType.GENERAL_ADMISSION));
        doThrow(new MaximumExceededException()).when(mockedEventDao).decrementEventCategoryNumberOfTickets(
                firstTicket.getEventId(), firstTicket.getCategoryId(), 1);

        ObjectNode json = Json.newObject();
        ArrayNode node = json.putArray(ConstantsManager.TICKET_IDS_FIELD_NAME);
        node.add(firstTicket.getId());
        when(mockedBody.asJson()).thenReturn(json);

        Result result = ticketsController.reserve();

        assertEquals(Helpers.BAD_REQUEST, Helpers.status(result));
    }

    @Test
    public void showEventSectionsWhenTicketExists(TicketDao mockedTicketDao) throws RecordNotFoundException {
        ticketSearchCriteria.setEventId(firstTicket.getEventId());
        ticketSearchCriteria.setCategoryId(firstTicket.getCategoryId());
        List<Ticket> tempTicketList = new ArrayList<>();
        tempTicketList.add(firstTicket);
        when(mockedTicketDao.search(refEq(ticketSearchCriteria))).thenReturn(tempTicketList);

        Result result = ticketsController.showEventSections(firstTicket.getEventId());

        assertEquals(Helpers.OK, Helpers.status(result));
        assertEquals("application/json", Helpers.contentType(result));
    }

    @Test
    public void checkoutReturnInternalServerErrorWhenOneTicketIsNotReserved(TicketDao mockedTicketDao) throws RecordNotFoundException {
        Ticket reservedTicket = new Ticket(AN_EVENT_ID, A_CATEGORY_ID, A_SECTION, A_SEAT);
        reservedTicket.setId(A_TICKET_ID);
        Ticket notReservedTicket = new Ticket(AN_EVENT_ID, A_CATEGORY_ID, A_SECTION, A_SEAT + 1);
        notReservedTicket.setId(ANOTHER_TICKET_ID);
        reservedTicket.setState(TicketState.RESERVED);
        notReservedTicket.setState(TicketState.AVAILABLE);

        when(mockedTicketDao.read(reservedTicket.getId())).thenReturn(reservedTicket);
        when(mockedTicketDao.read(notReservedTicket.getId())).thenReturn(notReservedTicket);

        ObjectNode json = Json.newObject();
        ArrayNode node = json.putArray(ConstantsManager.TICKET_IDS_FIELD_NAME);
        node.add(A_TICKET_ID);
        node.add(ANOTHER_TICKET_ID);
        when(mockedBody.asJson()).thenReturn(json);

        Result result = ticketsController.checkout();

        assertEquals(Helpers.INTERNAL_SERVER_ERROR, Helpers.status(result));
        verify(mockedTicketDao, times(2)).read(A_TICKET_ID);
        verify(mockedTicketDao, times(2)).read(ANOTHER_TICKET_ID);
    }

    public static class TestModule extends JukitoModule {

        @Override
        protected void configureTest() {
            forceMock(TicketDao.class);
            forceMock(EventDao.class);
        }
    }
}
