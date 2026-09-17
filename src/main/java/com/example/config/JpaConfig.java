package com.example.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public final class JpaConfig {
    private static final EntityManagerFactory EMF = Persistence.createEntityManagerFactory("facturePU");

    private JpaConfig() {
    }

    public static EntityManager createEntityManager() {
        return EMF.createEntityManager();
    }

    public static void close() {
        if (EMF.isOpen())
            EMF.close();
    }
}
