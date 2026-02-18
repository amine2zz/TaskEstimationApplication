package com.proxym.recommendation.service.impl;

import com.proxym.recommendation.dto.UserDTO;
import com.proxym.recommendation.exception.EmailAlreadyInUseException;
import com.proxym.recommendation.exception.ResourceNotFoundException;
import com.proxym.recommendation.model.User;
import com.proxym.recommendation.repository.UserRepository;
import com.proxym.recommendation.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Enterprise implementation of the UserService.
 * Handles DTO mapping and secure password management.
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO getUserById(Long id) {
        return mapToDTO(getUserEntityById(id));
    }

    @Override
    public User getUserEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    @Override
    public UserDTO createUser(User user) {
        validateEmailUniqueness(user.getEmail());
        applyUserDefaults(user);
        hashPassword(user);
        return mapToDTO(userRepository.save(user));
    }

    @Override
    public UserDTO updateUser(Long id, User userDetails) {
        User existingUser = getUserEntityById(id);
        performUpdate(existingUser, userDetails);
        return mapToDTO(userRepository.save(existingUser));
    }

    @Override
    public void deleteUser(Long id) {
        User user = getUserEntityById(id);
        userRepository.delete(user);
    }

    @Override
    public UserDTO login(String email, String password) {
        return userRepository.findByEmail(email)
                .filter(u -> isPasswordCorrect(u, password))
                .map(this::mapToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid email or password"));
    }

    // --- Enterprise Mapping and Logic ---

    private UserDTO mapToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .age(user.getAge())
                .monthlyIncome(user.getMonthlyIncome())
                .balance(user.getBalance())
                .riskProfile(user.getRiskProfile())
                .financialGoals(user.getFinancialGoals())
                .build();
    }

    private void hashPassword(User user) {
        if (user.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
    }

    private void validateEmailUniqueness(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new EmailAlreadyInUseException("Email " + email + " is already in use!");
        }
    }

    private boolean isPasswordCorrect(User user, String password) {
        return passwordEncoder.matches(password, user.getPassword());
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
            target.setPassword(passwordEncoder.encode(newPassword));
        }
    }
}
