package com.prebs.auth_service.controller;

import com.prebs.auth_service.dto.request.RoleDto;
import com.prebs.auth_service.dto.response.ListSuccessResponse;
import com.prebs.auth_service.dto.response.NoDataSuccessResponse;
import com.prebs.auth_service.dto.response.SuccessResponseDto;
import com.prebs.auth_service.enums.UserRoles;
import com.prebs.auth_service.exception.UserFoundException;
import com.prebs.auth_service.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User Management", description = "Endpoints for managing users")
public class AdminController {
    private static final Logger log = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private UserService userService;

    @Operation(summary = "Get users", description = "Retrieve a list of users based on optional filters")
    @GetMapping("/")
    public ResponseEntity<?> getUsers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Boolean isActivated) {
        log.info("Get users method activated.........................................");
        log.info("{}, {}, {}", name, email, isActivated);
        return userService.getAllUsers(name, email, isActivated);
    }

    @Operation(summary = "Get user by ID", description = "Retrieve a specific user by ID")
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable String id) throws UserFoundException {
        return userService.getUserById(id);
    }

    @Operation(summary = "Assign role to user", description = "Assigns a new role to a user")
    @PatchMapping("/{id}")
    public ResponseEntity<?> addUserRole(@Valid @PathVariable String id, @RequestBody RoleDto roleDto) throws UserFoundException {
        UserRoles role = roleDto.getRole();
        log.info(role.toString());
        return userService.addUserRole(id, role);
    }

    @Operation(summary = "Delete user", description = "Deletes a user by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUserById(@PathVariable String id) throws UserFoundException {
        return userService.deleteUserById(id);
    }
}
