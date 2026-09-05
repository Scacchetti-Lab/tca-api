package com.api.tca.common.security;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AuditContextInterceptor {

    @PersistenceContext
    private EntityManager em;

    public void setCurrentUser(UUID userId) {
        em.createNativeQuery("SELECT set_config('app.user_id', :uid, true)")
                .setParameter("uid", userId.toString())
                .getSingleResult();
    }
}
