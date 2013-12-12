package ca.ulaval.glo4003.persistence.ticketing;


import ca.ulaval.glo4003.domain.ticketing.Ticket;
import ca.ulaval.glo4003.domain.ticketing.TicketDao;
import ca.ulaval.glo4003.domain.ticketing.TicketSearchCriteria;
import ca.ulaval.glo4003.domain.ticketing.TicketState;
import ca.ulaval.glo4003.persistence.DaoPersistenceService;
import ca.ulaval.glo4003.persistence.PersistedDao;
import ca.ulaval.glo4003.persistence.UniqueConstraintValidator;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;

import java.util.Comparator;
import java.util.List;

public class PersistedTicketDao extends PersistedDao<Ticket> implements TicketDao {

    private static final Comparator<Ticket> SEAT_NUMBER_COMPARATOR = new Comparator<Ticket>() {
        public int compare(Ticket ticket1, Ticket ticket2) {
            Integer seat1 = ticket1.getSeat();
            Integer seat2 = ticket2.getSeat();
            return seat1.compareTo(seat2);
        }
    };

    @Inject
    public PersistedTicketDao(DaoPersistenceService persistenceService, UniqueConstraintValidator<Ticket>
            uniqueConstraintValidator) {
        super(persistenceService, uniqueConstraintValidator);
    }

    @Override
    public List<Ticket> search(final TicketSearchCriteria criteria) {
        FluentIterable<Ticket> results = FluentIterable.from(this.list());

        results = filterBySectionName(criteria.getSectionName(), results);
        results = filterByEventId(criteria.getEventId(), results);
        results = filterByCategoryId(criteria.getCategoryId(), results);
        results = filterByStates(criteria.getStates(), results);
        results = limitResults(criteria.getQuantity(), results);

        return results.toSortedList(SEAT_NUMBER_COMPARATOR);
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
                return eventId == null || eventId <= 0 || input.getEventId() == eventId;
            }
        });
    }

    private FluentIterable<Ticket> filterByCategoryId(final Long categoryId, FluentIterable<Ticket> results) {
        return results.filter(new Predicate<Ticket>() {
            @Override
            public boolean apply(Ticket input) {
                return categoryId == null || categoryId < 0 || input.getCategoryId() == categoryId;
            }
        });
    }

    private FluentIterable<Ticket> filterByStates(final List<TicketState> states, FluentIterable<Ticket> results) {
        return results.filter(new Predicate<Ticket>() {
            @Override
            public boolean apply(Ticket input) {
                return states == null || states.isEmpty() || states.contains(input.getState());
            }
        });
    }

    private FluentIterable<Ticket> limitResults(final Integer quantity, FluentIterable<Ticket> results) {
        if (quantity != null && quantity > 0) {
            results = results.limit(quantity);
        }

        return results;
    }
}
