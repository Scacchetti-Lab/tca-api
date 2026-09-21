package com.api.tca.common.helpers;

import com.api.tca.domain.user.enums.ProfileTypes;

public final class ProfileTypeByLabel {
    public static ProfileTypes get(String label) {
        return switch (label) {
            case "Salesperson", "Salesperson Profile" -> ProfileTypes.SALESPERSON;
            case "Director", "Director Profile" -> ProfileTypes.DIRECTOR;
            case "Manager", "Manager Profile" -> ProfileTypes.MANAGER;
            default -> throw new IllegalArgumentException("Invalid label");
        };
    }
}
