package org.netcracker.eventteammatessearch.controllers;

import org.netcracker.eventteammatessearch.Services.PayService;
import org.netcracker.eventteammatessearch.entity.PaidService;
import org.netcracker.eventteammatessearch.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/paid")
public class PayController {

    @Autowired
    private PayService payService;

    @PostMapping("/getReceiptAndRegisterCommAccount")
    public Map<String, String> payForCommercialAccount(Principal principal, @RequestBody User user) {
        Map map = new HashMap();
        map.put("url", payService.payForCommercial(principal, user));
        return map;
    }

    @GetMapping("/getPaymentStatus")
    public void getPaymentStatus(Principal principal, PaidService paidService) {

    }

    @GetMapping("/getPayingUrl")
    public String getPayingUrl(Principal principal) {
        return payService.getUrlForPaying(principal);
    }

    @GetMapping("/getNewUrl")
    public String getNewUrl(Principal principal) {
        return payService.getNewUrlForPaying(principal);
    }
}
