package com.api.tca.domain.client.repository;

import com.api.tca.domain.client.dto.analyse.ClientDetailedDto;
import com.api.tca.domain.client.entity.ClientEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ClientRepository extends JpaRepository<ClientEntity, UUID> {
    Optional<ClientEntity> findClientByCnpj(String cnpj);
    Boolean existsClientByCnpj(String cnpj);
    Optional<ClientEntity> findClientByEmailAndIsDeletedFalse(String email);
    Optional<ClientEntity> findClientByNameAndIsDeletedFalse(String email);
    Optional<ClientEntity> findByIdAndIsDeletedFalse(UUID id);

    @Query("""
        SELECT new com.api.tca.domain.client.dto.analyse.ClientDetailedDto(
            c.id, c.name, c.fantasyName, c.cnpj, c.email, c.phone,
            c.revenue, c.segment, c.status,
            a.performance, a.closingProbability, a.flexibility, a.risk,
            a.financialImpact, a.financialStatus, s.name
        )
        FROM ClientEntity c
        LEFT JOIN ClientAnalysisView a ON a.clientId = c.id
        LEFT JOIN SquadEntity s ON s.Id = c.squad.id
        WHERE c.id = :clientId AND c.isDeleted = false
    """)
    Optional<ClientDetailedDto> findClientWithAnalysis(@Param("clientId") UUID clientId);

    Page<ClientEntity> findAllClientsByIsDeletedFalse(Pageable pageable);
}
