package org.netcracker.eventteammatessearch.Services;

import org.netcracker.eventteammatessearch.dao.ChatDAO;
import org.netcracker.eventteammatessearch.entity.*;
import org.netcracker.eventteammatessearch.entity.mongoDB.Message;
import org.netcracker.eventteammatessearch.entity.mongoDB.sequenceGenerators.SequenceGeneratorService;
import org.netcracker.eventteammatessearch.persistence.repositories.ChatRepository;
import org.netcracker.eventteammatessearch.persistence.repositories.UserRepository;
import org.netcracker.eventteammatessearch.persistence.repositories.mongo.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChatService {

    @Autowired
    private SequenceGeneratorService sequenceGeneratorService;

    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private UserRepository userRepository;

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public long createForUser(String username, Principal principal) {
        Chat chat = chatRepository.getByPrivateTrueAndChatUsersContains(username);
        if (chat == null) {
            chat = new Chat();
            chat.setName("");
            chat.setPrivate(true);
            chat = chatRepository.save(chat);
            ChatUser owner = new ChatUser();
            owner.setUser(new User(principal.getName()));
            owner.setChat(chat);
            owner.setChatUserType(ChatUserType.READ_WRITE);
            ChatUserKey chatUserKey = new ChatUserKey();
            chatUserKey.setChatId(chat.getId());
            chatUserKey.setUserId(principal.getName());
            owner.setId(chatUserKey);
            ChatUser user = new ChatUser();
            user.setUser(new User(username));
            user.setChatUserType(ChatUserType.READ_WRITE);
            user.setChat(chat);
            ChatUserKey chatUserKey1 = new ChatUserKey();
            chatUserKey1.setChatId(chat.getId());
            chatUserKey1.setUserId(username);
            user.setId(chatUserKey1);
            Set<ChatUser> chatUsers = new HashSet<>();
            chatUsers.add(owner);
            chatUsers.add(user);
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

    public List<ChatDAO> getUserChats(Principal principal) {
        List<ChatDAO> chatDAOList = new ArrayList<>();
        HashMap<Long, Chat> map = new HashMap<>();
        List<Chat> chats = this.chatRepository.getAllByChatUsersContains(principal.getName());
        if (chats != null) {

            List<Message> messageList = messageRepository.findByChatIdInAndOrderBySendTimeDesc(chats.stream().map(chat -> {
                if (chat.isPrivate()) {
                    Optional<ChatUser> chatUser = chat.getChatUsers().stream().filter(e -> !e.getUser().getLogin().equals(principal.getName())).findFirst();
                    if (chatUser.isPresent()) {
                        chat.setName(chatUser.get().getUser().getFirstName() + " " + chatUser.get().getUser().getLastName());
                    } else chat.setName("PRIVATE CHAT " + chat.getId());
                }
                map.put(chat.getId(), chat);
                return chat.getId();
            }).collect(Collectors.toList()));
            messageList.forEach(message -> {
                Chat chat = map.get(message.getChatId());
                List<User> users = chat.getChatUsers().stream().map(e -> e.getUser()).collect(Collectors.toList());
                ChatDAO chatDAO = new ChatDAO(message.getChatId(), chat.getName(), chat.isPrivate(), chat.getEvent(), message, users);
                chatDAOList.add(chatDAO);
            });
        }
        return chatDAOList;
    }
}
