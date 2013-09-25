package ca.ulaval.glo4003;

import ca.ulaval.glo4003.dataaccessobjects.EventDao;
import ca.ulaval.glo4003.models.Category;
import ca.ulaval.glo4003.models.Event;
import ca.ulaval.glo4003.models.Gender;
import ca.ulaval.glo4003.models.Sport;
import com.google.inject.Inject;
import org.joda.time.LocalDateTime;

public class Bootstrap {
    private final EventDao eventDao;

    @Inject
    public Bootstrap(EventDao eventDao) {
        this.eventDao = eventDao;
    }

    public void initData() {
        if (!play.Play.isDev() && !play.Play.isTest()) {
            return; // You can only bootstrap in dev or test mode
        }

        //TODO use the helper
        Sport soccer = new Sport(1, "Soccer");
        Event event1 = new Event(1, soccer, Gender.MALE);
        event1.setDate(new LocalDateTime());
        Category category1 = new Category(1, 12.0, 120);
        Category category2 = new Category(2, 8.0, 1200);

        event1.addCategory(category1);
        event1.addCategory(category2);
        eventDao.create(event1);

        Event event2 = new Event(2, soccer, Gender.FEMALE);
        event2.setDate(new LocalDateTime());
        Category category3 = new Category(3, 12.0, 120);
        Category category4 = new Category(4, 8.0, 1200);

        event2.addCategory(category3);
        event2.addCategory(category4);

        eventDao.create(event2);
    }

    public void deleteAll() {
        if (play.Play.isDev() || play.Play.isTest()) {
            while (eventDao.list().size() > 0) {
                Event currentEvent = eventDao.read(eventDao.list().size() - 1);
                eventDao.delete(currentEvent.getId());
            }
        }
    }
}
