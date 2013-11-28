package ca.ulaval.glo4003.domain.ticketing;

import java.util.ArrayList;
import java.util.List;

public class TicketSearchCriteria {

    private Long eventId;
    private Long categoryId;
    private String sectionName;
    private List<TicketState> states;
    private Integer quantity;

    public TicketSearchCriteria() {
        states = new ArrayList<>();
        quantity = null;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public List<TicketState> getStates() {
        return states;
    }

    public void addState(TicketState state) {
        this.states.add(state);
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getQuantity() {
        return quantity;
    }
}
