package com.api.tca.domain.user.enums;

public enum ProfileTypes {
    SALESPERSON("Salesperson Profile"),
    MANAGER("Manager Profile"),
    DIRECTOR("Director Profile");

    public final String label;

    ProfileTypes(String label) {
        this.label = label;
    }

}
