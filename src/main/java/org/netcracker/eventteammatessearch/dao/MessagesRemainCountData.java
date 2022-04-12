package org.netcracker.eventteammatessearch.dao;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MessagesRemainCountData {
    private long chatId;
    private long count;
}
