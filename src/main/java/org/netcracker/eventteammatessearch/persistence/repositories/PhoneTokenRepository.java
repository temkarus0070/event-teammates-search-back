package org.netcracker.eventteammatessearch.persistence.repositories;

import org.netcracker.eventteammatessearch.entity.PhoneToken;
import org.netcracker.eventteammatessearch.entity.PhoneTokenKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhoneTokenRepository extends JpaRepository<PhoneToken, PhoneTokenKey> {
    List<PhoneToken> findAllById_UserId(String username);
}
