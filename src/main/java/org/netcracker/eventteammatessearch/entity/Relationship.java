package org.netcracker.eventteammatessearch.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
public class Relationship {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @JoinColumn(name = "rs_fromuser", nullable = false, updatable = false, referencedColumnName = "login")
    @OneToOne(optional = false)
    private User owner;

    @JoinColumn(name = "rs_touser", nullable = false, updatable = false, referencedColumnName = "login")
    @OneToOne(optional = false)
    private User friend;


    private boolean isFriend;

    public void setIsFriend(boolean tempFriend) {
        isFriend = tempFriend;
    }

    public boolean getIsFriend() { return isFriend; }
}
