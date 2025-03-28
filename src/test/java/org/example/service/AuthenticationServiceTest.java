package org.example.service;


import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.auth.AuthenticationRequest;
import org.example.auth.AuthenticationResponse;
import org.example.auth.RegisterRequest;
import org.example.config.JwtService;
import org.example.model.entity.User;
import org.example.model.enums.Role;
import org.example.repository.UserRepository;
import static org.mockito.Mockito.*;

import org.example.token.TokenRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AuthenticationServiceTest {

    @Mock
    private UserRepository repository;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private AuthenticationManager authenticationManager;

    private AutoCloseable closeable;

    private MockMvc mockMvc;
    @InjectMocks
    private AuthenticationService authenticationService;

    String name = "Aaaa bbbb";
    String email = "aaaa@example.com";

    String password = "Password!123";

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(authenticationService).build();
    }

    @AfterEach
    void tearDown() throws Exception {
        // Clean up after the test
        closeable.close();
    }

    @Test
    void testRegister() {
        // Arrange
        RegisterRequest request = new RegisterRequest(name, email, password, Role.USER);
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password("encodedPassword")
                .role(request.getRole())
                .build();

        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(repository.save(any(User.class))).thenReturn(user);
        when(jwtService.generateToken(any(User.class))).thenReturn("accessToken");
        when(jwtService.generateRefreshToken(any(User.class))).thenReturn("refreshToken");

        // Act
        AuthenticationResponse response = authenticationService.register(request);

        // Assert
        assertNotNull(response);
        assertEquals("accessToken", response.getAccessToken());
        assertEquals("refreshToken", response.getRefreshToken());

        verify(passwordEncoder).encode(request.getPassword());
        verify(repository).save(any(User.class));
        verify(jwtService).generateToken(user);
        verify(jwtService).generateRefreshToken(user);
    }

    @Test
    void testAuthenticate_Success() {
        // Arrange
        AuthenticationRequest request = new AuthenticationRequest(email, password);
        User user = User.builder()
                .name(name)
                .email(request.getEmail())
                .password("encodedPassword")
                .role(Role.USER)
                .build();

        when(repository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("accessToken");
        when(jwtService.generateRefreshToken(user)).thenReturn("refreshToken");

        // Act
        AuthenticationResponse response = authenticationService.authenticate(request);

        // Assert
        assertNotNull(response);
        assertEquals("accessToken", response.getAccessToken());
        assertEquals("refreshToken", response.getRefreshToken());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(repository).findByEmail(request.getEmail());
        verify(jwtService).generateToken(user);
        verify(jwtService).generateRefreshToken(user);
    }

    @Test
    void testAuthenticate_UserNotFound() {
        // Arrange
        AuthenticationRequest request = new AuthenticationRequest("nonexistent@example.com", "password123");
        when(repository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> authenticationService.authenticate(request));

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(repository).findByEmail(request.getEmail());
        verifyNoMoreInteractions(jwtService);
    }

    @Test
    void testRefreshToken_Success() throws IOException {
        // Arrange
        String refreshToken = "valid_refresh_token";
        String userEmail = email;
        String accessToken = "new_access_token";
        User user = User.builder()
                .name(name)
                .email(userEmail)
                .password("encodedPassword")
                .role(Role.USER)
                .build();

        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + refreshToken);
        when(jwtService.extractUsername(refreshToken)).thenReturn(userEmail);
        when(repository.findByEmail(userEmail)).thenReturn(Optional.of(user));
        when(jwtService.isTokenValid(refreshToken, user)).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn(accessToken);

        // ✅ Mock HttpServletResponse output stream
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ServletOutputStream servletOutputStream = new ServletOutputStream() {
            @Override
            public void write(int b) {
                byteArrayOutputStream.write(b);
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setWriteListener(WriteListener listener) {
                // Not needed for test
            }
        };

        when(response.getOutputStream()).thenReturn(servletOutputStream);

        // Act
        authenticationService.refreshToken(request, response);

        // Assert
        verify(repository).findByEmail(userEmail);
        verify(jwtService).generateToken(user);
        verify(jwtService).isTokenValid(refreshToken, user);
        verify(response).getOutputStream();
    }

    @Test
    void testRefreshToken_InvalidToken() throws IOException {
        // Arrange
        String refreshToken = "invalid_refresh_token";
        String userEmail = "john@example.com";
        User user = User.builder()
                .name("John Doe")
                .email(userEmail)
                .password("encodedPassword")
                .role(Role.USER)
                .build();

        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + refreshToken);
        when(jwtService.extractUsername(refreshToken)).thenReturn(userEmail);
        when(repository.findByEmail(userEmail)).thenReturn(Optional.of(user));
        when(jwtService.isTokenValid(refreshToken, user)).thenReturn(false);

        // Act
        authenticationService.refreshToken(request, response);

        // Assert
        verify(repository).findByEmail(userEmail);
        verify(jwtService).isTokenValid(refreshToken, user);
        verify(jwtService, never()).generateToken(user);
        verify(response, never()).getOutputStream();
    }

    @Test
    void testRefreshToken_UserNotFound() throws IOException {
        // Arrange
        String refreshToken = "valid_refresh_token";
        String userEmail = "unknown@example.com";

        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + refreshToken);
        when(jwtService.extractUsername(refreshToken)).thenReturn(userEmail);
        when(repository.findByEmail(userEmail)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> authenticationService.refreshToken(request, response));

        verify(repository).findByEmail(userEmail);
        verify(jwtService, never()).isTokenValid(any(), any());
        verify(jwtService, never()).generateToken(any());
        verify(response, never()).getOutputStream();
    }

    @Test
    void testRefreshToken_NoAuthorizationHeader() throws IOException {
        // Arrange
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);

        // Act
        authenticationService.refreshToken(request, response);

        // Assert
        verify(repository, never()).findByEmail(any());
        verify(jwtService, never()).isTokenValid(any(), any());
        verify(jwtService, never()).generateToken(any());
        verify(response, never()).getOutputStream();
    }
}