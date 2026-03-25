package fr.cesi.annuaire.repository;

import fr.cesi.annuaire.entity.AdminUser;
import jakarta.persistence.EntityManager;

import java.util.Optional;

public class AdminUserRepository {

    private final EntityManager entityManager;

    public AdminUserRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Optional<AdminUser> findByUsername(String username) {
        return entityManager.createQuery(
                        "SELECT a FROM AdminUser a WHERE a.username = :username",
                        AdminUser.class)
                .setParameter("username", username)
                .getResultStream()
                .findFirst();
    }
}
