package org.netcracker.eventteammatessearch.persistence.repositories;

import com.qiwi.billpayments.sdk.model.BillStatus;
import org.netcracker.eventteammatessearch.entity.PaidService;
import org.netcracker.eventteammatessearch.entity.PayingInfo;
import org.netcracker.eventteammatessearch.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PayingInfoRepository extends JpaRepository<PayingInfo, Long> {
    List<PayingInfo> findAllByUserAndPaidService(User user, PaidService paidService);

    List<PayingInfo> findAllByBillStatus(BillStatus billStatus);
}
