package ca.ulaval.glo4003.unittests.dataaccessobjects;

import ca.ulaval.glo4003.dataaccessobjects.TicketDao;
import ca.ulaval.glo4003.models.Ticket;
import ca.ulaval.glo4003.models.TicketSearchCriteria;
import ca.ulaval.glo4003.services.DaoPersistenceService;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class TicketDaoTest {

    private DaoPersistenceService daoPersistenceService;
    private TicketDao ticketDao;
    private Ticket ticket1;
    private Ticket ticket2;
    private Ticket ticket3;
    private Ticket ticket4;

    @Before
    public void setUp() {
        daoPersistenceService = mock(DaoPersistenceService.class);

        ticketDao = new TicketDao(daoPersistenceService);

        ticket1 = new Ticket(1, 0, "Section 100", 10);
        ticket2 = new Ticket(1, 1, "Section 200", 11);
        ticket3 = new Ticket(2, 0, "Section 100", 12);
        ticket4 = new Ticket(2, 1, "Section 200", 13);

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
            assertEquals(eventId, results.get(0).getEventId());
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
            assertEquals(categoryId, results.get(0).getCategoryId());
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
            assertEquals(section, results.get(0).getSection());
        }
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

    //Test with multiple criteria. (Every mix possible) Event-Cat, Event-Sec, Cat-Sec, Event-Ca-Sec.
}
