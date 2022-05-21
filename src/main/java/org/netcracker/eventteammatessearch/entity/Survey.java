package org.netcracker.eventteammatessearch.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NamedEntityGraph(name = "surveyGraph",attributeNodes = {@NamedAttributeNode("type")})
public class Survey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @ElementCollection
    private List<String> type;

    @NonNull
    @ElementCollection
    private List<String> format;

    @NonNull
    private LocalDateTime dateTimeStart;

    @NonNull
    private LocalDateTime dateTimeEnd;

    @NonNull
    private int minNumberOfGuests;

    @NonNull
    private int maxNumberOfGuests;

    @NonNull
    private int minPrice;

    @NonNull
    private int maxPrice;

    @NonNull
    private String location;

    @OneToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private User user;

    public void setUser(User newUser) {
        user = newUser;
    }
}
