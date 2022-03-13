package org.netcracker.eventteammatessearch.security.Persistence.Entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
public class JwtUserEntity {
    @EmbeddedId
    private JwtUserKey jwtUserKey;

    private String refreshToken;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        JwtUserEntity that = (JwtUserEntity) o;
        return jwtUserKey != null && Objects.equals(jwtUserKey, that.jwtUserKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(jwtUserKey);
    }

    @Data
    @Embeddable
    public static class JwtUserKey implements Serializable {
        private String username;
        private String jwt;

    }
}

