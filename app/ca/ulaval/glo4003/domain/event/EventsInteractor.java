package ca.ulaval.glo4003.domain.event;

import ca.ulaval.glo4003.domain.RecordNotFoundException;
import com.google.inject.Inject;

import java.security.InvalidParameterException;
import java.util.List;

public class EventsInteractor {

    private final EventDao eventDao;

    @Inject
    public EventsInteractor(EventDao eventDao) {
        this.eventDao = eventDao;
    }

    public List<Event> search(final EventSearchCriteria eventSearchCriteria) throws InvalidParameterException {
        return eventDao.search(eventSearchCriteria);
    }

    public Event getById(long eventId) throws RecordNotFoundException {
        return eventDao.read(eventId);
    }

}
