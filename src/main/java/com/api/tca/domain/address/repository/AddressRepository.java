package com.api.tca.domain.address.repository;

import com.api.tca.domain.address.entity.AddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AddressRepository extends JpaRepository<AddressEntity, UUID> {
    boolean existsAddressByPostalCode(String postalCode);
    AddressEntity findAddressByPostalCode(String postalCode);
}
