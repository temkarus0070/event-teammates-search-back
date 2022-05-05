package org.netcracker.eventteammatessearch.Services;


import com.twilio.Twilio;
import com.twilio.rest.verify.v2.Service;
import com.twilio.rest.verify.v2.service.VerificationCheck;
import org.netcracker.eventteammatessearch.entity.PhoneToken;
import org.netcracker.eventteammatessearch.entity.PhoneTokenKey;
import org.netcracker.eventteammatessearch.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.time.LocalDateTime;

@org.springframework.stereotype.Service
public class SmsConfirmationService {

    @Value("${twilio.name}")
    private String serviceName;

    @Autowired
    private UserService userService;

    @Autowired
    private PhoneTokenService phoneTokenService;


    private Service service;

    public SmsConfirmationService(@Value("${twilio.sid}")
                                          String accountSid,
                                  @Value("${twilio.token}")
                                          String authToken) {
        Twilio.init(accountSid, authToken);
        service = Service.creator("auth service").create();
    }


    public void sendToken(Principal principal, String phone) {
        if (!phoneTokenService.checkIfHasNotExpired(principal)) {
            //   Verification sms = Verification.creator(serviceName, phone, "sms").create();
            PhoneToken phoneToken = new PhoneToken();
            phoneToken.setId(new PhoneTokenKey(LocalDateTime.now(), principal.getName()));
            phoneToken.setPhone(phone);
            phoneToken.setUser(new User(principal.getName()));
            phoneTokenService.add(phoneToken);
        } else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Вам уже был отправлен код, запросите повторную отправку через 10 минут");

    }

    public boolean checkToken(String phone, String code, Principal principal) {
        VerificationCheck verificationCheck = VerificationCheck.creator(serviceName, code).create();
        if (verificationCheck.getTo().equals(phone)) {
            String status = verificationCheck.getStatus();
            if (status.equals("approved")) {
                try {
                    userService.setPhoneConfirmed(principal);
                    return true;
                } catch (Exception ex) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
                }
            }
            return false;
        } else throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ошибка чтения кода");
    }
}
