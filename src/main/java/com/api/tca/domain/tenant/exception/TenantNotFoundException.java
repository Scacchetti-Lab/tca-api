package com.api.tca.domain.tenant.exception;

public class TenantNotFoundException extends RuntimeException {
  public TenantNotFoundException(String message) {
    super(message);
  }
}
