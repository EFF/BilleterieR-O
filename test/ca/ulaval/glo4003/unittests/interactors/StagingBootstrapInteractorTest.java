package ca.ulaval.glo4003.unittests.interactors;

import ca.ulaval.glo4003.dataaccessobjects.*;
import ca.ulaval.glo4003.interactors.StagingBootstrapperInteractor;
import ca.ulaval.glo4003.models.*;
import com.google.inject.Inject;
import org.jukito.JukitoModule;
import org.jukito.JukitoRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(JukitoRunner.class)
public class StagingBootstrapInteractorTest {

    @Inject
    private StagingBootstrapperInteractor staingBootstrapperInteractor;

    @Test
    public void initDataShouldAddItems(EventDao mockedEventDao, SportDao mockedSportDao, UserDao mockedUserDao,
                                       TeamDao mockedTeamDao) {
        List<Sport> sports = new ArrayList<>();
        sports.add(mock(Sport.class));
        when(mockedSportDao.list()).thenReturn(sports);

        List<Team> teams = new ArrayList<>();
        teams.add(mock(Team.class));
        teams.add(mock(Team.class));
        when(mockedTeamDao.list()).thenReturn(teams);

        staingBootstrapperInteractor.initData();

        verify(mockedEventDao, atLeastOnce()).create(any(Event.class));
        verify(mockedSportDao, atLeastOnce()).create(any(Sport.class));
        verify(mockedUserDao, atLeastOnce()).create(any(User.class));
        verify(mockedTeamDao, atLeastOnce()).create(any(Team.class));
        verify(mockedUserDao, atLeastOnce()).create(any(User.class));
    }

    @Test
    public void deleteDataShouldRemoveAllItems(EventDao mockedEventDao, SportDao mockedSportDao,
                                               TransactionDao mockedTransactionDao, UserDao mockedUserDao,
                                               TeamDao mockedTeamDao, TicketDao mockedTicketDao) {
        staingBootstrapperInteractor.initData();

        staingBootstrapperInteractor.deleteAll();

        verify(mockedEventDao).deleteAll();
        verify(mockedSportDao).deleteAll();
        verify(mockedUserDao).deleteAll();
        verify(mockedTeamDao).deleteAll();
        verify(mockedTicketDao).deleteAll();
        verify(mockedTransactionDao).deleteAll();
    }

    public static class TestModule extends JukitoModule {

        @Override
        protected void configureTest() {
            forceMock(UserDao.class);
            forceMock(TransactionDao.class);
        }
    }
}
