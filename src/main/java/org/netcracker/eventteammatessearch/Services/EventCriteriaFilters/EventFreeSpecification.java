package org.netcracker.eventteammatessearch.Services.EventCriteriaFilters;

import org.netcracker.eventteammatessearch.appEntities.EventFilterData;
import org.netcracker.eventteammatessearch.entity.Event;
import org.netcracker.eventteammatessearch.entity.Event_;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.Predicate;

@Component
public class EventFreeSpecification implements EventSpecificationGenerator{
    @Override
    public Specification<Event> getSpecification(EventFilterData filterData) {
        return (root, query, criteriaBuilder) -> filterData.isFreeEvents() ? criteriaBuilder.equal(root.get(Event_.PRICE), 0) : null;
    }
}
