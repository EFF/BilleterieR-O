package ca.ulaval.glo4003.dataaccessobjects;

import ca.ulaval.glo4003.models.Event;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EventDaoInMemory implements EventDao {

    private ArrayList<Event> events;

    public EventDaoInMemory() {
        events = new ArrayList<Event>();
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
}
