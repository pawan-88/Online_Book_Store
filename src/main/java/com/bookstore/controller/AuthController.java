package com.bookstore.controller;

import com.bookstore.dto.AuthRequest;
import com.bookstore.dto.AuthResponse;
import com.bookstore.dto.RegisterRequest;
import com.bookstore.service.AuthService;
import com.bookstore.util.BaseResponse;
import com.bookstore.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    private AuthenticationManager manager;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthController(AuthService authService, JwtUtil jwtUtil) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
    }


    @PostMapping("/register")
    public ResponseEntity<BaseResponse<?>> register(@Valid @RequestBody RegisterRequest request) {
        String requestId = null;
        try {
            AuthResponse authResponse = authService.register(request);
            BaseResponse<AuthResponse> response = new BaseResponse<>(
                    requestId,
                    authResponse,
                    new BaseResponse.ResponseMessage("201", "User registered successfully", null)
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            BaseResponse<String> errorResponse = new BaseResponse<>(
                    requestId,
                    null,
                    new BaseResponse.ResponseMessage("400", e.getMessage(), null)
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
    @PostMapping("/login")
    public ResponseEntity<BaseResponse<?>> login(@RequestBody AuthRequest request) {
        String requestId = null;
        try {
            UserDetails userDetails = authService.loadUserByUsername(request.getUsername());
            String token = jwtUtil.generateToken(request.getUsername());
            AuthResponse response = new AuthResponse(token, request.getUsername());
            BaseResponse<AuthResponse> response1 = new BaseResponse<>(
                    requestId,
                    response,  // Send AuthResponse instead of UserDetails
                    new BaseResponse.ResponseMessage("200", "User logged in successfully", null)
            );
            return new ResponseEntity<>(response1, HttpStatus.OK);
        } catch (RuntimeException e) {
            BaseResponse<String> errorResponse = new BaseResponse<>(
                    requestId,
                    null,
                    new BaseResponse.ResponseMessage("400", e.getMessage(), null)
            );
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);  // Return error response with 400 status
        }
    }

}

