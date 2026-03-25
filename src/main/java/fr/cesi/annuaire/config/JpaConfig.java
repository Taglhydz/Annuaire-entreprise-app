package fr.cesi.annuaire.config;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public final class JpaConfig {

    private static final EntityManagerFactory EMF = Persistence.createEntityManagerFactory("annuairePU");

    private JpaConfig() {
    }

    public static EntityManagerFactory getEntityManagerFactory() {
        return EMF;
    }

    public static void shutdown() {
        if (EMF != null && EMF.isOpen()) {
            EMF.close();
        }
    }
}
