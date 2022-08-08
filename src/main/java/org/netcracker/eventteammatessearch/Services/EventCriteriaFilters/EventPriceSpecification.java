package org.netcracker.eventteammatessearch.Services.EventCriteriaFilters;

import org.netcracker.eventteammatessearch.appEntities.EventFilterData;
import org.netcracker.eventteammatessearch.entity.Event;
import org.netcracker.eventteammatessearch.entity.Event_;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.Predicate;

@Component
public class EventPriceSpecification implements EventSpecificationGenerator{
    @Override
    public Specification<Event> getSpecification(EventFilterData filterData) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = null;
            if (filterData.getPriceFrom() != 0)
                predicate = criteriaBuilder.greaterThanOrEqualTo(root.get(Event_.price), filterData.getPriceFrom());
            if (filterData.getPriceTo() != 0) {
                Predicate predicate1 = criteriaBuilder.lessThanOrEqualTo(root.get(Event_.price), filterData.getPriceTo());
                if (filterData.getPriceFrom() != 0) {
                    predicate = criteriaBuilder.and(predicate1, predicate);
                } else predicate = predicate1;
            }
            return predicate;
        };
    }
}
