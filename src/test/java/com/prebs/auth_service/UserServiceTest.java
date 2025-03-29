package com.prebs.auth_service;

import com.prebs.auth_service.dto.request.LoginDto;
import com.prebs.auth_service.dto.request.RegistrationDto;
import com.prebs.auth_service.dto.response.ListSuccessResponse;
import com.prebs.auth_service.dto.response.NoDataSuccessResponse;
import com.prebs.auth_service.dto.response.SuccessResponseDto;
import com.prebs.auth_service.dto.response.UsersDto;
import com.prebs.auth_service.enums.UserRoles;
import com.prebs.auth_service.exception.*;
import com.prebs.auth_service.model.User;
import com.prebs.auth_service.repository.UserRepository;
import com.prebs.auth_service.service.UserService;
import com.prebs.auth_service.util.JwtUtil;
import com.prebs.auth_service.util.MapUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private MapUtil mapUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private RegistrationDto registrationDto;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId("1");
        testUser.setEmail("test@example.com");
        testUser.setName("Test User");
        testUser.setPassword("encodedPassword");
        testUser.setActivated(true);
        testUser.setRoles(new HashSet<>(Collections.singleton(UserRoles.USER)));


        registrationDto = new RegistrationDto();
        registrationDto.setEmail("test1@example.com");
        registrationDto.setFirstName("Test");
        registrationDto.setLastName("User");
        registrationDto.setPassword("password");
        registrationDto.setConfirmPassword("password");
    }
    @Test
    void register_success() throws Exception {
        // Setup mocks
        when(userRepository.findByEmail(registrationDto.getEmail())).thenReturn(Optional.empty());
        when(mapUtil.mapRegistration(any(User.class), eq(registrationDto))).thenReturn(testUser);
        when(userRepository.save(testUser)).thenReturn(testUser);
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(jwtUtil.createJwt(eq(testUser.getId()), anySet(), eq(testUser.getEmail()))).thenReturn("jwtToken");

        // Execute
        ResponseEntity<?> response = userService.register(registrationDto);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        SuccessResponseDto<?> successResponse = (SuccessResponseDto<?>) response.getBody();
        assert successResponse != null;
        assertEquals("Registration successful", successResponse.message);
        assertEquals("jwtToken", successResponse.data);
        verify(userRepository).findByEmail(registrationDto.getEmail());
        verify(userRepository).save(testUser); // Save call
        verify(userRepository).findByEmail(testUser.getEmail());
    }

    @Test
    void register_userAlreadyExists_throwsException() throws Exception {
        when(userRepository.findByEmail(registrationDto.getEmail())).thenReturn(Optional.of(testUser));

        assertThrows(UserFoundException.class, () -> userService.register(registrationDto));
        verify(mapUtil, never()).mapRegistration(any(User.class), any(RegistrationDto.class));
    }

    @Test
    void register_passwordMismatch_throwsException() throws PasswordMismatchException {
        registrationDto.setConfirmPassword("different");

        when(userRepository.findByEmail(registrationDto.getEmail())).thenReturn(Optional.empty());

        assertThrows(PasswordMismatchException.class, () -> userService.register(registrationDto));
        verify(mapUtil, never()).mapRegistration(any(User.class), any(RegistrationDto.class));
    }

    @Test
    void register_mappingFailure_throwsException() throws Exception {
        when(userRepository.findByEmail(registrationDto.getEmail())).thenReturn(Optional.empty());
        when(mapUtil.mapRegistration(any(User.class), eq(registrationDto))).thenReturn(testUser);
        when(userRepository.save(testUser)).thenReturn(testUser);
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.empty());

        assertThrows(RegistrationErrorException.class, () -> userService.register(registrationDto));
    }

    @Test
    void login_success() throws Exception {
        LoginDto dto = new LoginDto();
        dto.setEmail("test@example.com");
        dto.setPassword("password");

        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(dto.getPassword(), testUser.getPassword())).thenReturn(true);
        when(jwtUtil.createJwt(eq(testUser.getId()), anySet(), eq(testUser.getEmail()))).thenReturn("jwtToken");

        ResponseEntity<?> response = userService.login(dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        SuccessResponseDto<?> successResponse = (SuccessResponseDto<?>) response.getBody();
        assert successResponse != null;
        assertEquals("Login successful", successResponse.message);
        assertEquals("jwtToken", successResponse.data);
    }
    @Test
    void login_invalidCredentials_throwsException() {
        LoginDto dto = new LoginDto();
        dto.setEmail("test@example.com");
        dto.setPassword("wrong");

        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(dto.getPassword(), testUser.getPassword())).thenReturn(false);

        assertThrows(EmailPasswordException.class, () -> userService.login(dto));
    }

    @Test
    void login_userNotActivated_throwsException() {
        LoginDto dto = new LoginDto();
        dto.setEmail("test@example.com");
        dto.setPassword("password");
        testUser.setActivated(false);

        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(dto.getPassword(), testUser.getPassword())).thenReturn(true);

        assertThrows(UserNotActivatedException.class, () -> userService.login(dto));
    }

    @Test
    void getAllUsers_success() {
        List<User> users = Collections.singletonList(testUser);
        when(userRepository.findByFilters("Test", "test@example.com", true)).thenReturn(users);

        ResponseEntity<?> response = userService.getAllUsers("Test", "test@example.com", true);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        ListSuccessResponse<?> successResponse = (ListSuccessResponse<?>) response.getBody();
        assert successResponse != null;
        assertEquals("Users retrieved successfully", successResponse.message);
        List<UsersDto> usersDtos = (List<UsersDto>) successResponse.data;
        assertEquals(1, usersDtos.size());
        assertEquals(testUser.getEmail(), usersDtos.get(0).getEmail());
    }

    @Test
    void getUserById_success() throws UserFoundException {
        when(userRepository.findById("1")).thenReturn(Optional.of(testUser));

        ResponseEntity<?> response = userService.getUserById("1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        SuccessResponseDto<?> successResponse = (SuccessResponseDto<?>) response.getBody();
        assert successResponse != null;
        assertEquals("User retrieved", successResponse.message);
        UsersDto userDto = (UsersDto) successResponse.data;
        assertEquals(testUser.getId(), userDto.getId());
    }
    @Test
    void getUserById_notFound_throwsException() {
        when(userRepository.findById("1")).thenReturn(Optional.empty());

        assertThrows(UserFoundException.class, () -> userService.getUserById("1"));
    }

    @Test
    void deleteUserById_success() throws UserFoundException {
        when(userRepository.findById("1")).thenReturn(Optional.of(testUser));
        doNothing().when(userRepository).deleteById("1");

        ResponseEntity<?> response = userService.deleteUserById("1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        NoDataSuccessResponse successResponse = (NoDataSuccessResponse) response.getBody();
        assert successResponse != null;
        assertEquals("User deleted successfully", successResponse.message);
    }

    @Test
    void addUserRole_success() throws UserFoundException {
        when(userRepository.findById("1")).thenReturn(Optional.of(testUser));
        when(userRepository.save(testUser)).thenReturn(testUser);

        ResponseEntity<?> response = userService.addUserRole("1", UserRoles.ADMIN);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        NoDataSuccessResponse successResponse = (NoDataSuccessResponse) response.getBody();
        assert successResponse != null;
        assertEquals("Role added successfully", successResponse.message);
        assertTrue(testUser.getRoles().contains(UserRoles.ADMIN));
    }
}
