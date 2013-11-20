package ca.ulaval.glo4003.dataaccessobjects;


import ca.ulaval.glo4003.models.Ticket;
import ca.ulaval.glo4003.models.TicketSearchCriteria;
import ca.ulaval.glo4003.models.TicketState;
import ca.ulaval.glo4003.services.DaoPersistenceService;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TicketDao extends PersistedDao<Ticket> {

    Comparator<Ticket> comparatorBySeat = new Comparator<Ticket>() {
        public int compare(Ticket ticket1, Ticket ticket2) {
            Integer seat1 = ticket1.getSeat();
            Integer seat2 = ticket2.getSeat();
            return seat1.compareTo(seat2);
        }
    };

    @Inject
    public TicketDao(DaoPersistenceService persistenceService, UniqueConstraintValidator<Ticket>
            uniqueConstraintValidator) {
        super(persistenceService, uniqueConstraintValidator);
    }

    public List<Ticket> search(final TicketSearchCriteria criteria) {
        FluentIterable<Ticket> results = FluentIterable.from(this.list());

        results = filterBySectionName(criteria.getSectionName(), results);
        if (criteria.getEventId() != null) {
            results = filterByEventId(criteria.getEventId(), results);
        }
        if (criteria.getCategoryId() != null) {
            results = filterByCategoryId(criteria.getCategoryId(), results);
        }
        if (criteria.getStates() != null && criteria.getStates().size() >= 1) {
            results = filterByStates(criteria.getStates(), results);
        }

        List<Ticket> listResults = new ArrayList<>(results.toList());

        if (criteria.getQuantity() > 0) {
            listResults = listResults.subList(0, criteria.getQuantity());
        }
        Collections.sort(listResults, comparatorBySeat);
        return listResults;
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

    private FluentIterable<Ticket> filterByStates(final List<TicketState> states, FluentIterable<Ticket> results) {
        return results.filter(new Predicate<Ticket>() {
            @Override
            public boolean apply(Ticket input) {
                return states != null && states.contains(input.getState());
            }
        });
    }
}
