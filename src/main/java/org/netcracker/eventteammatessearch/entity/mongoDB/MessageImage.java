package org.netcracker.eventteammatessearch.entity.mongoDB;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Getter
@Setter
@NoArgsConstructor
public class MessageImage {
    @Id
    private Long id;

    private String imageURL;

    private Long messageId;
}
