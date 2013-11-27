package ca.ulaval.glo4003.dataaccessobjects;

import ca.ulaval.glo4003.exceptions.MaximumExceededException;
import ca.ulaval.glo4003.exceptions.RecordNotFoundException;
import ca.ulaval.glo4003.models.Category;
import ca.ulaval.glo4003.models.Event;
import ca.ulaval.glo4003.models.EventSearchCriteria;

import java.security.InvalidParameterException;
import java.util.List;

public interface EventDao extends DataAccessObject<Event> {

    public List<Event> search(final EventSearchCriteria criteria) throws InvalidParameterException;

    public Category findCategory(long eventId, long categoryId) throws RecordNotFoundException;

    public void decrementEventCategoryNumberOfTickets(long eventId, long categoryId, int numberOfTickets) throws RecordNotFoundException, MaximumExceededException;
}
