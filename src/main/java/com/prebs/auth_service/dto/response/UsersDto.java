package com.prebs.auth_service.dto.response;

import com.prebs.auth_service.controller.AdminController;
import com.prebs.auth_service.dto.request.RoleDto;
import com.prebs.auth_service.exception.UserFoundException;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;


public class UsersDto extends RepresentationModel<UsersDto> {
    private final String id;
    private final String email;
    private final String name;

    public UsersDto(String id, String email, String name) throws UserFoundException {
        this.id = id;
        this.email = email;
        this.name = name;

        // Adding HATEOAS links
        add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AdminController.class).getUserById(id))
                .withRel("view_user"));

        add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AdminController.class).deleteUserById(id))
                .withRel("delete_user"));

        add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AdminController.class).addUserRole(id, new RoleDto()))
                .withRel("assign_role"));
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }
}