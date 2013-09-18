package dataAccessObjects;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;

import models.Event;

public class EventDaoInMemory implements EventDao {
	protected ArrayList<Event> events;

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
			if (currentEvent.id == id) {
				result = currentEvent;
			}
		}

		return result;
	}

	public void update(Event event) {
		//TODO
		throw new NotImplementedException();
	}

	public void delete(long id) {
		//TODO
		throw new NotImplementedException();
	}

	public List<Event> list() {
		return events;
	}
}
