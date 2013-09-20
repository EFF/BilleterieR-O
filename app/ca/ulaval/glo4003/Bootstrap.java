package ca.ulaval.glo4003;

import ca.ulaval.glo4003.dataaccessobjects.EventDao;
import ca.ulaval.glo4003.models.Category;
import ca.ulaval.glo4003.models.Event;
import ca.ulaval.glo4003.models.Gender;
import ca.ulaval.glo4003.models.Sport;
import com.google.inject.Inject;

public class Bootstrap {
    private final EventDao eventDao;

    @Inject
    public Bootstrap(EventDao eventDao) {
        this.eventDao = eventDao;
    }

    public void initData() {
        Sport soccer = new Sport(1, "Soccer");
        Event event1 = new Event(1, soccer, Gender.MALE);
        Category category1 = new Category(1, 12.0, 120);
        Category category2 = new Category(2, 8.0, 1200);

        event1.addCategory(category1);
        event1.addCategory(category2);
        eventDao.create(event1);

        Event event2 = new Event(2, soccer, Gender.FEMALE);
        Category category3 = new Category(3, 12.0, 120);
        Category category4 = new Category(4, 8.0, 1200);

        event2.addCategory(category3);
        event2.addCategory(category4);

        eventDao.create(event2);
    }
}
