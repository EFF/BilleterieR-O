package ca.ulaval.glo4003.dataaccessobjects;

import ca.ulaval.glo4003.models.Event;
import ca.ulaval.glo4003.models.EventSearchCriteria;

import java.util.List;

public interface EventDao extends DataAccessObject<Event> {

    public List<Event> search(EventSearchCriteria criteria) throws Exception;
}
