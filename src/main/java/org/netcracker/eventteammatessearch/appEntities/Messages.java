package org.netcracker.eventteammatessearch.appEntities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.netcracker.eventteammatessearch.entity.mongoDB.Message;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Messages {
    private List<Message> messageList;
    private long count;
}
