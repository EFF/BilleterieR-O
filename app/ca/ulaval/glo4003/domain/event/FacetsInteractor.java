package ca.ulaval.glo4003.domain.event;

import com.google.inject.Inject;

import java.util.Arrays;
import java.util.List;

public class FacetsInteractor {

    private final SportDao sportDao;

    @Inject
    public FacetsInteractor(SportDao sportDao) {
        this.sportDao = sportDao;
    }

    public List<Sport> sports() {
        return sportDao.list();
    }

    public List<Gender> genders() {
        return Arrays.asList(Gender.values());
    }
}
