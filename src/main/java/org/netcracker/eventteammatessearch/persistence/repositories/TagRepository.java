package org.netcracker.eventteammatessearch.persistence.repositories;

import org.netcracker.eventteammatessearch.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<Tag, String> {
}
