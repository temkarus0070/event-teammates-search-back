package org.netcracker.eventteammatessearch.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
public class ChatUser {
    @EmbeddedId
    private ChatUserKey id;

    @ManyToOne
    @MapsId("chatId")
    @JsonIgnore
    private Chat chat;

    @ManyToOne
    @MapsId("userId")
    private User user;

    @Enumerated
    private ChatUserType chatUserType;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ChatUser chatUser = (ChatUser) o;
        return id != null && Objects.equals(id, chatUser.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
