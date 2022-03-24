package org.netcracker.eventteammatessearch.persistence.repositories;


import org.netcracker.eventteammatessearch.entity.Survey;
import org.netcracker.eventteammatessearch.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SurveyRepository extends JpaRepository<Survey, String> {
    Survey findByUser_login(String name);

    void deleteById(long id);
}
