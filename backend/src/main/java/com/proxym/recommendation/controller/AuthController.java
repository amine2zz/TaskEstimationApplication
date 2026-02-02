package com.proxym.recommendation.controller;

import com.proxym.recommendation.model.User;
import com.proxym.recommendation.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email already in use!"));
        }
        
        // Default values for new users
        if (user.getRole() == null) user.setRole("USER");
        if (user.getBalance() == null) user.setBalance(0.0);
        if (user.getRiskProfile() == null) user.setRiskProfile("Medium");
        if (user.getFinancialGoals() == null) user.setFinancialGoals("Savings");
        
        User savedUser = userRepository.save(user);
        return ResponseEntity.ok(savedUser);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> loginRequest) {
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");

        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isPresent() && userOpt.get().getPassword().equals(password)) {
            return ResponseEntity.ok(userOpt.get());
        }

        return ResponseEntity.status(401).body(Map.of("message", "Invalid email or password"));
    }
}
