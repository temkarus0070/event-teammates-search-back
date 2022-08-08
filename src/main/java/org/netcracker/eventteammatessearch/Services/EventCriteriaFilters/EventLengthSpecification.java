package org.netcracker.eventteammatessearch.Services.EventCriteriaFilters;

import org.netcracker.eventteammatessearch.appEntities.EventFilterData;
import org.netcracker.eventteammatessearch.entity.Event;
import org.netcracker.eventteammatessearch.entity.Event_;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class EventLengthSpecification implements EventSpecificationGenerator{
    @Override
    public Specification<Event> getSpecification(EventFilterData filterData) {
       return  (root, query, criteriaBuilder) -> filterData.getEventLengthFrom() != 0 || filterData.getEventLengthTo() != 0 ?
                criteriaBuilder.isTrue(criteriaBuilder.function("is_date_diff_between", Boolean.class, root.get(Event_.dateTimeStart), root.get(Event_.dateTimeEnd),
                        criteriaBuilder.literal(filterData.getEventLengthFrom()), criteriaBuilder.literal(filterData.getEventLengthTo()))) : null;
    }
}
