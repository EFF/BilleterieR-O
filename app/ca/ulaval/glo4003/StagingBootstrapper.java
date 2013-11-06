package ca.ulaval.glo4003;

import ca.ulaval.glo4003.dataaccessobjects.EventDao;
import ca.ulaval.glo4003.dataaccessobjects.SportDao;
import ca.ulaval.glo4003.dataaccessobjects.TicketDao;
import ca.ulaval.glo4003.dataaccessobjects.UserDao;
import ca.ulaval.glo4003.models.*;
import com.google.inject.Inject;
import org.joda.time.LocalDateTime;

import java.util.Random;

public class StagingBootstrapper implements Bootstrapper {

    private final EventDao eventDao;
    private final SportDao sportDao;
    private final UserDao userDao;
    private final TicketDao ticketDao;

    @Inject
    public StagingBootstrapper(EventDao eventDao, SportDao sportDao, UserDao userDao, TicketDao ticketDao) {
        this.eventDao = eventDao;
        this.sportDao = sportDao;
        this.userDao = userDao;
        this.ticketDao = ticketDao;
    }

    @Override
    public void initData() {
        initSports();
        initEvents();
        initTicket();
        initUsers();
    }

    private void initEvents() {
        for (Sport sport : sportDao.list()) {
            int nbEvents = new Random().nextInt(19) + 1;
            for (int i = 0; i < nbEvents; i++) {
                Gender gender = (i % 2 == 0) ? Gender.MALE : Gender.FEMALE;

                Event event = new Event(sport, gender);
                LocalDateTime eventDate = new LocalDateTime();
                eventDate = eventDate.plusDays(i).withSecondOfMinute(0).withMinuteOfHour(0);
                eventDate = eventDate.withHourOfDay(18 + new Random().nextInt(4));

                event.setDate(eventDate);

                for (int j = 0; j < 3; j++) {
                    double price = new Random().nextInt(20);
                    int numberOfTickets = (new Random().nextInt(10) + 1) * 10;
                    Category category = new Category(price, numberOfTickets, j);
                    event.addCategory(category);
                }
                eventDao.create(event);
            }
        }
    }

    private void initSports() {
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
    }

    private void initTicket() {
        for (Event event : eventDao.list()) {
            for (Category category : event.getCategories()) {
                int numberOfTickets = category.getNumberOfTickets();
                while (numberOfTickets > 0) {
                    Ticket ticket = new Ticket(event.getId(), category.getId());
                    ticketDao.create(ticket);
                    numberOfTickets--;
                }
            }
        }
    }

    private void initUsers() {
        for (int i = 0; i < 5; i++) {
            User user = new User();
            user.setEmail("user" + i + "@example.com");
            user.setPassword("secret");
            userDao.create(user);
        }
    }

    @Override
    public void deleteAll() {
        eventDao.deleteAll();
        sportDao.deleteAll();
        ticketDao.deleteAll();
        userDao.deleteAll();
    }
}
