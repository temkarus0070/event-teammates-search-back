package org.netcracker.eventteammatessearch.Services;

import org.netcracker.eventteammatessearch.Services.EventCriteriaFilters.EventSpecificationGenerator;
import org.netcracker.eventteammatessearch.appEntities.EventFilterData;
import org.netcracker.eventteammatessearch.entity.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EventFilterOnSpecificationsService {
    @Autowired
private List<EventSpecificationGenerator> eventSpecificationsList=new ArrayList<>();
    public Specification<Event> getSpecification(EventFilterData filterData){
        Specification<Event> specification=null;
        boolean isFirst=true;
        for (EventSpecificationGenerator eventSpecificationGenerator : eventSpecificationsList) {
            Specification<Event> generatorSpecification = eventSpecificationGenerator.getSpecification(filterData);
            if (isFirst) {
                specification= generatorSpecification;
                isFirst=false;
            }
            else specification=specification.and(generatorSpecification);

        }
        return specification;
}
}
