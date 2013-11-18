package ca.ulaval.glo4003.unittests.interactors;

import ca.ulaval.glo4003.interactors.StagingBootstrapperInteractor;
import ca.ulaval.glo4003.dataaccessobjects.*;
import ca.ulaval.glo4003.models.Event;
import ca.ulaval.glo4003.models.Sport;
import ca.ulaval.glo4003.models.Team;
import ca.ulaval.glo4003.models.User;
import ca.ulaval.glo4003.dataaccessobjects.EventDao;
import ca.ulaval.glo4003.dataaccessobjects.SportDao;
import ca.ulaval.glo4003.dataaccessobjects.TransactionDao;
import ca.ulaval.glo4003.dataaccessobjects.UserDao;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class StagingBootstrapInteractorTest {

    private EventDao eventDao;
    private SportDao sportDao;
    private TicketDao ticketDao;
    private TeamDao teamDao;
    private UserDao userDao;
    private TransactionDao transactionDao;
    private StagingBootstrapperInteractor bootstrap;

    //TODO add tests on userDao and ticketDao.

    @Before
    public void setUp() {
        eventDao = mock(EventDao.class);
        sportDao = mock(SportDao.class);
        ticketDao = mock(TicketDao.class);
        teamDao = mock(TeamDao.class);
        userDao = mock(UserDao.class);
        transactionDao = mock(TransactionDao.class);
        bootstrap = new StagingBootstrapperInteractor(eventDao, sportDao, userDao, ticketDao, teamDao, transactionDao);
    }

    @Test
    public void initDataShouldAddItems() {
        List<Sport> sports = new ArrayList<>();
        sports.add(mock(Sport.class));
        when(sportDao.list()).thenReturn(sports);

        List<Team> teams = new ArrayList<>();
        teams.add(mock(Team.class));
        teams.add(mock(Team.class));
        when(teamDao.list()).thenReturn(teams);

        bootstrap.initData();

        verify(eventDao, atLeastOnce()).create(any(Event.class));
        verify(sportDao, atLeastOnce()).create(any(Sport.class));
        verify(userDao, atLeastOnce()).create(any(User.class));
    }

    @Test
    public void deleteDataShouldRemoveAllItems() {
        bootstrap.initData();

        bootstrap.deleteAll();

        verify(eventDao, times(1)).deleteAll();
        verify(sportDao, times(1)).deleteAll();
        verify(userDao, times(1)).deleteAll();
        verify(transactionDao, times(1)).deleteAll();
    }
}
