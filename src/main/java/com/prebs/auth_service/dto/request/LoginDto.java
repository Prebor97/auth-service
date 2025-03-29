package com.prebs.auth_service.dto.request;

import jakarta.validation.constraints.NotBlank;

public class LoginDto {
    @NotBlank(message = "email cannot be empty")
    private String email;
    @NotBlank(message = "password cannot be empty")
    private String password;

    public LoginDto() {
    }

    public LoginDto(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setEmail(@NotBlank(message = "email cannot be empty") String email) {
        this.email = email;
    }

    public void setPassword(@NotBlank(message = "password cannot be empty") String password) {
        this.password = password;
    }
}
