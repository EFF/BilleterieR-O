package ca.ulaval.glo4003;

import ca.ulaval.glo4003.api.ApiModule;
import ca.ulaval.glo4003.persistence.DaoPersistenceService;
import ca.ulaval.glo4003.persistence.PersistenceModule;
import com.google.inject.AbstractModule;

public class ApplicationModule extends AbstractModule {

    private DaoPersistenceService persistenceService;

    public ApplicationModule(DaoPersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

    @Override
    protected void configure() {
        install(new ApiModule());
        install(new PersistenceModule(persistenceService));
    }

}
