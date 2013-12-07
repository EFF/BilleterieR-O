package ca.ulaval.glo4003.domain.ticketing;

import ca.ulaval.glo4003.domain.RecordNotFoundException;
import ca.ulaval.glo4003.domain.event.Category;
import ca.ulaval.glo4003.domain.event.Event;
import ca.ulaval.glo4003.domain.event.EventsInteractor;
import com.google.inject.Inject;

import java.util.Iterator;
import java.util.List;

public class TicketConstraintValidator {
    private final TicketsInteractor ticketsInteractor;
    private final EventsInteractor eventsInteractor;

    @Inject
    public TicketConstraintValidator(TicketsInteractor ticketsInteractor, EventsInteractor eventsInteractor) {
        this.ticketsInteractor = ticketsInteractor;
        this.eventsInteractor = eventsInteractor;
    }

    public void validateGeneralAdmission(long eventId, long categoryId) throws NoSuchCategoryException, RecordNotFoundException {
        Event event = eventsInteractor.getById(eventId);
        validateCategory(event.getCategories(), categoryId);
    }

    public void validateSeatedTicket(long eventId, long categoryId, String section, int seat)
            throws NoSuchCategoryException, AlreadyAssignedSeatException, NoSuchTicketSectionException, RecordNotFoundException {

        Event event = eventsInteractor.getById(eventId);
        validateCategory(event.getCategories(), categoryId);
        validateTicketNotExists(eventId, categoryId, section, seat);
    }

    private void validateTicketNotExists(long eventId, long categoryId, String section, int seat)
            throws NoSuchTicketSectionException, AlreadyAssignedSeatException {

        TicketSearchCriteria ticketSearchCriteria = new TicketSearchCriteria();
        ticketSearchCriteria.setEventId(eventId);
        ticketSearchCriteria.setCategoryId(categoryId);
        ticketSearchCriteria.setSectionName(section);

        List<Ticket> tickets = ticketsInteractor.search(ticketSearchCriteria);
        if (tickets.size() == 0) throw new NoSuchTicketSectionException();

        Ticket lastTicket = tickets.get(tickets.size() - 1);
        if (lastTicket.getSeat() > seat) throw new AlreadyAssignedSeatException();
    }

    private void validateCategory(List<Category> categories, long categoryId) throws NoSuchCategoryException {
        Iterator iterator = categories.iterator();
        boolean categoryExists = false;

        while (iterator.hasNext() && !categoryExists) {
            Category category = (Category) iterator.next();
            categoryExists = (category.getId() == categoryId);
        }
        if (!categoryExists) throw new NoSuchCategoryException();
    }
}
