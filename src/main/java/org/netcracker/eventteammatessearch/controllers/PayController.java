package org.netcracker.eventteammatessearch.controllers;

import org.netcracker.eventteammatessearch.Services.PayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/paid")
public class PayController {

    @Autowired
    private PayService payService;

    @GetMapping("/getReceipt")
    public String payForCommercialAccount(Principal principal) {
        payService.payForCommercial(principal);
    }
}
