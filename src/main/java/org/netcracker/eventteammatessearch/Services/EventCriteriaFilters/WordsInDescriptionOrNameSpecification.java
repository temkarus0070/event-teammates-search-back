package org.netcracker.eventteammatessearch.Services.EventCriteriaFilters;

import org.netcracker.eventteammatessearch.appEntities.EventFilterData;
import org.netcracker.eventteammatessearch.entity.Event;
import org.netcracker.eventteammatessearch.entity.Event_;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

@Component
public class WordsInDescriptionOrNameSpecification implements EventSpecificationGenerator{
    @Override
    public Specification<Event> getSpecification(EventFilterData filterData) {
       return (root, query, criteriaBuilder) -> getPredicateWithWordsInEventDescriptionOrName(root, query, criteriaBuilder, filterData.getKeyWords());
    }

    private Predicate getPredicateWithWordsInEventDescriptionOrName(Root<Event> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder, List<String> words) {
        if (words == null || words.size() == 0)
            return null;
        else {
            Predicate predicate = null;
            for (String word : words) {

                Predicate like = criteriaBuilder.like(root.get(Event_.NAME), "%" + word + "%");
                like = criteriaBuilder.or(like, criteriaBuilder.like(root.get(Event_.DESCRIPTION), "%" + word + "%"));
                if (predicate == null) {
                    predicate = like;
                } else
                    predicate = criteriaBuilder.or(like, predicate);
            }
            return predicate;
        }
    }
}
