package com.api.tca.domain.address.service;

import com.api.tca.domain.address.dto.AddressDto;
import com.api.tca.domain.address.dto.AddressViaCepDto;
import com.api.tca.domain.address.entity.AddressEntity;
import com.api.tca.domain.address.exception.AddressNotFound;
import com.api.tca.domain.address.repository.AddressRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

@Service
public class AddressService {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private HttpClient httpClient;

    @Transactional
    public AddressEntity createAddress(AddressDto addressDto) {
        if (isAddressExistByPostalCode(addressDto.postalCode())) {
            return addressRepository.findAddressByPostalCode(addressDto.postalCode());
        }
        var newAddress = new AddressEntity(addressDto);
        newAddress.setPostalCode(addressDto.postalCode().replace("-", ""));
        return addressRepository.save(newAddress);
    }

    public AddressEntity findAddressByPostalCode(String postalCode) {
        var entity = addressRepository.findAddressByPostalCode(postalCode);
        if (entity == null) {
            throw new AddressNotFound("Endereço não existe no sistema.");
        }

        return entity;
    }

    public AddressDto findAddressById(UUID id) {
        var entity = addressRepository.findById(id);
        if (entity.isEmpty()) {
            throw new AddressNotFound("Endereço não existe no sistema.");
        }

        return new AddressDto(entity.get());
    }

    public boolean isAddressExistByPostalCode(String postalCode) throws AddressNotFound {
        return addressRepository.existsAddressByPostalCode(postalCode);
    }

    public AddressViaCepDto searchAddressByPostalCode(String postalCode) throws Exception {
        if (postalCode.contains("-"))
            postalCode = postalCode.replace("-", "");

        String viaCepUrl = "https://viacep.com.br/ws/[cep]/json/";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(viaCepUrl.replace("[cep]", postalCode)))
                .header("Accept", "application/json")
                .GET()
                .build();

        var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200)
            throw new AddressNotFound("Nenhum endereço encontrado");

        return new ObjectMapper().readValue(response.body(), AddressViaCepDto.class);
    }

    public AddressEntity findOrCreateAddressByPostalCode(AddressDto address) {
        String formattedCep = address.postalCode().replace("-", "");
        return isAddressExistByPostalCode(formattedCep)
                ? findAddressByPostalCode(formattedCep)
                : createAddress(address);
    }
}
