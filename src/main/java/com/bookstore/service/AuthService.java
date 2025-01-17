package com.bookstore.service;


import com.bookstore.dto.AuthRequest;
import com.bookstore.dto.AuthResponse;
import com.bookstore.dto.RegisterRequest;
import org.springframework.security.core.userdetails.UserDetails;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

//    AuthResponse login(AuthRequest request);

    UserDetails loadUserByUsername(String username);
}
