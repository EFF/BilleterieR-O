package ca.ulaval.glo4003;

import ca.ulaval.glo4003.dataaccessobjects.TransactionDao;

import ca.ulaval.glo4003.dataaccessobjects.*;
import ca.ulaval.glo4003.models.*;
import com.google.inject.Inject;
import org.joda.time.LocalDateTime;

import java.util.List;
import java.util.Random;

public class StagingBootstrapper implements Bootstrapper {

    //public static final String ROUGE_ET_OR = "Rouge et Or";
    public static final String VERT_ET_OR = "Vert et Or";
    private final EventDao eventDao;
    private final SportDao sportDao;
    private final UserDao userDao;
    private final TicketDao ticketDao;
    private final TeamDao teamDao;
    private final TransactionDao transactionDao;

    @Inject
    public StagingBootstrapper(EventDao eventDao, SportDao sportDao, UserDao userDao, TicketDao ticketDao, TeamDao teamDao, TransactionDao transactionDao) {
        this.eventDao = eventDao;
        this.sportDao = sportDao;
        this.userDao = userDao;
        this.ticketDao = ticketDao;
        this.teamDao = teamDao;
        this.transactionDao = transactionDao;
    }

    @Override
    public void initData() {
        initSports();
        initTeams();
        initEvents();
        initTicket();
        initUsers();
    }

    private void initTeams() {
        Team ulaval = new Team("Rouge et Or");
        Team sherbrooke = new Team(VERT_ET_OR);

        teamDao.create(ulaval);
        teamDao.create(sherbrooke);
    }

    private void initEvents() {
        for (Sport sport : sportDao.list()) {
            int nbEvents = new Random().nextInt(5) + 1;
            for (int i = 0; i < nbEvents; i++) {
                Gender gender = (i % 2 == 0) ? Gender.MALE : Gender.FEMALE;

                Event event = new Event(sport, gender);

                List<Team> teams = teamDao.list();
                event.setHomeTeam(teams.get(0));
                event.setVisitorTeam(teams.get(1));

                LocalDateTime eventDate = new LocalDateTime();
                eventDate = eventDate.plusDays(i).withSecondOfMinute(0).withMinuteOfHour(0);
                eventDate = eventDate.withHourOfDay(18 + new Random().nextInt(4));

                event.setDate(eventDate);

                for (int j = 0; j < 3; j++) {
                    double price = new Random().nextInt(20);
                    int numberOfTickets = (new Random().nextInt(10) + 1) * 10;
                    CategoryType type = CategoryType.SEAT;
                    if (j == 0) {
                        type = CategoryType.GENERAL_ADMISSION;
                    }
                    Category category = new Category(price, numberOfTickets, j, type);
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
                    String strSection = "";
                    int seat = -1;
                    if (category.getType() == CategoryType.SEAT) {
                        strSection = "Niveau " + (new Random().nextInt(2) + 1) * 100;
                        seat = numberOfTickets;
                    }
                    Ticket ticket = new Ticket(event.getId(),
                            category.getId(),
                            strSection,
                            seat);
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
        transactionDao.deleteAll();
        teamDao.deleteAll();
    }
}
