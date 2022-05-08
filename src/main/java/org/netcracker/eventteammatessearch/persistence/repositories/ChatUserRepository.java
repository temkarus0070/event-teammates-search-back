package org.netcracker.eventteammatessearch.persistence.repositories;

import org.netcracker.eventteammatessearch.entity.ChatUser;
import org.netcracker.eventteammatessearch.entity.ChatUserKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ChatUserRepository extends JpaRepository<ChatUser, ChatUserKey> {
    @Transactional
    ChatUser findChatUserByChat_IdAndUser_Login(long chatId, String login);
}
