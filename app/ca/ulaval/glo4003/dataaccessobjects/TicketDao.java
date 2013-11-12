package ca.ulaval.glo4003.dataaccessobjects;


import ca.ulaval.glo4003.models.Ticket;
import ca.ulaval.glo4003.models.TicketSearchCriteria;
import ca.ulaval.glo4003.services.DaoPersistenceService;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;

import java.security.InvalidParameterException;
import java.util.List;

public class TicketDao extends PersistedDao<Ticket> {

    @Inject
    public TicketDao(DaoPersistenceService persistenceService) {
        super(persistenceService);
    }

    public List<Ticket> search(final TicketSearchCriteria criteria) throws InvalidParameterException {
        FluentIterable<Ticket> results = FluentIterable.from(this.list());

        results = filterBySectionName(criteria.getSectionName(), results);
        if (criteria.getEventId() != null) {
            results = filterByEventId(criteria.getEventId(), results);
        }
        if (criteria.getCategoryId() != null) {
            results = filterByCategoryId(criteria.getCategoryId(), results);
        }

        return results.toList();
    }

    private FluentIterable<Ticket> filterBySectionName(final String sectionName, FluentIterable<Ticket> results) {
        return results.filter(new Predicate<Ticket>() {
            @Override
            public boolean apply(Ticket input) {
                return StringUtils.isBlank(sectionName) || input.getSection().toLowerCase().equals(sectionName
                        .toLowerCase());
            }
        });
    }

    private FluentIterable<Ticket> filterByEventId(final Long eventId, FluentIterable<Ticket> results) {
        return results.filter(new Predicate<Ticket>() {
            @Override
            public boolean apply(Ticket input) {
                return eventId <= 0 || input.getEventId() == eventId;
            }
        });
    }

    private FluentIterable<Ticket> filterByCategoryId(final Long categoryId, FluentIterable<Ticket> results) {
        return results.filter(new Predicate<Ticket>() {
            @Override
            public boolean apply(Ticket input) {
                return categoryId < 0 || input.getCategoryId() == categoryId;
            }
        });
    }
}
