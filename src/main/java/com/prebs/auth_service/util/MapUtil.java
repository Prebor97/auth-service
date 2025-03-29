package com.prebs.auth_service.util;

import com.prebs.auth_service.dto.request.RegistrationDto;
import com.prebs.auth_service.enums.UserRoles;
import com.prebs.auth_service.exception.PasswordMismatchException;
import com.prebs.auth_service.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class MapUtil {
    @Autowired
    private PasswordEncoder passwordEncoder;
    public User mapRegistration(User user, RegistrationDto registrationDto) throws  PasswordMismatchException {
        Set<UserRoles> roles = new HashSet<>();
        roles.add(UserRoles.USER);
        user.setEmail(registrationDto.getEmail());
        user.setName(registrationDto.getFirstName()+" "+registrationDto.getLastName());
        user.setRoles(roles);
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        return user;
    }
}
