package org.netcracker.eventteammatessearch.Services;

import com.qiwi.billpayments.sdk.client.BillPaymentClient;
import com.qiwi.billpayments.sdk.client.BillPaymentClientFactory;
import com.qiwi.billpayments.sdk.model.BillStatus;
import com.qiwi.billpayments.sdk.model.MoneyAmount;
import com.qiwi.billpayments.sdk.model.in.CreateBillInfo;
import com.qiwi.billpayments.sdk.model.in.Customer;
import com.qiwi.billpayments.sdk.model.out.BillResponse;
import com.qiwi.billpayments.sdk.model.out.ResponseStatus;
import com.sun.security.auth.UserPrincipal;
import org.netcracker.eventteammatessearch.entity.PaidService;
import org.netcracker.eventteammatessearch.entity.PayingInfo;
import org.netcracker.eventteammatessearch.entity.User;
import org.netcracker.eventteammatessearch.entity.mongoDB.CommercialAccountConnectionTicket;
import org.netcracker.eventteammatessearch.persistence.repositories.PayingInfoRepository;
import org.netcracker.eventteammatessearch.persistence.repositories.UserRepository;
import org.netcracker.eventteammatessearch.persistence.repositories.mongo.CommercialAccountConnectionTicketRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class PayService {
    private static Logger logger = LoggerFactory.getLogger(PayService.class);
    private AtomicLong pendingPaymentsCount = new AtomicLong(1);
    private AtomicLong trashCount=new AtomicLong(1);
    @Autowired
    private PayingInfoRepository payingInfoRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommercialAccountConnectionTicketRepository commercialAccountConnectionTicketRepository;
    @Value("${commercial_price}")
    private double commercialPrice;

    @Value("${FRONTEND}")
    private String frontendAddress;

    private BillPaymentClient client;

    public PayService(@Value("${qiwi}")
                              String secretKey) {
        client = BillPaymentClientFactory.createDefault(secretKey);
    }

    public String payForCommercial(Principal principal, User user) {
        Optional<CommercialAccountConnectionTicket> accountConnectionTicket = commercialAccountConnectionTicketRepository.findById(principal.getName());
        if (accountConnectionTicket.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ваша заявка на подключение коммерческого аккаунта еще в рассмотрении");
        }
        User userByLogin = userRepository.getUserByLogin(principal.getName());

        Currency currency = Currency.getInstance("RUB");
        PayingInfo payingInfo = new PayingInfo(0, commercialPrice, PaidService.COMMERCIAL_ACCOUNT, "RUB", "Pay for commercial account",
                ZonedDateTime.now().plus(Duration.ofHours(1)), userByLogin, "", BillStatus.WAITING);
        payingInfoRepository.save(payingInfo);
        CommercialAccountConnectionTicket commercialAccountConnectionTicket = new CommercialAccountConnectionTicket(principal.getName(), payingInfo.getBillId(), false, user.getOrganizationName(), user.getDescription());

        commercialAccountConnectionTicketRepository.save(commercialAccountConnectionTicket);
        try {
            logger.info("TRY SEND EQUEST TO QIWI");
            BillResponse billResponse = client.createBill(new CreateBillInfo(String.valueOf(payingInfo.getBillId()), new MoneyAmount(BigDecimal.valueOf(commercialPrice),
                    currency), payingInfo.getComment(), payingInfo.getExpirationDateTime(),
                    new Customer(user.getEmail(), user.getLogin(), null), frontendAddress + "/events/map"));
            logger.info("SEND EQUEST TO QIWI");
            pendingPaymentsCount.incrementAndGet();
            payingInfo.setSuccessUrl(billResponse.getPayUrl());
            logger.info("TRY TO SAVE PAYING INFO WITH URL");
            payingInfoRepository.save(payingInfo);
            logger.info("SAVE PAYING INFO WITH URL");
            return billResponse.getPayUrl();
        } catch (Exception ex) {
            logger.error("Ошибка при оплате, payingInfo=  " + payingInfo + " \n заявка на подключения коммерческого аккаунта " + commercialAccountConnectionTicket + "\n error: " + ex.getMessage());
            commercialAccountConnectionTicketRepository.delete(commercialAccountConnectionTicket);
            payingInfoRepository.delete(payingInfo);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "ошибка при оплате "+ex.getMessage());
        }
    }

    @Transactional
    public void getPaymentStatus(Principal principal, PaidService payService) {
        List<PayingInfo> payingInfos;
        if (principal != null && payService != null) {
            payingInfos = payingInfoRepository.findAllByUserAndPaidService(new User(principal.getName()), payService);
        } else {
            payingInfos = payingInfoRepository.findAllByBillStatus(BillStatus.WAITING);
            if (payingInfos.size() == 0) {
                pendingPaymentsCount.set(0);
            } else
                pendingPaymentsCount.set(payingInfos.size());
        }
        List<PayingInfo> payingInfoListForChanged = new ArrayList<>();
        payingInfos.forEach(e -> {
            if (e.getExpirationDateTime().isBefore(ZonedDateTime.now())) {
                ZonedDateTime expirationDateTime = ZonedDateTime.of(e.getExpirationDateTime().toLocalDateTime(),e.getExpirationDateTime().getZone());
                if (expirationDateTime.plusDays(1).isBefore(ZonedDateTime.now())&&e.getBillStatus()!=BillStatus.PAID){
                    trashCount.incrementAndGet();
                }
                e.setBillStatus(BillStatus.EXPIRED);
                trashCount.incrementAndGet();
                payingInfoListForChanged.add(e);
                if (pendingPaymentsCount.get()>0)
                    pendingPaymentsCount.decrementAndGet();
            } else {
                try {
                    BillResponse response = client.getBillInfo(String.valueOf(e.getBillId()));
                    ResponseStatus responseStatus = response.getStatus();
                    if (responseStatus.getValue() == BillStatus.PAID) {
                        activateCommercialAccount(new UserPrincipal(e.getUser().getLogin()));
                        e.setBillStatus(BillStatus.PAID);
                        payingInfoListForChanged.add(e);
                        pendingPaymentsCount.decrementAndGet();
                    }
                } catch (Exception e1) {
                    logger.error("Ошибка при проверке статуса оплаты payingInfo=  " + e + "\n error: " + e1.getMessage());
                }
            }
        });
        payingInfoRepository.saveAll(payingInfoListForChanged);
    }

   private void removeTrash(){
       List<PayingInfo> payingInfos=payingInfoRepository.findAll();
       List<PayingInfo>payingInfoListForRemove=new ArrayList<>();
       payingInfos.forEach(e -> {
           if (e.getExpirationDateTime().isBefore(ZonedDateTime.now())) {
               ZonedDateTime expirationDateTime = ZonedDateTime.of(e.getExpirationDateTime().toLocalDateTime(),e.getExpirationDateTime().getZone());
               if (expirationDateTime.plusDays(1).isBefore(ZonedDateTime.now())&&e.getBillStatus()!=BillStatus.PAID){
                   try{
                       removeCommercialAccountConnectionTicket(new UserPrincipal(e.getUser().getLogin()));
                   }
                   catch (Exception ex){
                       logger.error(ex.getMessage());
                   }
                   payingInfoListForRemove.add(e);
                   if (trashCount.get()>0)
                    trashCount.decrementAndGet();
               }

           }
       });
       if (payingInfoListForRemove.size()==0){
           if (trashCount.get()>0)
               trashCount.decrementAndGet();
       }
       payingInfoRepository.deleteAll(payingInfoListForRemove);
   }


    public void removeCommercialAccountConnectionTicket(Principal principal) {
        Optional<CommercialAccountConnectionTicket> accountConnectionTicket = commercialAccountConnectionTicketRepository.findById(principal.getName());
        if (accountConnectionTicket.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "заявка не найдена");
        }
        commercialAccountConnectionTicketRepository.delete(accountConnectionTicket.get());
    }

    @Scheduled(fixedDelay =60000)
    public void checkPaymentStatuses() {
        if (pendingPaymentsCount.get() > 0) {
            getPaymentStatus(null, null);
        }
        if (trashCount.get()>0){
            removeTrash();
        }
    }

    private void activateCommercialAccount(Principal principal) {
        Optional<CommercialAccountConnectionTicket> commercialAccountConnectionTicket = commercialAccountConnectionTicketRepository.findById(principal.getName());
        if (commercialAccountConnectionTicket.isPresent()) {
            CommercialAccountConnectionTicket accountConnectionTicket = commercialAccountConnectionTicket.get();
            User user = userRepository.getUserByLogin(principal.getName());
            user.setDescription(accountConnectionTicket.getDescription());
            user.setOrganizationName(accountConnectionTicket.getOrganizationName());
            user.setCommercialUserCreated(true);
            userRepository.save(user);
            commercialAccountConnectionTicketRepository.delete(accountConnectionTicket);
        } else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "не найдена заявка на подключение коммерческого аккаунта");
    }

    public CommercialAccountConnectionTicket getExistingCommercialUserRegistration(Principal principal) {
        return commercialAccountConnectionTicketRepository.findCommercialAccountConnectionTicketByUserLogin(principal.getName());
    }

    public String getUrlForPaying(Principal principal) {
        List<CommercialAccountConnectionTicket> all = commercialAccountConnectionTicketRepository.findAllByUserLogin(principal.getName());
       if (all.size()==0){
           return null;
       }
        List<PayingInfo> allByUserAndPaidService = payingInfoRepository.findAllByUserAndPaidService(new User(principal.getName()), PaidService.COMMERCIAL_ACCOUNT);
        boolean has=false;
        for (PayingInfo payingInfo : allByUserAndPaidService) {
            if (payingInfo.getExpirationDateTime().isBefore(ZonedDateTime.now())) {
                return payingInfo.getSuccessUrl();
            }
            has=true;
        }
        logger.info("PAY HAS ="+has);
        if (has)

            return "";
        return null;
    }

    public String getNewUrlForPaying(Principal principal) {
        Optional<CommercialAccountConnectionTicket> accountConnectionTicket = commercialAccountConnectionTicketRepository.findById(principal.getName());
        if (accountConnectionTicket.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Отсутствует заявка на подключение коммерческого аккаунта");
        }
        CommercialAccountConnectionTicket commercialAccountConnectionTicket = accountConnectionTicket.get();
        User user = userRepository.getUserByLogin(principal.getName());
        Currency currency = Currency.getInstance("RUB");
        PayingInfo payingInfo = new PayingInfo(0, commercialPrice, PaidService.COMMERCIAL_ACCOUNT, "RUB", "Pay for commercial account",
                ZonedDateTime.now().plus(Duration.ofHours(1)), user, "", BillStatus.WAITING);
        payingInfoRepository.save(payingInfo);
        try {
            BillResponse billResponse = client.createBill(new CreateBillInfo(String.valueOf(payingInfo.getBillId()), new MoneyAmount(BigDecimal.valueOf(commercialPrice),
                    currency), payingInfo.getComment(), payingInfo.getExpirationDateTime(),
                    new Customer(user.getEmail(), user.getLogin(), user.getPhone()), frontendAddress + "/events/map"));
            pendingPaymentsCount.incrementAndGet();
            payingInfo.setSuccessUrl(billResponse.getPayUrl());
            payingInfoRepository.save(payingInfo);
            return billResponse.getPayUrl();
        } catch (Exception ex) {
            logger.error("Ошибка при оплате, payingInfo=  " + payingInfo + " \n заявка на подключения коммерческого аккаунта " + commercialAccountConnectionTicket + "\n error: " + ex.getMessage());
            payingInfoRepository.delete(payingInfo);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "ошибка при оплате");
        }
    }
}
