package org.netcracker.eventteammatessearch.security.Persistence.Entity;

import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Type;
import org.netcracker.eventteammatessearch.entity.User;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class JwtUserEntity {
    @EmbeddedId
    private JwtUserKey id;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String refreshToken;


    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String userData;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        JwtUserEntity that = (JwtUserEntity) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Data
    @Embeddable
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JwtUserKey implements Serializable {
        private String username;
        @Lob
        @Type(type = "org.hibernate.type.TextType")
        private String jwt;

    }
}
