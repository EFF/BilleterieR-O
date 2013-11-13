package ca.ulaval.glo4003.unittests;

import ca.ulaval.glo4003.StagingBootstrapper;
import ca.ulaval.glo4003.dataaccessobjects.EventDao;
import ca.ulaval.glo4003.dataaccessobjects.SportDao;
import ca.ulaval.glo4003.dataaccessobjects.TransactionDao;
import ca.ulaval.glo4003.dataaccessobjects.UserDao;
import ca.ulaval.glo4003.models.Event;
import ca.ulaval.glo4003.models.Sport;
import ca.ulaval.glo4003.models.User;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class StagingBootstrapTest {

    private EventDao eventDao;
    private SportDao sportDao;
    private UserDao userDao;
    private TransactionDao transactionDao;
    private StagingBootstrapper bootstrap;

    @Before
    public void setUp() {
        eventDao = mock(EventDao.class);
        sportDao = mock(SportDao.class);
        userDao = mock(UserDao.class);
        transactionDao = mock(TransactionDao.class);
        bootstrap = new StagingBootstrapper(eventDao, sportDao, userDao, transactionDao);
    }

    @Test
    public void initDataShouldAddItems() {
        //Arrange
        List<Sport> sports = new ArrayList<>();
        sports.add(mock(Sport.class));
        when(sportDao.list()).thenReturn(sports);

        //Act
        bootstrap.initData();

        //Assert
        verify(eventDao, atLeastOnce()).create(any(Event.class));
        verify(sportDao, atLeastOnce()).create(any(Sport.class));
        verify(userDao, atLeastOnce()).create(any(User.class));
    }

    @Test
    public void deleteDataShouldRemoveAllItems() {
        //Arrange
        bootstrap.initData();

        //Act
        bootstrap.deleteAll();

        //Assert
        verify(eventDao, times(1)).deleteAll();
        verify(sportDao, times(1)).deleteAll();
        verify(userDao, times(1)).deleteAll();
        verify(transactionDao, times(1)).deleteAll();
    }
}
