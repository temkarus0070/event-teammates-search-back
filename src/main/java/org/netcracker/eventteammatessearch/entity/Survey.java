package org.netcracker.eventteammatessearch.entity;

import lombok.NonNull;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Survey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    private String[] type;

    @NonNull
    private String[] format;

    @NonNull
    private LocalDateTime dateTimeStart;

    @NonNull
    private LocalDateTime dateTimeEnd;

    @NonNull
    private int maxNumberOfGuests;

    @NonNull
    private int price;

    @NonNull
    @ManyToOne(cascade = CascadeType.ALL)
    private Location location;

    @NonNull
    @OneToOne
    private User user;
}
