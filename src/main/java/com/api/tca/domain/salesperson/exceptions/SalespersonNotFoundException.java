package com.api.tca.domain.salesperson.exceptions;

public class SalespersonNotFoundException extends RuntimeException {
    public SalespersonNotFoundException(String message) {
        super(message);
    }
}
