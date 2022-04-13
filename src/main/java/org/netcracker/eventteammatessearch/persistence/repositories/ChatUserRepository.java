package org.netcracker.eventteammatessearch.persistence.repositories;

import org.netcracker.eventteammatessearch.entity.ChatUser;
import org.netcracker.eventteammatessearch.entity.ChatUserKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatUserRepository extends JpaRepository<ChatUser, ChatUserKey> {
    ChatUser findChatUserByChat_IdAndUser_Login(long chatId, String login);
}
