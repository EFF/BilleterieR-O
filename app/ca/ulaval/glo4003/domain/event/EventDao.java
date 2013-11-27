package ca.ulaval.glo4003.domain.event;

import ca.ulaval.glo4003.domain.Dao;
import ca.ulaval.glo4003.domain.RecordNotFoundException;

import java.security.InvalidParameterException;
import java.util.List;

public interface EventDao extends Dao<Event> {

    public List<Event> search(final EventSearchCriteria criteria) throws InvalidParameterException;

    public Category findCategory(long eventId, long categoryId) throws RecordNotFoundException;

}
