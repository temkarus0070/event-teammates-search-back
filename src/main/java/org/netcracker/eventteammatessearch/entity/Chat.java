package org.netcracker.eventteammatessearch.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@NamedEntityGraph(name = "chat-graph",attributeNodes = {@NamedAttributeNode(value = "chatUsers",subgraph = "chat-user-graph")},
subgraphs = {@NamedSubgraph(name = "chat-user-graph",attributeNodes = {@NamedAttributeNode(value = "user",subgraph = "user-subgraph")}),
@NamedSubgraph(name = "user-subgraph",type = User.class,attributeNodes = {@NamedAttributeNode("surveyResult")})})
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private boolean isPrivate;

    @OneToMany(mappedBy = "chat", cascade = CascadeType.PERSIST)
    @ToString.Exclude
    private Set<ChatUser> chatUsers;

    @OneToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @JsonIgnore
    private Event event;

    @Transient
    private long lastReadMessage;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Chat chat = (Chat) o;
        return id != null && Objects.equals(id, chat.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
