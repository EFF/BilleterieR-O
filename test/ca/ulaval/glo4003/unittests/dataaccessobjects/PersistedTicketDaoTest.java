package ca.ulaval.glo4003.unittests.dataaccessobjects;

import ca.ulaval.glo4003.persistence.ticketing.PersistedTicketDao;
import ca.ulaval.glo4003.persistence.UniqueConstraintValidator;
import ca.ulaval.glo4003.domain.ticketing.Ticket;
import ca.ulaval.glo4003.domain.ticketing.TicketSearchCriteria;
import ca.ulaval.glo4003.domain.ticketing.TicketState;
import ca.ulaval.glo4003.persistence.DaoPersistenceService;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class PersistedTicketDaoTest {

    private DaoPersistenceService daoPersistenceService;
    private PersistedTicketDao ticketDao;
    private Ticket ticket1;
    private Ticket ticket2;
    private Ticket ticket3;
    private Ticket ticket4;

    @Before
    public void setUp() {
        daoPersistenceService = mock(DaoPersistenceService.class);

        ticketDao = new PersistedTicketDao(daoPersistenceService, new UniqueConstraintValidator<Ticket>());

        ticket1 = new Ticket(1, 0, TicketState.SOLD, "Section 100", 10);
        ticket2 = new Ticket(1, 1, TicketState.AVAILABLE, "Section 200", 11);
        ticket3 = new Ticket(2, 0, TicketState.AVAILABLE, "Section 100", 12);
        ticket4 = new Ticket(2, 1, TicketState.RESERVED, "Section 200", 13);

        ticketDao.create(ticket1);
        ticketDao.create(ticket2);
        ticketDao.create(ticket3);
        ticketDao.create(ticket4);
    }

    @Test
    public void searchAllThenReturnsAllEvents() {
        List<Ticket> results = ticketDao.search(new TicketSearchCriteria());

        assertNotNull(results);
        assertEquals(4, results.size());
    }

    @Test
    public void searchEventThenReturnsFilteredResults() {
        long eventId = ticket1.getEventId();
        TicketSearchCriteria ticketSearchCriteria = new TicketSearchCriteria();
        ticketSearchCriteria.setEventId(eventId);

        List<Ticket> results = ticketDao.search(ticketSearchCriteria);

        assertNotNull(results);
        assertEquals(2, results.size());
        for (int i = 0; i < results.size(); i++) {
            assertEquals(eventId, results.get(i).getEventId());
        }
    }

    @Test
    public void searchEventThatDoesNotExistsThenReturnsEmptyResults() {
        long eventId = 10;
        TicketSearchCriteria ticketSearchCriteria = new TicketSearchCriteria();
        ticketSearchCriteria.setEventId(eventId);

        List<Ticket> results = ticketDao.search(ticketSearchCriteria);

        assertNotNull(results);
        assertEquals(0, results.size());
    }

    @Test
    public void searchCategoryThenReturnsFilteredResults() {
        long categoryId = ticket1.getCategoryId();
        TicketSearchCriteria ticketSearchCriteria = new TicketSearchCriteria();
        ticketSearchCriteria.setCategoryId(categoryId);

        List<Ticket> results = ticketDao.search(ticketSearchCriteria);

        assertNotNull(results);
        assertEquals(2, results.size());
        for (int i = 0; i < results.size(); i++) {
            assertEquals(categoryId, results.get(i).getCategoryId());
        }
    }

    @Test
    public void searchCategoryThatDoesNotExistsThenReturnsEmptyResults() {
        long categoryId = 10;
        TicketSearchCriteria ticketSearchCriteria = new TicketSearchCriteria();
        ticketSearchCriteria.setCategoryId(categoryId);

        List<Ticket> results = ticketDao.search(ticketSearchCriteria);

        assertNotNull(results);
        assertEquals(0, results.size());
    }

    @Test
    public void searchSectionThenReturnsFilteredResults() {
        String section = ticket1.getSection();
        TicketSearchCriteria ticketSearchCriteria = new TicketSearchCriteria();
        ticketSearchCriteria.setSectionName(section);

        List<Ticket> results = ticketDao.search(ticketSearchCriteria);

        assertNotNull(results);
        assertEquals(2, results.size());
        for (int i = 0; i < results.size(); i++) {
            assertEquals(section, results.get(i).getSection());
        }
    }

    @Test
    public void searchSectionAndCategoryThenReturnsFilteredResults() {
        TicketSearchCriteria ticketSearchCriteria = new TicketSearchCriteria();
        String section = ticket1.getSection();
        long categoryId = ticket1.getCategoryId();
        ticketSearchCriteria.setSectionName(section);
        ticketSearchCriteria.setCategoryId(categoryId);

        List<Ticket> results = ticketDao.search(ticketSearchCriteria);

        assertNotNull(results);
        assertEquals(2, results.size());
        for (int i = 0; i < results.size(); i++) {
            assertEquals(section, results.get(i).getSection());
            assertEquals(categoryId, results.get(i).getCategoryId());
        }
    }

    @Test
    public void searchSectionAndCategoryThenReturnsFirstFilteredResults() {
        TicketSearchCriteria ticketSearchCriteria = new TicketSearchCriteria();
        String section = ticket1.getSection();
        long categoryId = ticket1.getCategoryId();
        int quantity = 1;
        ticketSearchCriteria.setSectionName(section);
        ticketSearchCriteria.setCategoryId(categoryId);
        ticketSearchCriteria.setQuantity(quantity);

        List<Ticket> results = ticketDao.search(ticketSearchCriteria);

        assertNotNull(results);
        assertEquals(1, results.size());

        assertEquals(section, results.get(0).getSection());
        assertEquals(categoryId, results.get(0).getCategoryId());
    }

    @Test
    public void searchWrongSectionAndGoodCategoryThenReturnsEmptyResults() {
        TicketSearchCriteria ticketSearchCriteria = new TicketSearchCriteria();
        String section = "bad";
        long categoryId = ticket1.getCategoryId();
        ticketSearchCriteria.setSectionName(section);
        ticketSearchCriteria.setCategoryId(categoryId);

        List<Ticket> results = ticketDao.search(ticketSearchCriteria);

        assertNotNull(results);
        assertEquals(0, results.size());
    }

    @Test
    public void searchSectionThatDoesNotExistsThenReturnsEmptyResults() {
        String section = "bad";
        TicketSearchCriteria ticketSearchCriteria = new TicketSearchCriteria();
        ticketSearchCriteria.setSectionName(section);

        List<Ticket> results = ticketDao.search(ticketSearchCriteria);

        assertNotNull(results);
        assertEquals(0, results.size());
    }

    @Test
    public void searchStatesThatDoExistsThenReturnsFilteredResults() {
        TicketSearchCriteria ticketSearchCriteria = new TicketSearchCriteria();
        ticketSearchCriteria.addState(TicketState.SOLD);
        ticketSearchCriteria.addState(TicketState.RESERVED);

        List<Ticket> results = ticketDao.search(ticketSearchCriteria);
        assertNotNull(results);
        assertEquals(2, results.size());
        Assert.assertEquals(ticket1.getCategoryId(), results.get(0).getCategoryId());
        Assert.assertEquals(ticket1.getEventId(), results.get(0).getEventId());
        Assert.assertEquals(ticket1.getSection(), results.get(0).getSection());
        Assert.assertEquals(ticket1.getSeat(), results.get(0).getSeat());
        Assert.assertEquals(ticket1.getState(), results.get(0).getState());
        Assert.assertEquals(ticket4.getCategoryId(), results.get(1).getCategoryId());
        Assert.assertEquals(ticket4.getEventId(), results.get(1).getEventId());
        Assert.assertEquals(ticket4.getSection(), results.get(1).getSection());
        Assert.assertEquals(ticket4.getSeat(), results.get(1).getSeat());
        Assert.assertEquals(ticket4.getState(), results.get(1).getState());
    }
}
