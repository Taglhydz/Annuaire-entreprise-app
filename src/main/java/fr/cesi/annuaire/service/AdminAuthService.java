package fr.cesi.annuaire.service;

import fr.cesi.annuaire.repository.AdminUserRepository;
import jakarta.persistence.EntityManager;
import org.mindrot.jbcrypt.BCrypt;

public class AdminAuthService {

    private final AdminUserRepository adminUserRepository;

    public AdminAuthService(EntityManager entityManager) {
        this.adminUserRepository = new AdminUserRepository(entityManager);
    }

    public boolean authenticate(String username, String plainPassword) {
        if (username == null || username.isBlank() || plainPassword == null || plainPassword.isBlank()) {
            return false;
        }

        return adminUserRepository.findByUsername(username.trim())
                .map(admin -> BCrypt.checkpw(plainPassword, admin.getPasswordHash()))
                .orElse(false);
    }
}
