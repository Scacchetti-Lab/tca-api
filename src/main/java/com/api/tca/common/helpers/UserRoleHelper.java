package com.api.tca.common.helpers;

import com.api.tca.domain.chat.enums.UserRoles;

public final class UserRoleHelper {

    public static UserRoles normalize(String profileName) {
        var name = profileName.toLowerCase().replace(" profile", "");
        System.out.println(name);
        return switch (name) {
            case "salesperson" -> UserRoles.SALESPERSON;
            case "manager", "director", "admin" -> UserRoles.MANAGER;
            default -> UserRoles.TOTVS_AI;
        };
    }
}
