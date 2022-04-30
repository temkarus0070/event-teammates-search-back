package org.netcracker.eventteammatessearch.persistence.repositories;

import org.netcracker.eventteammatessearch.entity.Complaint;
import org.netcracker.eventteammatessearch.entity.UserEventKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, UserEventKey> {
    List<Complaint> findAllByComplaintResolver_Login(String login);
}
