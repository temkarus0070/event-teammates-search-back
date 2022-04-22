package org.netcracker.eventteammatessearch.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
    private String currency;
    private String comment;
    private ZonedDateTime expirationDateTime;
    private String payerLogin;
    private String successUrl;
}
