package org.netcracker.eventteammatessearch.entity;

import com.qiwi.billpayments.sdk.model.BillStatus;
import lombok.*;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SequenceGenerator(name = "seq", initialValue = 255, allocationSize = 1)
@ToString
public class PayingInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq")
    private long billId;

    private double amount;
    @Enumerated(EnumType.STRING)
    private PaidService paidService;
    private String currency;
    private String comment;
    private ZonedDateTime expirationDateTime;
    @ManyToOne
    private User user;
    private String successUrl;
    @Enumerated(EnumType.STRING)
    private BillStatus billStatus;

}
