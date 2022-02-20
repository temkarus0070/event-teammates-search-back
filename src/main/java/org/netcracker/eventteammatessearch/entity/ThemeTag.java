package org.netcracker.eventteammatessearch.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
public class ThemeTag {

    @EmbeddedId
    private ThemeTagKey id;

    @ManyToOne
    @MapsId("themeId")
    private Theme theme;

    @ManyToOne
    @MapsId("tagId")
    private Tag tag;

    @ManyToMany(mappedBy = "themeTags")
    private Set<Event> events;

    @ManyToMany
    private Set<User> users;
}
