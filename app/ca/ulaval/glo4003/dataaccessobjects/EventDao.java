package ca.ulaval.glo4003.dataaccessobjects;

import ca.ulaval.glo4003.exceptions.NumberTooLargeException;
import ca.ulaval.glo4003.exceptions.RecordNotFoundException;
import ca.ulaval.glo4003.models.Category;
import ca.ulaval.glo4003.models.Event;
import ca.ulaval.glo4003.models.EventSearchCriteria;

import java.util.List;

public interface EventDao extends DataAccessObject<Event> {

    public List<Event> search(EventSearchCriteria criteria) throws Exception;

    public Category findCategory(long eventId, long categoryId) throws RecordNotFoundException;

    public void decrementEventCategoryNumberOfTickets(long eventId, long categoryId, int numberOfTickets) throws RecordNotFoundException, NumberTooLargeException;
}
