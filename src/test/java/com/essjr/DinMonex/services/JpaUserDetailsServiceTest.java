package com.essjr.DinMonex.services;

import com.essjr.DinMonex.auth.JpaUserDetailsService;
import com.essjr.DinMonex.user.AppUser;
import com.essjr.DinMonex.user.AppUserRepository;
import com.essjr.DinMonex.user.enums.AppUserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JpaUserDetailsServiceTest { // <-- MUDANÇA DE NOME AQUI

    @Mock
    private AppUserRepository appUserRepository;

    @InjectMocks
    private JpaUserDetailsService userDetailsService; // <-- E AQUI

    private AppUser testUser;
    private final String userEmail = "test@user.com";

    @BeforeEach
    void setUp() {
        testUser = new AppUser(1L, "Test User", userEmail, "password123", AppUserRole.REGULAR);
    }

    @Test
    @DisplayName("Deve carregar o usuário com sucesso quando o email existir")
    void loadUserByUsername_ShouldReturnUserDetails_WhenUserExists() {
        // Arrange
        when(appUserRepository.findByEmail(userEmail)).thenReturn(Optional.of(testUser));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail); // <-- Usando a variável correta

        // Assert
        assertNotNull(userDetails);
        assertEquals(userEmail, userDetails.getUsername());
        verify(appUserRepository).findByEmail(userEmail);
    }

    @Test
    @DisplayName("Deve lançar UsernameNotFoundException quando o email não existir")
    void loadUserByUsername_ShouldThrowException_WhenUserDoesNotExist() {
        // Arrange
        when(appUserRepository.findByEmail(userEmail)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername(userEmail); // <-- E aqui também
        });

        verify(appUserRepository).findByEmail(userEmail);
    }
}