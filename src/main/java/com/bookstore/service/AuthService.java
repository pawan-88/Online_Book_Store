package com.bookstore.service;

import com.bookstore.dto.AuthRequestDTO;
import com.bookstore.dto.AuthResponseDTO;
import com.bookstore.dto.RegisterRequestDTO;

public interface AuthService {

    AuthResponseDTO register(RegisterRequestDTO request);

    AuthResponseDTO login(AuthRequestDTO request);
}
