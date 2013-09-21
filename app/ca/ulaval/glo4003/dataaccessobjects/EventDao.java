package ca.ulaval.glo4003.dataaccessobjects;

import ca.ulaval.glo4003.models.Event;
import ca.ulaval.glo4003.models.SearchCriteria;

import java.util.List;

public interface EventDao extends DataAccessObject<Event> {
    public List<Event> search(SearchCriteria criteria) throws Exception;

}
