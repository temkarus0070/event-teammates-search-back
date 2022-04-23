package org.netcracker.eventteammatessearch.Services;

import com.qiwi.billpayments.sdk.client.BillPaymentClient;
import com.qiwi.billpayments.sdk.client.BillPaymentClientFactory;
import com.qiwi.billpayments.sdk.model.MoneyAmount;
import com.qiwi.billpayments.sdk.model.in.CreateBillInfo;
import com.qiwi.billpayments.sdk.model.in.Customer;
import org.netcracker.eventteammatessearch.entity.PaidService;
import org.netcracker.eventteammatessearch.entity.PayingInfo;
import org.netcracker.eventteammatessearch.entity.User;
import org.netcracker.eventteammatessearch.entity.mongoDB.CommercialAccountConnectionTicket;
import org.netcracker.eventteammatessearch.persistence.repositories.PayingInfoRepository;
import org.netcracker.eventteammatessearch.persistence.repositories.UserRepository;
import org.netcracker.eventteammatessearch.persistence.repositories.mongo.CommercialAccountConnectionTicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Currency;
import java.util.List;
import java.util.Optional;

@Service
public class PayService {
    @Autowired
    private PayingInfoRepository payingInfoRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommercialAccountConnectionTicketRepository commercialAccountConnectionTicketRepository;
    @Value("${commercial_price}")
    private double commercialPrice;

    public String payForCommercial(Principal principal, User user) {
        Optional<CommercialAccountConnectionTicket> accountConnectionTicket = commercialAccountConnectionTicketRepository.findById(principal.getName());
        if (accountConnectionTicket.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ваша заявка на подключение коммерческого аккаунта еще в рассмотрении");
        }
        CommercialAccountConnectionTicket commercialAccountConnectionTicket = new CommercialAccountConnectionTicket(principal.getName(), false, user.getOrganizationName(), user.getDescription());
        commercialAccountConnectionTicketRepository.save(commercialAccountConnectionTicket);
        String secretKey = "eyJ2ZXJzaW9uIjoicmVzdF92MyIsImRhdGEiOnsibWVyY2hhbnRfaWQiOjUyNjgxMiwiYXBpX3VzZXJfaWQiOjcxNjI2MTk3LCJzZWNyZXQiOiJmZjBiZmJiM2UxYzc0MjY3YjIyZDIzOGYzMDBkNDhlYjhiNTnONPININONPN090MTg5Z**********************";
        BillPaymentClient client = BillPaymentClientFactory.createDefault(secretKey);
        PayingInfo payingInfo = new PayingInfo(1, commercialPrice, "RUB", "Pay for commercial account", ZonedDateTime.now().plus(Duration.ofHours(2)), principal.getName(), "");
        Currency currency = Currency.getInstance("RUB");
        client.createBill(new CreateBillInfo(String.valueOf(payingInfo.getBillId()), new MoneyAmount(BigDecimal.valueOf(commercialPrice), currency), payingInfo.getComment(), payingInfo.getExpirationDateTime(),
                new Customer(null, payingInfo.getPayerLogin(), null), ""))
    }

    public void getPaymentStatus(Principal principal, PaidService payService) {
        List<PayingInfo> payingInfos = payingInfoRepository.findAllByUserAndPaidService(new User(principal.getName()), payService);
        payingInfos.forEach(e -> {

        });
    }

    public void removeCommercialAccountConnectionTicket(Principal principal) {
        Optional<CommercialAccountConnectionTicket> accountConnectionTicket = commercialAccountConnectionTicketRepository.findById(principal.getName());
        if (accountConnectionTicket.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "заявка не найдена");
        }
        commercialAccountConnectionTicketRepository.delete(accountConnectionTicket.get());
        payingInfoRepository.
    }

    private void activateCommercialAccount(Principal principal) {
        Optional<CommercialAccountConnectionTicket> commercialAccountConnectionTicket = commercialAccountConnectionTicketRepository.findById(principal.getName());
        if (commercialAccountConnectionTicket.isPresent()) {
            CommercialAccountConnectionTicket accountConnectionTicket = commercialAccountConnectionTicket.get();
            User user = userRepository.getUserByLogin(principal.getName());
            user.setDescription(accountConnectionTicket.getDescription());
            user.setOrganizationName(accountConnectionTicket.getOrganizationName());
            user.setCommercialUser(true);
            userRepository.save(user);
            commercialAccountConnectionTicketRepository.delete(accountConnectionTicket);
        } else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "не найдена заявка на подключение коммерческого аккаунта");
    }
}
