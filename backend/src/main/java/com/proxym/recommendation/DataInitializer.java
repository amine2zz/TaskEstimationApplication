package com.proxym.recommendation;

import com.proxym.recommendation.dto.UserDTO;
import com.proxym.recommendation.model.User;
import com.proxym.recommendation.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Maintenance component. No longer creates hardcoded data.
 * Purely handles system health and security migrations for the existing
 * PostgreSQL data.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserService userService;

    public DataInitializer(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("🚀 [System Check] Backend online. Connecting to PostgreSQL...");

        if (userService.getAllUsers().isEmpty()) {
            System.out.println("ℹ️ [System Check] Database is currently empty. Please add users via the API or SQL.");
        } else {
            System.out.println("🔒 [System Check] Database records found. Running security audit...");
            migrateLegacyPasswords();
        }

        System.out.println("✅ [System Check] System is ready and synchronized with PostgreSQL.");
    }

    /**
     * Audits existing database users and ensures their passwords are encrypted.
     * This is required to keep the database accessible as security standards
     * evolve.
     */
    private void migrateLegacyPasswords() {
        List<UserDTO> users = userService.getAllUsers();
        int count = 0;

        for (UserDTO userDto : users) {
            User entity = userService.getUserEntityById(userDto.getId());
            String password = entity.getPassword();

            if (isLegacyPassword(password)) {
                System.out.println("🛡️ [Security Update] Encrypting password for: " + entity.getEmail());
                // Passing the entity back through the service triggers the PasswordEncoder
                userService.updateUser(entity.getId(), entity);
                count++;
            }
        }

        if (count > 0) {
            System.out.println("✨ [Security Update] Finished. " + count + " records are now secure.");
        }
    }

    private boolean isLegacyPassword(String password) {
        // BCrypt hashes always start with $2a$ or $2b$
        return password != null && !password.startsWith("$2");
    }
}
