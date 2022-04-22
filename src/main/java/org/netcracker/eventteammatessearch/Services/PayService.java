package org.netcracker.eventteammatessearch.Services;

import com.qiwi.billpayments.sdk.client.BillPaymentClient;
import com.qiwi.billpayments.sdk.client.BillPaymentClientFactory;
import com.qiwi.billpayments.sdk.model.MoneyAmount;
import com.qiwi.billpayments.sdk.model.in.CreateBillInfo;
import com.qiwi.billpayments.sdk.model.in.Customer;
import org.netcracker.eventteammatessearch.entity.PayingInfo;
import org.netcracker.eventteammatessearch.persistence.repositories.PayingInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Currency;

@Service
public class PayService {
    @Autowired
    private PayingInfoRepository payingInfoRepository;

    @Value("${commercial_price}")
    private double commercialPrice;

    public String payForCommercial(Principal principal) {
        String secretKey = "eyJ2ZXJzaW9uIjoicmVzdF92MyIsImRhdGEiOnsibWVyY2hhbnRfaWQiOjUyNjgxMiwiYXBpX3VzZXJfaWQiOjcxNjI2MTk3LCJzZWNyZXQiOiJmZjBiZmJiM2UxYzc0MjY3YjIyZDIzOGYzMDBkNDhlYjhiNTnONPININONPN090MTg5Z**********************";
        BillPaymentClient client = BillPaymentClientFactory.createDefault(secretKey);
        PayingInfo payingInfo = new PayingInfo(1, commercialPrice, "RUB", "Pay for commercial account", ZonedDateTime.now().plus(Duration.ofHours(2)), principal.getName(), "");
        Currency currency = Currency.getInstance("RUB");
        client.createBill(new CreateBillInfo(String.valueOf(payingInfo.getBillId()), new MoneyAmount(BigDecimal.valueOf(commercialPrice), currency), payingInfo.getComment(), payingInfo.getExpirationDateTime(),
                new Customer(null, payingInfo.getPayerLogin(), null), ""))
    }
}
