package org.netcracker.eventteammatessearch.Services.EventCriteriaFilters;

import org.netcracker.eventteammatessearch.appEntities.EventFilterData;
import org.netcracker.eventteammatessearch.entity.Event;
import org.netcracker.eventteammatessearch.entity.Event_;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class EventThemeSpecification implements EventSpecificationGenerator{
    @Override
    public Specification<Event> getSpecification(EventFilterData filterData) {
        return (root, query, criteriaBuilder) -> filterData.getTheme() != null && !filterData.getTheme().equals("") ? criteriaBuilder.equal(root.get(Event_.theme), filterData.getTheme()) : null;
    }
}
