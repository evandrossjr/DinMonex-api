package com.essjr.DinMonex.services;

import com.essjr.DinMonex.auth.AuthService;
import com.essjr.DinMonex.security.JwtService;
import com.essjr.DinMonex.auth.dtos.LoginRequestDTO;
import com.essjr.DinMonex.auth.dtos.RegisterRequestDTO;
import com.essjr.DinMonex.user.AppUser;
import com.essjr.DinMonex.user.AppUserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;



@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AppUserRepository appUserRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("Deve registrar um novo usuário com sucesso")
    void registerUser_ShouldSaveUser_WhenRegistrationIsSuccessful() {
        // Arrange
        final String rawPassword = "password123";
        final String encodedPassword = "encodedPassword123";
        final AppUser.AppUserRole role = AppUser.AppUserRole.REGULAR;
        RegisterRequestDTO request = new RegisterRequestDTO("New User", "new@user.com", rawPassword);

        // Simula que o email ainda não existe no banco
        when(appUserRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        // Simula a codificação da senha
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);

        // Act
        // O método não retorna nada (void), então apenas o chamamos
        authService.registerUser(request, role);

        // Assert
        // Como o método é void, não podemos verificar um valor de retorno.
        // Em vez disso, verificamos o "efeito colateral": o método save() foi chamado?

        // 1. Criamos um "capturador" para o argumento do tipo AppUser
        ArgumentCaptor<AppUser> userArgumentCaptor = ArgumentCaptor.forClass(AppUser.class);

        // 2. Verificamos se appUserRepository.save() foi chamado e capturamos o objeto que foi passado para ele
        verify(appUserRepository).save(userArgumentCaptor.capture());

        // 3. Pegamos o usuário capturado
        AppUser savedUser = userArgumentCaptor.getValue();

        // 4. Verificamos se os dados do usuário salvo estão corretos
        assertNotNull(savedUser);
        assertEquals(request.getName(), savedUser.getName());
        assertEquals(request.getEmail(), savedUser.getEmail());
        assertEquals(encodedPassword, savedUser.getPassword());
        assertEquals(role, savedUser.getRole());
    }

    @Test
    @DisplayName("Deve lançar IllegalStateException ao registrar um email que já existe")
    void registerUser_ShouldThrowException_WhenEmailAlreadyExists() {
        // Arrange
        RegisterRequestDTO request = new RegisterRequestDTO("Existing User", "existing@user.com", "password");

        // Simula que o usuário já existe
        when(appUserRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(new AppUser()));

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            authService.registerUser(request, AppUser.AppUserRole.REGULAR);
        });

        assertEquals("Email já cadastrado.", exception.getMessage());

        // Garante que o processo parou e NUNCA tentou salvar o usuário
        verify(appUserRepository, never()).save(any(AppUser.class));
    }

    @Test
    @DisplayName("Deve autenticar com sucesso e retornar um token JWT")
    void login_ShouldReturnJwtToken_WhenCredentialsAreValid() {
        // Arrange
        LoginRequestDTO request = new LoginRequestDTO("test@user.com", "password");
        AppUser existingUser = new AppUser(1L, "Test User", "test@user.com", "encodedPass", AppUser.AppUserRole.REGULAR);
        String expectedToken = "mocked-jwt-token-string";

        // Mocks
        when(appUserRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(existingUser));
        when(jwtService.generateToken(existingUser)).thenReturn(expectedToken);

        // Act
        // O método agora retorna a String do token diretamente
        String actualToken = authService.login(request);

        // Assert
        assertNotNull(actualToken);
        assertEquals(expectedToken, actualToken);

        // Verifica as interações
        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        verify(appUserRepository).findByEmail(request.getEmail());
        verify(jwtService).generateToken(existingUser);
    }
}