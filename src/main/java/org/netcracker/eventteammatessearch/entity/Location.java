package org.netcracker.eventteammatessearch.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.annotations.Type;
import org.locationtech.jts.geom.Point;

import javax.persistence.*;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    private String name;

    @Column(columnDefinition = "geography")
    @Type(type = "org.hibernate.spatial.JTSGeometryType")
    private Point location;

    @OneToMany(mappedBy = "location")
    private Set<Event> events;
}
