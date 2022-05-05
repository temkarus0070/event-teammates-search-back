package org.netcracker.eventteammatessearch.controllers;

import org.netcracker.eventteammatessearch.Services.SmsConfirmationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/phoneToken")
public class PhoneTokenController {
    @Autowired
    private SmsConfirmationService smsConfirmationService;

    @PostMapping("/sendToken")
    public void sendToken(Principal principal, @RequestParam String phone) {
        smsConfirmationService.sendToken(principal, phone);
    }

    @GetMapping("/checkCode")
    public boolean checkConfirmCode(@RequestParam String code, @RequestParam String phone, Principal principal) {
        return smsConfirmationService.checkToken(phone, code, principal);
    }
}
