package org.netcracker.eventteammatessearch.controllers;

import org.netcracker.eventteammatessearch.Services.PayService;
import org.netcracker.eventteammatessearch.entity.PaidService;
import org.netcracker.eventteammatessearch.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
    public Map<String, String> getPayingUrl(Principal principal) {
        Map map = new HashMap();
        map.put("url",     payService.getUrlForPaying(principal));
        return map;

    }

    @GetMapping("/getNewUrl")
    public Map<String, String> getNewUrl(Principal principal) {
        try {
            String newUrlForPaying = payService.getNewUrlForPaying(principal);
            Map map = new HashMap();
            map.put("url", newUrlForPaying);
            return map;
        }
        catch (Exception ex){
            System.out.println(ex);
        }
throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
}
