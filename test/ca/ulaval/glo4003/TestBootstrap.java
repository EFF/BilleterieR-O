package ca.ulaval.glo4003;

import ca.ulaval.glo4003.dataaccessobjects.EventDao;
import ca.ulaval.glo4003.dataaccessobjects.SportDao;
import ca.ulaval.glo4003.models.Category;
import ca.ulaval.glo4003.models.Event;
import ca.ulaval.glo4003.models.Gender;
import ca.ulaval.glo4003.models.Sport;
import com.google.inject.Inject;
import org.joda.time.LocalDateTime;

import java.util.Random;

public class TestBootstrap implements Bootstrap {

    private final EventDao eventDao;
    private final SportDao sportDao;

    @Inject
    public TestBootstrap(EventDao eventDao, SportDao sportDao) {
        this.eventDao = eventDao;
        this.sportDao = sportDao;
    }

    @Override
    public void initData() {
        Sport soccer = new Sport("Soccer");
        Sport golf = new Sport("Golf");
        sportDao.create(soccer);
        sportDao.create(golf);

        Event event1 = new Event(soccer, Gender.MALE);
        event1.setDate(new LocalDateTime());
        Category category1 = new Category(12.0, 120, 1);
        Category category2 = new Category(8.0, 1200, 2);

        event1.addCategory(category1);
        event1.addCategory(category2);
        eventDao.create(event1);

        Event event2 = new Event(soccer, Gender.FEMALE);
        event2.setDate(new LocalDateTime());
        Category category3 = new Category(12.0, 120, 3);
        Category category4 = new Category(8.0, 1200, 4);

        event2.addCategory(category3);
        event2.addCategory(category4);

        eventDao.create(event2);
    }

    @Override
    public void deleteAll() {
        eventDao.deleteAll();
        sportDao.deleteAll();
    }
}
