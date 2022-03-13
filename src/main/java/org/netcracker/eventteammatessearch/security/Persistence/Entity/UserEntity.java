package org.netcracker.eventteammatessearch.security.Persistence.Entity;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {
    @Id
    private String username;
    private String password;

    @ElementCollection
    private List<GrantedAuthority> authorities;
}
