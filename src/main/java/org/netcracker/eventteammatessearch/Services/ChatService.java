package org.netcracker.eventteammatessearch.Services;

import org.netcracker.eventteammatessearch.dao.ChatDAO;
import org.netcracker.eventteammatessearch.dao.MessagesRemainCountData;
import org.netcracker.eventteammatessearch.entity.*;
import org.netcracker.eventteammatessearch.entity.mongoDB.LastSeenChatMessage;
import org.netcracker.eventteammatessearch.entity.mongoDB.Message;
import org.netcracker.eventteammatessearch.entity.mongoDB.sequenceGenerators.SequenceGeneratorService;
import org.netcracker.eventteammatessearch.persistence.repositories.ChatRepository;
import org.netcracker.eventteammatessearch.persistence.repositories.ChatUserRepository;
import org.netcracker.eventteammatessearch.persistence.repositories.UserRepository;
import org.netcracker.eventteammatessearch.persistence.repositories.mongo.LastMessagesRepository;
import org.netcracker.eventteammatessearch.persistence.repositories.mongo.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

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

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private ChatUserRepository chatUserRepository;

    @Autowired
    private LastMessagesRepository lastMessagesRepository;

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public long createForUser(String username, Principal principal) {
        Chat chat = chatRepository.getByPrivateTrueAndChatUsersContains(username, principal.getName());
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
            chat1 = chatRepository.save(chat);

            ChatUser admin = new ChatUser();
            admin.setChat(chat);
            admin.setChatUserType(ChatUserType.MAIN_ADMIN);
            admin.setUser(event.getOwner());
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

    public Optional<Chat> get(long id, Principal principal) {
        Optional<Chat> chatOptional = this.chatRepository.findById(id);
        if (chatOptional.isPresent()) {
            Optional<LastSeenChatMessage> lastSeenChatMessage1 = lastMessagesRepository.findById(new LastSeenChatMessage.LastSeenMessageId(id, principal.getName()));
            lastSeenChatMessage1.ifPresent(lastSeenChatMessage -> chatOptional.get().setLastReadMessage(lastSeenChatMessage.getMessageId()));
        }
        return chatOptional;
    }

    public void updateLastSeenMessage(Principal principal, long chatId, long messageId) {
        LastSeenChatMessage lastSeenChatMessage = new LastSeenChatMessage();
        lastSeenChatMessage.setMessageId(messageId);
        lastSeenChatMessage.setId(new LastSeenChatMessage.LastSeenMessageId(chatId, principal.getName()));
        this.lastMessagesRepository.save(lastSeenChatMessage);
    }

    public List<Message> getMessages(long chatId) {
        return messageRepository.findMessagesByChatIdOrderBySendTime(chatId);
    }

    public Message getMessageBefore(long chatId, long messageId) {
        Message topByChatIdAndIdBefore = this.messageRepository.findTopByChatIdAndIdBeforeOrderByIdDesc(chatId, messageId);
        return topByChatIdAndIdBefore;
    }

    public List<ChatDAO> getUserChats(Principal principal) {
        List<ChatDAO> chatDAOList = new ArrayList<>();
        List<Chat> chats = this.chatRepository.getAllByChatUsersContains(principal.getName());
        if (chats != null) {
            List<Long> chatIds = chats.stream().map(Chat::getId).collect(Collectors.toList());
            Map<Long, LastSeenChatMessage> chatMessageMap = this.lastMessagesRepository.findAllById_ChatIdInAndId_Username(chatIds, principal.getName()).stream().collect(Collectors.toMap(e -> e.getId().getChatId(), e -> e));

            Map<Long, Message> messageMap = messageRepository.findByChatIdInAndOrderBySendTimeDesc(chatIds)
                    .stream().collect(Collectors.toMap((Message e) -> e.getChatId(), (Message e) -> e));

            Map<Long, Long> dataAboutRemainMessages = getDataAboutRemainMessages(chatIds, chatMessageMap);
            chats.forEach(chat -> {
                if (chat.isPrivate()) {
                    Optional<ChatUser> chatUser = chat.getChatUsers().stream().filter(e -> !e.getUser().getLogin().equals(principal.getName())).findFirst();
                    if (chatUser.isPresent()) {
                        chat.setName(chatUser.get().getUser().getFirstName() + " " + chatUser.get().getUser().getLastName());
                    } else chat.setName("PRIVATE CHAT " + chat.getId());
                }
                LastSeenChatMessage lastSeenChatMessage = chatMessageMap.get(chat.getId());
                long lastMessageId = 0;
                if (lastSeenChatMessage != null) {
                    lastMessageId = lastSeenChatMessage.getMessageId();
                }
                long count = 0;
                if (dataAboutRemainMessages.get(chat.getId()) != null) {
                    count = dataAboutRemainMessages.get(chat.getId());
                }
                Message message = messageMap.get(chat.getId());
                ChatDAO chatDAO = new ChatDAO(chat.getId(), chat.getName(), chat.isPrivate(), chat.getEvent(), message, new ArrayList<>(chat.getChatUsers()), lastMessageId, count);
                chatDAOList.add(chatDAO);
            });
        }
        return chatDAOList;
    }

    public void remove(Message message) {
        this.messageRepository.removeMessageByChatIdAndIdAndUserId(message.getChatId(), message.getId(), message.getUserId());
    }

    public void removeUserFromChat(String username, long eventId) {
        Chat byEvent_id = this.chatRepository.getByEvent_Id(eventId);
        if (byEvent_id != null) {
            chatUserRepository.deleteById(new ChatUserKey(byEvent_id.getId(), username));
        }
    }

    public Map<Long, Long> getDataAboutRemainMessages(List<Long> chatIds, Map<Long, LastSeenChatMessage> chatMessageMap) {

        Criteria criteria = null;
        List<Criteria> criteria1 = new ArrayList<>();
        for (var entry : chatMessageMap.entrySet()) {
            criteria1.add(Criteria.where("chatId").is(entry.getKey()).andOperator(Criteria.where("id").gt(entry.getValue().getMessageId())));
        }
        if (criteria1.size() > 0) {
            criteria = new Criteria().orOperator(criteria1);

            Aggregation aggregation = newAggregation(match(criteria),
                    group("chatId").count().as("count"), addFields().addField("chatId").withValueOfExpression("'$_id'").build());

            List<MessagesRemainCountData> mappedResults = mongoTemplate.aggregate(aggregation, Message.class, MessagesRemainCountData.class).getMappedResults();
            return mappedResults.stream().collect(Collectors.toMap(e -> e.getChatId(), e -> e.getCount()));
        }
        return new HashMap<>();
    }
}
