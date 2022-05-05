package org.netcracker.eventteammatessearch.Services;

import org.netcracker.eventteammatessearch.entity.PhoneToken;
import org.netcracker.eventteammatessearch.persistence.repositories.PhoneTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class PhoneTokenService {
    @Autowired
    private PhoneTokenRepository phoneTokenRepository;


    public boolean checkIfHasNotExpired(Principal principal) {
        List<PhoneToken> phoneTokenList = phoneTokenRepository.findAllById_UserId(principal.getName());
        if (phoneTokenList.size() == 0)
            return false;
        for (PhoneToken phoneToken : phoneTokenList) {
            if (phoneToken.getId().getDateTimeSend().plus(10, ChronoUnit.MINUTES).isAfter(LocalDateTime.now())) {
                return true;
            }
        }
        return false;
    }

    public void add(PhoneToken phoneToken) {
        phoneTokenRepository.save(phoneToken);
    }
}
