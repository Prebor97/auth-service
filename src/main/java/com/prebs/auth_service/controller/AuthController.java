package com.prebs.auth_service.controller;

import com.prebs.auth_service.dto.request.LoginDto;
import com.prebs.auth_service.dto.request.RegistrationDto;
import com.prebs.auth_service.exception.EmailPasswordException;
import com.prebs.auth_service.exception.UserNotActivatedException;
import com.prebs.auth_service.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Endpoints for user authentication")
public class AuthController {

    @Autowired
    private UserService userService;

    @Operation(summary = "Register a new user", description = "Registers a user with email and password")
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegistrationDto registrationDto) throws Exception {
        return userService.register(registrationDto);
    }
    @Operation(summary = "User login", description = "Logs in a user and returns a JWT token")
    @PostMapping("login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDto loginDto) throws EmailPasswordException, UserNotActivatedException {
        return userService.login(loginDto);
    }
}
