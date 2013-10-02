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

public class StagingBootstrap implements Bootstrap {

    private final EventDao eventDao;
    private final SportDao sportDao;

    @Inject
    public StagingBootstrap(EventDao eventDao, SportDao sportDao) {
        this.eventDao = eventDao;
        this.sportDao = sportDao;
    }

    @Override
    public void initData() {
        Sport soccer = new Sport("Soccer");
        Sport football = new Sport("Football");
        Sport rugby = new Sport("Rugby");
        Sport volleyball = new Sport("Volleyball");
        Sport basketball = new Sport("Basketball");
        sportDao.create(soccer);
        sportDao.create(football);
        sportDao.create(rugby);
        sportDao.create(volleyball);
        sportDao.create(basketball);

        for (Sport sport : sportDao.list()) {
            int nbEvents = new Random().nextInt(20);
            for (int i = 0; i < nbEvents; i++) {
                Gender gender = (i % 2 == 0) ? Gender.MALE : Gender.FEMALE;

                Event event = new Event(sport, gender);
                LocalDateTime eventDate = new LocalDateTime();
                eventDate = eventDate.plusDays(i).withSecondOfMinute(0).withMinuteOfHour(0);
                eventDate = eventDate.withHourOfDay(18 + new Random().nextInt(4));

                event.setDate(eventDate);

                for (int j = 0; j < 3; j++) {
                    double price = new Random().nextInt(20);
                    int numberOfTickets = (new Random().nextInt(10) + 1) * 100;
                    Category category = new Category(price, numberOfTickets, new Random().nextLong());
                    event.addCategory(category);
                }
                eventDao.create(event);
            }
        }
    }

    @Override
    public void deleteAll() {
        eventDao.deleteAll();
        sportDao.deleteAll();
    }
}
