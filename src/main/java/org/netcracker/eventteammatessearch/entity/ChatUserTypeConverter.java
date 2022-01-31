package org.netcracker.eventteammatessearch.entity;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class ChatUserTypeConverter implements AttributeConverter<ChatUserType, String> {

    @Override
    public String convertToDatabaseColumn(ChatUserType chatUserType) {
        if (chatUserType == null) {
            return null;
        }
        return chatUserType.getCode();
    }

    @Override
    public ChatUserType convertToEntityAttribute(String code) {
        if (code == null) {
            return null;
        }

        return Stream.of(ChatUserType.values())
                .filter(c -> c.getCode().equals(code))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}