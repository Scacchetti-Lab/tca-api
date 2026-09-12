package com.api.tca.domain.squad.repository;

import com.api.tca.domain.squad.entity.SquadEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface SquadRepository extends JpaRepository<SquadEntity, UUID> {
    @Query("SELECT s.code FROM SquadEntity s ORDER BY s.code DESC")
    String findLastSquadCodeAdded();
    Optional<SquadEntity> findSquadByCode(String code);

}
