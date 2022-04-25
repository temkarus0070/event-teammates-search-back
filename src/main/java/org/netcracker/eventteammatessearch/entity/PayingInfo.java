package org.netcracker.eventteammatessearch.entity;

import com.qiwi.billpayments.sdk.model.BillStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PayingInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
