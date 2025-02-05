package com.bookstore.service.impl;

import com.bookstore.dto.AuthResponse;
import com.bookstore.dto.RegisterRequest;
import com.bookstore.model.Role;
import com.bookstore.model.RoleName;
import com.bookstore.model.User;
import com.bookstore.repository.RoleRepository;
import com.bookstore.repository.UserRepository;
import com.bookstore.service.AuthService;
import com.bookstore.util.JwtUtil;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class AuthServiceImpl implements AuthService, UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    private final EntityManager entityManager;

    @Autowired
    public AuthServiceImpl(UserRepository userRepository, RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder, JwtUtil jwtUtil, EntityManager entityManager) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.entityManager = entityManager;
    }

    //baseResponse class exception handling
    //
    @Transactional
    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username is already taken: " + request.getUsername());
        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email is already registered: " + request.getEmail());
        }

        // Hash the password before storing
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        // Insert the user using native query and get the generated ID
      userRepository.registerUser(request.getUsername(), hashedPassword, request.getEmail());

        // Fetch the user_id after insertion
        Long userId = entityManager.createQuery(
                        "SELECT u.id FROM User u WHERE u.username = :username", Long.class)
                .setParameter("username", request.getUsername())
                .getSingleResult();

        if (userId == null) {
            throw new RuntimeException("Failed to register user.");
        }
        // Assign roles to user
        Set<Role> roles = new HashSet<>();
        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            for (String roleName : request.getRoles()) {
                Role role = roleRepository.findByRoleName(roleName)
                        .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
                roles.add(role);
            }
        } else {
            Role defaultRole = roleRepository.findByRoleName("ROLE_USER")
                    .orElseThrow(() -> new RuntimeException("Default role not found: ROLE_USER"));
            roles.add(defaultRole);
        }

        // Insert user roles using native query
        String insertUserRoleSQL = "INSERT INTO user_roles (user_id, role_id) VALUES (:userId, :roleId)";
        for (Role role : roles) {
            entityManager.createNativeQuery(insertUserRoleSQL)
                    .setParameter("userId", userId)
                    .setParameter("roleId", role.getId())
                    .executeUpdate();
        }

        String token = jwtUtil.generateToken(request.getUsername());

        return AuthResponse.of(token, request.getUsername());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(user.getRoles().stream()
                        .map(role -> role.getRoleName().name())
                        .toArray(String[]::new))
                .build();
    }
}