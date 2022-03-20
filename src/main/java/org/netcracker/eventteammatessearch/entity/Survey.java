package org.netcracker.eventteammatessearch.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NonNull;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
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

    @OneToOne
    @JsonIgnore
    private User user;

    public void setUser(User newUser) {
        user = newUser;
    }
}
