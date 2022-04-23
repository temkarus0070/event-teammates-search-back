package org.netcracker.eventteammatessearch.controllers;

import org.netcracker.eventteammatessearch.Services.PayService;
import org.netcracker.eventteammatessearch.entity.PaidService;
import org.netcracker.eventteammatessearch.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/paid")
public class PayController {

    @Autowired
    private PayService payService;

    @PostMapping("/getReceiptAndRegisterCommAccount")
    public String payForCommercialAccount(Principal principal, @RequestBody User user) {
        return payService.payForCommercial(principal, user);
    }

    @GetMapping("/getPaymentStatus")
    public void getPaymentStatus(Principal principal, PaidService paidService) {

    }
}
