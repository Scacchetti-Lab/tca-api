package com.api.tca.domain.address.controller;

import com.api.tca.domain.address.dto.AddressDto;
import com.api.tca.domain.address.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/address")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @GetMapping("/{cep}")
    public AddressDto getAddress(@PathVariable String cep) {
        return addressService.findAddressByPostalCode(cep);
    }

    @GetMapping("/{id}")
    public AddressDto getAddress(@PathVariable UUID id) {
        return addressService.findAddressById(id);
    }

    @GetMapping("/viacep/{cep}")
    public AddressDto searchByViaCep(String cep) throws Exception {
        return addressService.searchAddressByPostalCode(cep);
    }
}
