package org.netcracker.eventteammatessearch.Services;

import org.netcracker.eventteammatessearch.entity.*;
import org.netcracker.eventteammatessearch.entity.mongoDB.Message;
import org.netcracker.eventteammatessearch.entity.mongoDB.sequenceGenerators.SequenceGeneratorService;
import org.netcracker.eventteammatessearch.persistence.repositories.ChatRepository;
import org.netcracker.eventteammatessearch.persistence.repositories.mongo.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ChatService {

    @Autowired
    private SequenceGeneratorService sequenceGeneratorService;

    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private MessageRepository messageRepository;

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public long createForUser(String username, Principal principal) {
        Chat chat = chatRepository.getByPrivateTrueAndChatUsersContains(username);
        if (chat == null) {
            chat = new Chat();
            chat.setPrivate(true);
            ChatUser owner = new ChatUser();
            owner.setUser(new User(principal.getName()));
            owner.setChat(chat);
            owner.setChatUserType(ChatUserType.ADMIN);

            ChatUser user = new ChatUser();
            user.setUser(new User(username));
            user.setChatUserType(ChatUserType.READ_WRITE);
            user.setChat(chat);

            Set<ChatUser> chatUsers = Set.of(user, owner);
            chat.setChatUsers(chatUsers);
            chat = chatRepository.save(chat);
        }
        return chat.getId();
    }

    public Message saveMessage(Message message) {
        long id = sequenceGeneratorService.generateSequence(Message.SEQUENCE_NAME);
        message.setId(id);
        message = messageRepository.save(message);
        return message;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public long createForEvent(Event event, Principal principal) {
        long eventId = event.getId();
        Chat chat1 = this.chatRepository.getByEvent_Id(eventId);
        if (chat1 == null) {
            Chat chat = new Chat();
            chat.setName("Чат события " + event.getName());
            event.setChat(chat);
            chat.setEvent(event);
            ChatUser admin = new ChatUser();

            admin.setChat(chat);
            admin.setChatUserType(ChatUserType.MAIN_ADMIN);
            admin.setUser(event.getOwner());

            chat1 = chatRepository.save(chat);
            ChatUserKey chatUserKey = new ChatUserKey();
            chatUserKey.setUserId(event.getOwner().getLogin());
            chatUserKey.setChatId(chat1.getId());
            admin.setId(chatUserKey);
            Set<ChatUser> set = new HashSet<>();
            set.add(admin);
            chat1.setChatUsers(set);

        }
        if (!principal.getName().equals(event.getOwner().getLogin())) {
            ChatUser chatUser = new ChatUser();
            chatUser.setUser(new User(principal.getName()));
            chatUser.setChatUserType(ChatUserType.READ_WRITE);
            chatUser.setChat(chat1);
            ChatUserKey chatUserKey = new ChatUserKey();
            chatUserKey.setUserId(principal.getName());
            chatUserKey.setChatId(chat1.getId());
            chatUser.setId(chatUserKey);
            chat1.getChatUsers().add(chatUser);
        }
        chat1 = chatRepository.save(chat1);
        return chat1.getId();
    }

    public Optional<Chat> get(long id) {
        return this.chatRepository.findById(id);
    }

    public List<Message> getMessages(long chatId) {
        return messageRepository.findMessagesByChatId(chatId);
    }

}