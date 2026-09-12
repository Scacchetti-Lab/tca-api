package com.api.tca.domain.address.controller;

import com.api.tca.domain.address.dto.AddressDto;
import com.api.tca.domain.address.dto.AddressViaCepDto;
import com.api.tca.domain.address.service.AddressService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/address")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @GetMapping
    public ResponseEntity<AddressDto> getAddress(@RequestParam("cep") String cep) {
        if (cep.isEmpty())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        var address = addressService.findAddressByPostalCode(cep);
        return ResponseEntity.ok(new AddressDto(address));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AddressDto> getAddress(@PathVariable UUID id) {
        var address = addressService.findAddressById(id);
        return ResponseEntity.ok(address);
    }

    @GetMapping("/viacep/{cep}")
    public ResponseEntity<AddressViaCepDto> searchByViaCep(@PathVariable String cep) throws Exception {
        var address = addressService.searchAddressByPostalCode(cep);

        return ResponseEntity.ok(address);
    }

    @PostMapping
    public ResponseEntity<AddressDto> createAddress(@RequestBody @Valid AddressDto addressDto) {
        var data = addressService.createAddress(addressDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(new AddressDto(data));
    }
}
