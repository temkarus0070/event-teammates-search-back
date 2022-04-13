package org.netcracker.eventteammatessearch;

import com.sun.security.auth.UserPrincipal;
import org.netcracker.eventteammatessearch.entity.Chat;
import org.netcracker.eventteammatessearch.entity.ChatUser;
import org.netcracker.eventteammatessearch.entity.ChatUserType;
import org.netcracker.eventteammatessearch.persistence.repositories.ChatRepository;
import org.netcracker.eventteammatessearch.persistence.repositories.ChatUserRepository;
import org.netcracker.eventteammatessearch.security.Entity.JWTAuthentication;
import org.netcracker.eventteammatessearch.security.Persistence.Entity.UserDetailsManager;
import org.netcracker.eventteammatessearch.security.Providers.JwtAuthProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Map;

@Service
public class AuthInterceptor implements ChannelInterceptor {
    @Value("${jwt.secretKey}")
    private String secret;
    @Autowired
    private JwtAuthProvider jwtAuthProvider;
    @Autowired
    private UserDetailsManager userDetailsManager;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private ChatUserRepository chatUserRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        final StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor.getCommand() == StompCommand.CONNECT) {
            Object raw = message.getHeaders().get(SimpMessageHeaderAccessor.NATIVE_HEADERS);

            if (raw instanceof Map) {
                String jwt = ((ArrayList<String>) ((Map) raw).get("Authorization")).get(0);
                Authentication authenticate = jwtAuthProvider.authenticate(new JWTAuthentication(jwt, secret, userDetailsManager));
                UserPrincipal principal = new UserPrincipal(authenticate.getName());
                accessor.setUser(principal);
            }
        }
        if (accessor.getCommand() == StompCommand.SUBSCRIBE) {
            Object raw = message.getHeaders().get(SimpMessageHeaderAccessor.NATIVE_HEADERS);

            if (raw instanceof Map) {
                long chatId = Long.parseLong(((ArrayList<String>) ((Map) raw).get("chatId")).get(0));
                String name = accessor.getUser().getName();
                Chat chat = chatRepository.getByChatUsersContains(accessor.getUser().getName(), chatId);
                if (chat == null)
                    throw new AuthorizationServiceException("you cant participate at this chat");
            }
        }
        if (accessor.getCommand() == StompCommand.MESSAGE) {
            org.netcracker.eventteammatessearch.entity.mongoDB.Message messagePayload = (org.netcracker.eventteammatessearch.entity.mongoDB.Message) message.getPayload();
            if (messagePayload.isRemoved()) {
                if (messagePayload.getUserId().equals(accessor.getUser().getName())) {
                    return message;
                } else {
                    ChatUser userAtChat = chatUserRepository.findChatUserByChat_IdAndUser_Login(messagePayload.getChatId(), accessor.getUser().getName());
                    if (userAtChat.getChatUserType() == ChatUserType.ADMIN || userAtChat.getChatUserType() == ChatUserType.MAIN_ADMIN) {
                        return message;
                    } else throw new AuthorizationServiceException("you cant remove this message");
                }
            }
        }
        return message;
    }
}
