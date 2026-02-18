package com.proxym.recommendation.service;

import com.proxym.recommendation.exception.EmailAlreadyInUseException;
import com.proxym.recommendation.exception.ResourceNotFoundException;
import com.proxym.recommendation.model.User;
import com.proxym.recommendation.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service handling all user-related business logic, including authentication,
 * profile management, and data integrity constraints.
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Retrieves all registered users.
     * @return List of all users.
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Retrieves a user by their unique identifier.
     * @param id The user ID.
     * @return The found User.
     * @throws ResourceNotFoundException if user doesn't exist.
     */
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    /**
     * Creates a new user after validating email uniqueness and applying defaults.
     * @param user User data to save.
     * @return The saved user.
     */
    public User createUser(User user) {
        validateEmailUniqueness(user.getEmail());
        applyUserDefaults(user);
        return userRepository.save(user);
    }

    /**
     * Updates an existing user's profile.
     * @param id The ID of the user to update.
     * @param userDetails The new details.
     * @return The updated user.
     */
    public User updateUser(Long id, User userDetails) {
        User existingUser = getUserById(id);
        performUpdate(existingUser, userDetails);
        return userRepository.save(existingUser);
    }

    /**
     * Deletes a user by ID.
     * @param id User ID to delete.
     */
    public void deleteUser(Long id) {
        User user = getUserById(id);
        userRepository.delete(user);
    }

    /**
     * Authenticates a user based on email and password.
     * @param email User email.
     * @param password Plain-text password.
     * @return The authenticated user.
     */
    public User login(String email, String password) {
        return userRepository.findByEmail(email)
                .filter(u -> isPasswordCorrect(u, password))
                .orElseThrow(() -> new ResourceNotFoundException("Invalid email or password"));
    }

    // --- Private Helper Methods (Decomposition) ---

    private void validateEmailUniqueness(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new EmailAlreadyInUseException("Email " + email + " is already in use!");
        }
    }

    private boolean isPasswordCorrect(User user, String password) {
        return user.getPassword().equals(password);
    }

    private void applyUserDefaults(User user) {
        ensureRole(user);
        ensureBalance(user);
        ensureRiskProfile(user);
        ensureFinancialGoals(user);
    }

    private void ensureRole(User user) {
        if (user.getRole() == null) user.setRole("USER");
    }

    private void ensureBalance(User user) {
        if (user.getBalance() == null) user.setBalance(0.0);
    }

    private void ensureRiskProfile(User user) {
        if (user.getRiskProfile() == null) user.setRiskProfile("Medium");
    }

    private void ensureFinancialGoals(User user) {
        if (user.getFinancialGoals() == null) user.setFinancialGoals("Savings");
    }

    private void performUpdate(User target, User source) {
        target.setName(source.getName());
        target.setEmail(source.getEmail());
        updatePasswordIfProvided(target, source.getPassword());
        target.setRole(source.getRole());
        target.setAge(source.getAge());
        target.setMonthlyIncome(source.getMonthlyIncome());
        target.setBalance(source.getBalance());
        target.setRiskProfile(source.getRiskProfile());
        target.setFinancialGoals(source.getFinancialGoals());
    }

    private void updatePasswordIfProvided(User target, String newPassword) {
        if (newPassword != null) {
            target.setPassword(newPassword);
        }
    }
}
