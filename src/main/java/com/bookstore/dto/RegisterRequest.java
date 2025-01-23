package com.bookstore.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.List;

public class RegisterRequest {

    @NotNull
    @Size(min = 3, max = 50)
    private String username;


    @NotNull
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;


    @NotNull
    @Email(message = "Email should be valid")
    private String email;
    private List<String> roles; // ["ROLE_USER", "ROLE_ADMIN"]

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
