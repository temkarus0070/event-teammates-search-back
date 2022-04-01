package org.netcracker.eventteammatessearch.persistence.repositories;

import org.netcracker.eventteammatessearch.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    @Query("SELECT  c from  Chat c inner join ChatUser  cu where cu.user.login=:chatUser and c.isPrivate=true ")
    public Chat getByPrivateTrueAndChatUsersContains(String chatUser);

    public Chat getByEvent_Id(long id);
}
