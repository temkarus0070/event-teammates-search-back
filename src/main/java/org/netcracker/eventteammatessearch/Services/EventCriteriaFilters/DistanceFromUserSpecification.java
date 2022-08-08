package org.netcracker.eventteammatessearch.Services.EventCriteriaFilters;

import org.geolatte.geom.builder.DSL;
import org.netcracker.eventteammatessearch.appEntities.EventFilterData;
import org.netcracker.eventteammatessearch.entity.Event;
import org.netcracker.eventteammatessearch.entity.Event_;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import static org.geolatte.geom.builder.DSL.g;
import static org.geolatte.geom.crs.CoordinateReferenceSystems.WGS84;

@Component
public class DistanceFromUserSpecification implements EventSpecificationGenerator{
    @Override
    public Specification<Event> getSpecification(EventFilterData filterData) {
        double[] userLocation = filterData.getUserLocation();
        return (root, query, criteriaBuilder) -> filterData.getUserLocation().length == 2 && filterData.getMaxDistance() != 0 ?
                org.hibernate.spatial.predicate.GeolatteSpatialPredicates.distanceWithin(criteriaBuilder, root.join(Event_.location).get("location"),
                        DSL.point(WGS84, g(userLocation[0], userLocation[1]))
                        , filterData.getMaxDistance()) : null;
    }
}
