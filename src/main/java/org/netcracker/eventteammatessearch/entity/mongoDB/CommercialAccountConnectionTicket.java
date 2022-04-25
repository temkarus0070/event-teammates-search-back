package org.netcracker.eventteammatessearch.entity.mongoDB;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CommercialAccountConnectionTicket {
    @Id
    private String userLogin;
    private long receiptId;
    private boolean isCommercialUser;

    private String organizationName;

    private String description;
}
