package ca.ulaval.glo4003.dataaccessobjects;

import ca.ulaval.glo4003.exceptions.MaximumExceededException;
import ca.ulaval.glo4003.exceptions.RecordNotFoundException;
import ca.ulaval.glo4003.models.Category;
import ca.ulaval.glo4003.models.Event;
import ca.ulaval.glo4003.models.EventSearchCriteria;
import ca.ulaval.glo4003.models.Gender;
import ca.ulaval.glo4003.services.DaoPersistenceService;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDateTime;

import javax.annotation.Nullable;
import java.security.InvalidParameterException;
import java.util.List;

public class EventDao extends PersistedDao<Event> {

    @Inject
    public EventDao(DaoPersistenceService persistenceService, UniqueConstraintValidator<Event>
            uniqueConstraintValidator) {
        super(persistenceService, uniqueConstraintValidator);
    }

    public List<Event> search(final EventSearchCriteria criteria) throws InvalidParameterException {
        FluentIterable<Event> results = FluentIterable.from(this.list());

        if (criteria.getDateStart() != null && criteria.getDateEnd() != null) {
            if (criteria.getDateEnd().isBefore(criteria.getDateStart())) {
                String errorFormat = "End Date %s can't be before Start Date %s";
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

    public void decrementEventCategoryNumberOfTickets(long eventId, long categoryId, int numberOfTickets)
            throws RecordNotFoundException, MaximumExceededException {
        Category category = findCategory(eventId, categoryId);
        category.decrementNumberOfTickets(numberOfTickets);
    }

    public void incrementEventCategoryNumberOfTickets(Long eventId, Long categoryId, int numberOfTickets)
            throws RecordNotFoundException {
        Category category = findCategory(eventId, categoryId);
        category.incrementNumberOfTickets(numberOfTickets);
    }

    private FluentIterable<Event> filterByDateStart(final LocalDateTime dateStart, FluentIterable<Event> results) {
        return results.filter(new Predicate<Event>() {
            @Override
            public boolean apply(@Nullable Event event) {
                return dateStart == null || event.getDate().isAfter(dateStart) || event.getDate().isEqual(dateStart);
            }
        });
    }

    private FluentIterable<Event> filterByDateEnd(final LocalDateTime dateEnd, FluentIterable<Event> results) {
        return results.filter(new Predicate<Event>() {
            @Override
            public boolean apply(@Nullable Event event) {
                return dateEnd == null || event.getDate().isBefore(dateEnd) || event.getDate().isEqual(dateEnd);
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
