package com.prebs.auth_service.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public class RegistrationDto {

    @JsonProperty("email")
    @NotBlank(message = "email cannot be empty")
    private String email;
    @JsonProperty("first_name")
    @NotBlank(message = "first name cannot be empty")
    private String firstName;
    @JsonProperty("last_name")
    @NotBlank(message = "last name cannot be empty")
    private String lastName;
    @JsonProperty("password")
    @NotBlank(message = "password cannot be empty")
    private String password;
    @JsonProperty("confirm_password")
    @NotBlank(message = "confirm password cannot be empty")
    private String confirmPassword;

    public RegistrationDto() {
    }

    public RegistrationDto(String email,String firstName, String lastName,String password, String confirmPassword) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.confirmPassword = confirmPassword;
    }

    public void setEmail(@NotBlank(message = "email cannot be empty") String email) {
        this.email = email;
    }

    public void setFirstName(@NotBlank(message = "first name cannot be empty") String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(@NotBlank(message = "last name cannot be empty") String lastName) {
        this.lastName = lastName;
    }

    public void setPassword(@NotBlank(message = "password cannot be empty") String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName(){
        return firstName;
    }

    public String getLastName(){
        return lastName;
    }

    public String getPassword() {
        return password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
