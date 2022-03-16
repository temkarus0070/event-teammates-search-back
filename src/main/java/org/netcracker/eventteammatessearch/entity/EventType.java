package org.netcracker.eventteammatessearch.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@NoArgsConstructor
@Table(name = "eventTypes")
public class EventType {
    @Id
    private String name;
}
