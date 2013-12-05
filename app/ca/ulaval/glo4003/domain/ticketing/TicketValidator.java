package ca.ulaval.glo4003.domain.ticketing;

import ca.ulaval.glo4003.domain.RecordNotFoundException;
import ca.ulaval.glo4003.domain.event.Category;
import ca.ulaval.glo4003.domain.event.Event;
import ca.ulaval.glo4003.domain.event.EventsInteractor;
import com.google.inject.Inject;

import java.util.Iterator;
import java.util.List;

public class TicketValidator {
    private final TicketsInteractor ticketsInteractor;
    private final EventsInteractor eventsInteractor;

    @Inject
    public TicketValidator(TicketsInteractor ticketsInteractor, EventsInteractor eventsInteractor) {
        this.ticketsInteractor = ticketsInteractor;
        this.eventsInteractor = eventsInteractor;
    }

    public void validate(long eventId, long categoryId, String section, int seat)
            throws NoSuchCategoryException, AlreadyAssignedSeatExceptionDummy, NoSuchTicketSectionExceptionDummy, RecordNotFoundException {

        Event event = eventsInteractor.getById(eventId);
        validateCategory(event.getCategories(), categoryId);
        validateTicketNotExists(eventId, categoryId, section, seat);
    }

    private void validateTicketNotExists(long eventId, long categoryId, String section, int seat)
            throws NoSuchTicketSectionExceptionDummy, AlreadyAssignedSeatExceptionDummy {

        TicketSearchCriteria ticketSearchCriteria = new TicketSearchCriteria();
        ticketSearchCriteria.setEventId(eventId);
        ticketSearchCriteria.setCategoryId(categoryId);
        ticketSearchCriteria.setSectionName(section);

        List<Ticket> tickets = ticketsInteractor.search(ticketSearchCriteria);
        if (tickets.size() == 0) throw new NoSuchTicketSectionExceptionDummy();

        Ticket lastTicket = tickets.get(tickets.size() - 1);
        if (lastTicket.getSeat() > seat) throw new AlreadyAssignedSeatExceptionDummy();
    }

    private void validateCategory(List<Category> categories, long categoryId) throws NoSuchCategoryException {
        Iterator iterator = categories.iterator();
        boolean categoryExists = false;

        while (iterator.hasNext()) {
            Category category = (Category) iterator.next();
            categoryExists = (category.getId() == categoryId);
        }
        if (!categoryExists) throw new NoSuchCategoryException();
    }
}
