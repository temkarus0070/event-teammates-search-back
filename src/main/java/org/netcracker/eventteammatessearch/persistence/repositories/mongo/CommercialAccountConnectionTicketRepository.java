package org.netcracker.eventteammatessearch.persistence.repositories.mongo;

import org.netcracker.eventteammatessearch.entity.mongoDB.CommercialAccountConnectionTicket;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommercialAccountConnectionTicketRepository extends MongoRepository<CommercialAccountConnectionTicket, String> {
    CommercialAccountConnectionTicket findCommercialAccountConnectionTicketByUserLogin(String userLogin);

    List<CommercialAccountConnectionTicket> findAllByUserLogin(String userlogin);
}
