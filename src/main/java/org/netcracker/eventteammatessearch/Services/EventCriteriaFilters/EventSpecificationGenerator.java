package org.netcracker.eventteammatessearch.Services.EventCriteriaFilters;

import org.netcracker.eventteammatessearch.appEntities.EventFilterData;
import org.netcracker.eventteammatessearch.entity.Event;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public interface EventSpecificationGenerator {
    public Specification<Event> getSpecification(EventFilterData filterData);
}
