package com.essjr.DinMonex.debt;




import com.essjr.DinMonex.debt.dtos.SharedDebtResponseDTO;
import com.essjr.DinMonex.debt.enums.SharedDebtStatus;
import com.essjr.DinMonex.security.AuthenticationHelper;
import com.essjr.DinMonex.user.AppUser;
import com.essjr.DinMonex.user.AppUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para a classe DebtService.
 * @ExtendWith(MockitoExtension.class) ativa a integração do JUnit 5 com o Mockito.
 */
@ExtendWith(MockitoExtension.class)
class DebtServiceTest {

    // @Mock cria uma versão "falsa" (um mock) das dependências.
    @Mock
    private SharedDebtRepository sharedDebtRepository;

    @Mock
    private AppUserRepository appUserRepository; // Necessário para o construtor do serviço

    @Mock
    private AuthenticationHelper authenticationHelper;

    // @InjectMocks cria uma instância real do DebtService e injeta os mocks.
    @InjectMocks
    private DebtService debtService;

    private AppUser currentUser;
    private AppUser creatorUser;
    private SharedDebt pendingDebt;

    @BeforeEach
    void setUp() {
        // Cria utilizadores de teste para os nossos cenários.
        currentUser = new AppUser();
        currentUser.setId(1L);
        currentUser.setName("Utilizador Convidado");
        currentUser.setEmail("invited@user.com");

        creatorUser = new AppUser();
        creatorUser.setId(2L);
        creatorUser.setName("Utilizador Criador");
        creatorUser.setEmail("creator@user.com");

        // Cria um convite de dívida pendente para ser usado nos testes.
        pendingDebt = new SharedDebt();
        pendingDebt.setId(100L);
        pendingDebt.setStatus(SharedDebtStatus.PENDING);
        pendingDebt.setInvitedUser(currentUser); // O nosso currentUser é o convidado.
        pendingDebt.setCreatedBy(creatorUser);
    }

    @Test
    @DisplayName("Deve aceitar um convite com sucesso se o utilizador for o convidado e o status for PENDENTE")
    void deveAceitarConvitePendenteComSucesso() {
        // Arrange (Preparação)
        // 1. Simula o comportamento das dependências.
        when(authenticationHelper.getCurrentUser()).thenReturn(currentUser);
        when(sharedDebtRepository.findById(100L)).thenReturn(Optional.of(pendingDebt));

        // Simula a ação de salvar, retornando o objeto que foi passado.
        when(sharedDebtRepository.save(any(SharedDebt.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act (Ação)
        // Chama o método real que queremos testar.
        SharedDebtResponseDTO response = debtService.respondToInvitation(100L, SharedDebtStatus.ACCEPTED);

        // Assert (Verificação)
        // 1. Cria um "Captor" para "apanhar" o objeto que é passado para o método save.
        ArgumentCaptor<SharedDebt> debtCaptor = ArgumentCaptor.forClass(SharedDebt.class);
        verify(sharedDebtRepository, times(1)).save(debtCaptor.capture());

        // 2. Obtém a dívida que foi "apanhada" e verifica o seu status.
        SharedDebt savedDebt = debtCaptor.getValue();
        assertThat(savedDebt.getStatus()).isEqualTo(SharedDebtStatus.ACCEPTED);

        // 3. Verifica se o DTO de resposta também tem o status correto.
        assertThat(response.getStatus()).isEqualTo(SharedDebtStatus.ACCEPTED.name());
    }

    @Test
    @DisplayName("Deve recusar um convite com sucesso se o utilizador for o convidado e o status for PENDENTE")
    void deveRecusarConvitePendenteComSucesso() {
        // Arrange
        when(authenticationHelper.getCurrentUser()).thenReturn(currentUser);
        when(sharedDebtRepository.findById(100L)).thenReturn(Optional.of(pendingDebt));
        when(sharedDebtRepository.save(any(SharedDebt.class))).thenAnswer(invocation -> invocation.getArgument(0));
        ArgumentCaptor<SharedDebt> debtCaptor = ArgumentCaptor.forClass(SharedDebt.class);

        // Act
        debtService.respondToInvitation(100L, SharedDebtStatus.REJECTED);

        // Assert
        verify(sharedDebtRepository, times(1)).save(debtCaptor.capture());
        SharedDebt savedDebt = debtCaptor.getValue();
        assertThat(savedDebt.getStatus()).isEqualTo(SharedDebtStatus.REJECTED);
    }

    @Test
    @DisplayName("Deve lançar SecurityException ao tentar responder a um convite que não é para o utilizador logado")
    void deveLancarExcecaoSeUtilizadorNaoForOConvidado() {
        // Arrange
        // A dívida pertence ao 'currentUser', mas quem está a tentar responder é o 'creatorUser'.
        when(authenticationHelper.getCurrentUser()).thenReturn(creatorUser); // Utilizador errado
        when(sharedDebtRepository.findById(100L)).thenReturn(Optional.of(pendingDebt));

        // Act & Assert
        // Verificamos se a chamada ao método respondToInvitation lança a exceção de segurança que esperamos.
        assertThrows(SecurityException.class, () -> {
            debtService.respondToInvitation(100L, SharedDebtStatus.ACCEPTED);
        });

        // Verificamos que o método save do repositório NUNCA foi chamado.
        verify(sharedDebtRepository, never()).save(any(SharedDebt.class));
    }

    @Test
    @DisplayName("Deve lançar IllegalStateException ao tentar responder a um convite que já foi respondido")
    void deveLancarExcecaoSeConviteNaoEstiverPendente() {
        // Arrange
        pendingDebt.setStatus(SharedDebtStatus.ACCEPTED); // A dívida já está como aceite

        when(authenticationHelper.getCurrentUser()).thenReturn(currentUser);
        when(sharedDebtRepository.findById(100L)).thenReturn(Optional.of(pendingDebt));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> {
            debtService.respondToInvitation(100L, SharedDebtStatus.REJECTED);
        });

        verify(sharedDebtRepository, never()).save(any(SharedDebt.class));
    }
}

