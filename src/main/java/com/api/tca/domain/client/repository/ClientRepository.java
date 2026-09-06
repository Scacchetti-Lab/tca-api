package com.api.tca.domain.client.repository;

import com.api.tca.domain.client.entity.ClientEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ClientRepository extends JpaRepository<ClientEntity, UUID> {
    Optional<ClientEntity> findClientByCnpj(String cnpj);
    Boolean existsClientByCnpj(String cnpj);
    Optional<ClientEntity> findClientByEmail(String email);
    Optional<ClientEntity> findByIdAndIsDeletedFalse(UUID id);

    Page<ClientEntity> findAllClientsByIsDeletedFalse(Pageable pageable);
}
