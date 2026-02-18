package com.proxym.recommendation.service;

import com.proxym.recommendation.dto.UserDTO;
import com.proxym.recommendation.model.User;
import java.util.List;

public interface UserService {
    List<UserDTO> getAllUsers();
    UserDTO getUserById(Long id);
    UserDTO createUser(User user);
    UserDTO updateUser(Long id, User userDetails);
    void deleteUser(Long id);
    UserDTO login(String email, String password);
    User getUserEntityById(Long id); // Hidden from API, used by other services
}
