package com.qiwi.billpayments.sdk.client;

import com.qiwi.billpayments.sdk.web.ApacheWebClient;
import com.qiwi.billpayments.sdk.web.WebClient;
import org.apache.http.impl.client.HttpClients;

public class BillPaymentClientFactory {
    public static com.qiwi.billpayments.sdk.client.BillPaymentClient createDefault(String secretKey) {
        return new com.qiwi.billpayments.sdk.client.BillPaymentClient(
                secretKey,
                new ApacheWebClient(HttpClients.createDefault())
        );
    }

    public static com.qiwi.billpayments.sdk.client.BillPaymentClient createCustom(String secretKey, WebClient webClient) {
        return new BillPaymentClient(secretKey, webClient);
    }
}
