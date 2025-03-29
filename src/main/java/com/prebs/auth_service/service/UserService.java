package com.prebs.auth_service.service;

import com.prebs.auth_service.controller.AdminController;
import com.prebs.auth_service.dto.request.LoginDto;
import com.prebs.auth_service.dto.request.RegistrationDto;
import com.prebs.auth_service.dto.response.ListSuccessResponse;
import com.prebs.auth_service.dto.response.NoDataSuccessResponse;
import com.prebs.auth_service.dto.response.SuccessResponseDto;
import com.prebs.auth_service.dto.response.UsersDto;
import com.prebs.auth_service.enums.UserRoles;
import com.prebs.auth_service.exception.*;
import com.prebs.auth_service.model.User;
import com.prebs.auth_service.util.JwtUtil;
import com.prebs.auth_service.repository.UserRepository;
import com.prebs.auth_service.util.MapUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    MapUtil mapUtil;

    @Autowired
    PasswordEncoder passwordEncoder;

    public ResponseEntity<?> register(RegistrationDto registrationDto) throws Exception {
        Optional<User> optionalUser = userRepository.findByEmail(registrationDto.getEmail());

        if (optionalUser.isPresent()) {
            throw new UserFoundException("User with email " + registrationDto.getEmail() + " already exists");
        }
        if (!registrationDto.getPassword().equals(registrationDto.getConfirmPassword())){
            throw new PasswordMismatchException("password do not match for email "+registrationDto.getEmail());
        }
        User user = new User();
        User savedUser = mapUtil.mapRegistration(user,registrationDto);
        userRepository.save(savedUser);
        Optional<User> optional = userRepository.findByEmail(savedUser.getEmail());
        if (optional.isEmpty()){
            throw new RegistrationErrorException("Registration unsuccessful for mail "+user.getEmail());
        }
        Set<String> roleSet = savedUser.getRoles().stream()
                .map(UserRoles::name)
                .collect(Collectors.toSet());
        String token = jwtUtil.createJwt(savedUser.getId(),roleSet, savedUser.getEmail());
        return new ResponseEntity<>(new SuccessResponseDto<>(HttpStatus.CREATED.value(),"Registration successful", token),HttpStatus.CREATED);
    }
    public ResponseEntity<?> login(LoginDto loginDto) throws EmailPasswordException, UserNotActivatedException {
        Optional<User> optionalUser = userRepository.findByEmail(loginDto.getEmail());
        if (optionalUser.isEmpty()|| !passwordEncoder.matches(loginDto.getPassword(),optionalUser.get().getPassword())){
            throw new EmailPasswordException("Email or password invalid");
        }
        if (!optionalUser.get().isActivated()){
            throw new UserNotActivatedException("User is not activated");
        }
        Set<String> roleSet = optionalUser.get().getRoles().stream()
                .map(UserRoles::name)
                .collect(Collectors.toSet());
        String token = jwtUtil.createJwt(optionalUser.get().getId(),roleSet,optionalUser.get().getEmail());
        return new ResponseEntity<>(new SuccessResponseDto<>(HttpStatus.OK.value(), "Login successful", token),HttpStatus.OK);
    }
    public ResponseEntity<?> getAllUsers(String name, String email, boolean isActivated) {
        log.info("Getting users...");
        List<User> users = userRepository.findByFilters(name, email, isActivated);

        log.info("Users found: {}", users);

        List<UsersDto> usersDtos = users.stream()
                .map(user -> {
                    try {
                        return new UsersDto(user.getId(), user.getEmail(), user.getName());
                    } catch (UserFoundException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());

        Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AdminController.class)
                .getUsers(name, email, isActivated)).withSelfRel();

        CollectionModel<UsersDto> model = CollectionModel.of(usersDtos, selfLink);


        List<UsersDto> usersDtoList = new ArrayList<>(model.getContent());

        return new ResponseEntity<>(new ListSuccessResponse<>(HttpStatus.OK.value(), "Users retrieved successfully", usersDtoList), HttpStatus.OK);
    }

    public ResponseEntity<?> getUserById(String id) throws UserFoundException {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()){
            throw new UserFoundException("User not found");
        }
        UsersDto usersDto = new UsersDto(optionalUser.get().getId(),optionalUser.get().getName(),optionalUser.get().getEmail());
        return new ResponseEntity<>(new SuccessResponseDto<>(HttpStatus.OK.value(),"User retrieved",usersDto),HttpStatus.OK);
    }
    public ResponseEntity<?> deleteUserById(String id) throws UserFoundException {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()){
            throw new UserFoundException("User not found");
        }
        userRepository.deleteById(id);
        return new ResponseEntity<>(new NoDataSuccessResponse(HttpStatus.OK.value(), "User deleted successfully"),HttpStatus.OK);
    }

    public ResponseEntity<?> addUserRole(String id,UserRoles role) throws UserFoundException {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()){
            throw new UserFoundException("User not found");
        }
        log.info("Retrieving user.......................................................");
        User user = optionalUser.get();
        log.info("Adding roles...........................................................");
        log.info("User roles are : {}", user.getRoles().toString());
        user.getRoles().add(role);
        log.info("Roles added, saving role..................................................");
        log.info("User roles after addition is {}", user.getRoles().toString());
        userRepository.save(user);
        log.info("Role saved.............................................................");
        return new ResponseEntity<>(new NoDataSuccessResponse(HttpStatus.CREATED.value(),"Role added successfully"),HttpStatus.CREATED);
    }
}
