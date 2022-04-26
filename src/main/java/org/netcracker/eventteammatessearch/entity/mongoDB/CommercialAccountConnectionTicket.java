package org.netcracker.eventteammatessearch.entity.mongoDB;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class CommercialAccountConnectionTicket {
    @Id
    private String userLogin;
    private long receiptId;
    private boolean isCommercialUser;

    private String organizationName;

    private String description;
}
