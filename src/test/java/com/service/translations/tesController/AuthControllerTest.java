package com.service.translations.tesController;

import com.service.translations.controller.AuthController;
import com.service.translations.dto.AuthRequestDTO;
import com.service.translations.dto.AuthResponseDTO;
import com.service.translations.serviceImplementation.UserDetailsServiceImpl;
import com.service.translations.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void login_validCredentials_returnsToken() {
        AuthRequestDTO request = new AuthRequestDTO("admin", "12345678");
        UserDetails userDetails = new User("admin", "12345678", Collections.emptyList());

        when(userDetailsService.loadUserByUsername("admin")).thenReturn(userDetails);
        when(jwtUtil.generateToken("admin")).thenReturn("mock-token");

        ResponseEntity<?> response = authController.login(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        AuthResponseDTO body = (AuthResponseDTO) response.getBody();
        assertNotNull(body);
        assertEquals("mock-token", body.getToken());
    }

    @Test
    void login_invalidCredentials_returnsUnauthorized() {
        AuthRequestDTO request = new AuthRequestDTO("admin", "wrongpassword");

        doThrow(new BadCredentialsException("Bad credentials"))
                .when(authenticationManager).authenticate(any());

        ResponseEntity<?> response = authController.login(request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid username or password", response.getBody());
    }

    @Test
    void login_missingUsername_returnsBadRequest() {
        AuthRequestDTO request = new AuthRequestDTO(null, "password");

        ResponseEntity<?> response = authController.login(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid request. Missing username or password.", response.getBody());
    }

    @Test
    void login_userNotFound_returnsNotFound() {
        AuthRequestDTO request = new AuthRequestDTO("ghost", "1234");

        when(userDetailsService.loadUserByUsername("ghost")).thenReturn(null);

        ResponseEntity<?> response = authController.login(request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found", response.getBody());
    }
}
