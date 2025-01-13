package com.bookstore.service.impl;

import com.bookstore.dto.AuthRequestDTO;
import com.bookstore.dto.AuthResponseDTO;
import com.bookstore.dto.RegisterRequestDTO;
import com.bookstore.model.Role;
import com.bookstore.model.RoleName;
import com.bookstore.model.User;
import com.bookstore.util.JwtUtil;
import com.bookstore.repository.RoleRepository;
import com.bookstore.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.bookstore.service.AuthService;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }


    @Override
    public AuthResponseDTO register(RegisterRequestDTO request) {
        User user = new User();
        user.setUsername(request.getUsername());
//        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());

        Set<Role> roles = new HashSet<>();
        for (String roleName : request.getRoles()) {
            Role role = roleRepository.findByRoleName(RoleName.valueOf(roleName))
                    .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
            roles.add(role);
        }
        user.setRoles(roles);
        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getUsername());
        return new AuthResponseDTO(token);
    }

    @Override
    public AuthResponseDTO login(AuthRequestDTO request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        String token = jwtUtil.generateToken(user.getUsername());
        return new AuthResponseDTO(token);
    }
}
