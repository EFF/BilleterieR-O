package ca.ulaval.glo4003.unittests.dataaccessobjects;

import ca.ulaval.glo4003.models.Event;
import ca.ulaval.glo4003.models.EventSearchCriteria;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EventDaoInMemory implements EventDao {

    // TODO: Change to private and rewrite tests
    protected ArrayList<Event> events;

    public EventDaoInMemory() {
        events = new ArrayList<>();
    }

    public void create(Event event) {
        events.add(event);
    }

    public Event read(long id) {
        Event result = null;

        Iterator<Event> itr = events.iterator();
        while (itr.hasNext() && result == null) {
            Event currentEvent = itr.next();
            if (currentEvent.getId() == id) {
                result = currentEvent;
            }
        }

        return result;
    }

    public void update(Event event) {
        //TODO
    }

    public void delete(long id) {
        //TODO
    }

    public List<Event> list() {
        return events;
    }

    @Override
    public List<Event> search(final EventSearchCriteria criteria) throws InvalidParameterException {

        FluentIterable<Event> results = FluentIterable.from(this.list());

        // TODO Write unit test to ensure that equal dates don't throw
        if (criteria.getDateStart() != null && criteria.getDateEnd() != null) {
            if (criteria.getDateEnd().isBefore(criteria.getDateStart())) {
                String errorFormat = "End Date %s can't be before Start Date %s";
                String error = String.format(errorFormat, criteria.getDateEnd().toString(), criteria.getDateStart().toString());
                throw new InvalidParameterException(error);
            }
        }

        results = FilterBySportName(criteria.getSportName(), results);
        results = FilterByTeamName(criteria.getTeamName(), results);

        results = FilterByDateStart(criteria.getDateStart(), results);
        results = FilterByDateEnd(criteria.getDateEnd(), results);

        return results.toList();
    }

    private FluentIterable<Event> FilterByDateStart(LocalDate dateStart, FluentIterable<Event> results) {
        return results; // TODO When we add dates, don't forget to add the date filter
    }

    private FluentIterable<Event> FilterByDateEnd(LocalDate dateEnd, FluentIterable<Event> results) {
        return results; // TODO When we add dates, don't forget to add the date filter
    }

    private FluentIterable<Event> FilterBySportName(final String sportName, FluentIterable<Event> results) {
        return results.filter(new Predicate<Event>() {
            @Override
            public boolean apply(Event input) {
                return StringUtils.isBlank(sportName) || input.getSport().getName().toLowerCase().equals(sportName.toLowerCase());
            }
        });
    }

    private FluentIterable<Event> FilterByTeamName(final String teamName, FluentIterable<Event> results) {
        return results.filter(new Predicate<Event>() {
            @Override
            public boolean apply(Event input) {
                return StringUtils.isBlank(teamName) || input.getTeam().getName().toLowerCase().equals(teamName.toLowerCase());
            }
        });
    }
}
