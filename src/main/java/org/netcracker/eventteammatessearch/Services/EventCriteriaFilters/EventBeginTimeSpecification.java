package org.netcracker.eventteammatessearch.Services.EventCriteriaFilters;

import org.netcracker.eventteammatessearch.appEntities.EventFilterData;
import org.netcracker.eventteammatessearch.entity.Event;
import org.netcracker.eventteammatessearch.entity.Event_;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.Predicate;

@Component
public class EventBeginTimeSpecification implements EventSpecificationGenerator {
    @Override
    public Specification<Event> getSpecification(EventFilterData filterData) {
       return  (root, query, criteriaBuilder) -> {
            Predicate predicate = null;
            if (filterData.getEventBeginTimeFrom() != null)
                predicate = criteriaBuilder.greaterThanOrEqualTo(root.get(Event_.DATE_TIME_START), filterData.getEventBeginTimeFrom());
            if (filterData.getEventBeginTimeTo() != null) {
                Predicate predicate1 = criteriaBuilder.lessThanOrEqualTo(root.get(Event_.DATE_TIME_START), filterData.getEventBeginTimeTo());
                if (predicate != null) {
                    predicate = criteriaBuilder.and(predicate, predicate1);
                } else predicate = predicate1;
            }
            return predicate;
        };
    }
}
