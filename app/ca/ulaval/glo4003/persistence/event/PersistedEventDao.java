package ca.ulaval.glo4003.persistence.event;

import ca.ulaval.glo4003.domain.RecordNotFoundException;
import ca.ulaval.glo4003.domain.event.*;
import ca.ulaval.glo4003.persistence.DaoPersistenceService;
import ca.ulaval.glo4003.persistence.PersistedDao;
import ca.ulaval.glo4003.persistence.UniqueConstraintValidator;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDateTime;

import javax.annotation.Nullable;
import java.security.InvalidParameterException;
import java.util.List;

public class PersistedEventDao extends PersistedDao<Event> implements EventDao {

    @Inject
    public PersistedEventDao(DaoPersistenceService persistenceService, UniqueConstraintValidator<Event>
            uniqueConstraintValidator) {
        super(persistenceService, uniqueConstraintValidator);
    }

    public List<Event> search(final EventSearchCriteria criteria) throws InvalidParameterException {
        FluentIterable<Event> results = FluentIterable.from(this.list());

        if (criteria.getDateStart() != null && criteria.getDateEnd() != null) {
            if (criteria.getDateEnd().isBefore(criteria.getDateStart())) {
                String errorFormat = PersitenceEventConstantsManager.END_DATE_CANNOT_BE_BEFORE_START_DATE_ERROR_MESSAGE_FORMAT;
                String error = String.format(errorFormat, criteria.getDateEnd().toString(),
                        criteria.getDateStart().toString());
                throw new InvalidParameterException(error);
            }
        }

        results = filterBySportName(criteria.getSportName(), results);
        results = filterByTeamName(criteria.getTeamName(), results);
        results = filterByDateStart(criteria.getDateStart(), results);
        results = filterByDateEnd(criteria.getDateEnd(), results);
        results = filterByGender(criteria.getGender(), results);

        return results.toList();
    }

    public Category findCategory(long eventId, long categoryId) throws RecordNotFoundException {
        Event event = read(eventId);
        List<Category> categories = event.getCategories();
        for (Category category : categories) {
            if (category.getId() == categoryId) {
                return category;
            }
        }
        throw new RecordNotFoundException();
    }

    private FluentIterable<Event> filterByDateStart(final LocalDateTime dateStart, FluentIterable<Event> results) {
        return results.filter(new Predicate<Event>() {
            @Override
            public boolean apply(@Nullable Event event) {
                return event != null && (dateStart == null || event.getDate().isAfter(dateStart)
                        || event.getDate().isEqual(dateStart));
            }
        });
    }

    private FluentIterable<Event> filterByDateEnd(final LocalDateTime dateEnd, FluentIterable<Event> results) {
        return results.filter(new Predicate<Event>() {
            @Override
            public boolean apply(@Nullable Event event) {
                return event != null && (dateEnd == null || event.getDate().isBefore(dateEnd)
                        || event.getDate().isEqual(dateEnd));
            }
        });
    }

    private FluentIterable<Event> filterBySportName(final String sportName, FluentIterable<Event> results) {
        return results.filter(new Predicate<Event>() {
            @Override
            public boolean apply(Event input) {
                return StringUtils.isBlank(sportName) || input.getSport().getName().toLowerCase().equals(sportName
                        .toLowerCase());
            }
        });
    }

    private FluentIterable<Event> filterByTeamName(final String teamName, FluentIterable<Event> results) {
        return results.filter(new Predicate<Event>() {
            @Override
            public boolean apply(Event input) {
                return StringUtils.isBlank(teamName) || input.getHomeTeam().getName().toLowerCase().equals(teamName
                        .toLowerCase());
            }
        });
    }

    private FluentIterable<Event> filterByGender(final Gender gender, FluentIterable<Event> results) {
        return results.filter(new Predicate<Event>() {
            @Override
            public boolean apply(Event input) {
                return gender == null || input.getGender().equals(gender);
            }
        });
    }
}
