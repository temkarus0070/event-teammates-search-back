package org.netcracker.eventteammatessearch.entity;

import lombok.*;
import javax.persistence.*;
import javax.persistence.Entity;

@Entity
@Getter
@Setter
@Table(name = "event_invitedguests")
public class Invite {

    @Id
    @NonNull
    private Long id;

    @NonNull
    private String login;
}
