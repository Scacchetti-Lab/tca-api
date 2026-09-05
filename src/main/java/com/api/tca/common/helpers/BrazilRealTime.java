package com.api.tca.common.helpers;

import java.time.LocalDateTime;
import java.time.ZoneId;

public final class BrazilRealTime {

    public static LocalDateTime now() {
        return LocalDateTime.now(ZoneId.of("America/Sao_Paulo"));
    }
}
