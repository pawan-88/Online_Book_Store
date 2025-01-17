package com.bookstore.dto;

import lombok.Data;

@Data
public class AuthResponse {

    private String jwtToken;
    private String username;

    public AuthResponse(String jwtToken, String username) {
        this.jwtToken = jwtToken;
        this.username = username;
    }

    // Static factory method for creating instances
    public static AuthResponse of(String jwtToken, String username) {
        return new AuthResponse(jwtToken, username);
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public String getUsername() {
        return username;
    }

    // Optional: Override toString() for debugging
    @Override
    public String toString() {
        return "AuthResponse{jwtToken='" + jwtToken + "', username='" + username + "'}";
    }


}
