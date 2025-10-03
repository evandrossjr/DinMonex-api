package com.essjr.DinMonex.transaction;

import com.essjr.DinMonex.security.AuthenticationHelper;
import com.essjr.DinMonex.transaction.dtos.TransactionResponseDTO;
import com.essjr.DinMonex.transaction.enuns.TransactionType;
import com.essjr.DinMonex.user.AppUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
/**
* Testes unitários para a classe TransactionService.
* @ExtendWith(MockitoExtension.class) ativa a integração do JUnit 5 com o Mockito .
*/
@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    // @Mock cria uma versão "falsa" (um mock) das dependências.
    // Estes mocks não irão falar com a base de dados de verdade.
    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AuthenticationHelper authenticationHelper;

    // @InjectMocks cria uma instância real do TransactionService e injeta
    // os mocks criados acima (@Mock) nos seus campos.
    @InjectMocks
    private TransactionService transactionService;

    // Utilizador de teste que será usado em todos os cenários.
    private AppUser testUser;

    @BeforeEach
    void setUp() {
        // Cria um utilizador de teste padrão para os nossos testes.
        testUser = new AppUser();
        testUser.setId(1L);
        testUser.setEmail("test@user.com");
    }

    @Test
    @DisplayName("Deve retornar uma lista de transações de consumo do utilizador logado")
    void deveRetornarTransacoesDeConsumo() {
        // Arrange (Preparação)
        // 1. Simula o comportamento do AuthenticationHelper:
        // "QUANDO o método getCurrentUser for chamado, ENTÃO retorna o nosso testUser."
        when(authenticationHelper.getCurrentUser()).thenReturn(testUser);

        // 2. Cria uma lista de transações falsas que o repositório deveria retornar.
        Transaction tx1 = new Transaction();
        tx1.setId(10L);
        tx1.setDescription("Conta de Luz");
        List<Transaction> fakeTransactions = List.of(tx1);

        // 3. Simula o comportamento do TransactionRepository:
        // "QUANDO o método findAllByAppUserAndType for chamado com o nosso utilizador e o tipo CONSUMPTION,
        // ENTÃO retorna a nossa lista de transações falsas."
        when(transactionRepository.findAllByAppUserAndType(testUser, TransactionType.CONSUMPTION))
                .thenReturn(fakeTransactions);

        // Act (Ação)
        // Chama o método real do nosso serviço que estamos a testar.
        List<TransactionResponseDTO> result = transactionService.getMyConsumptionTransactions();

        // Assert (Verificação)
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDescription()).isEqualTo("Conta de Luz");

        // Verifica se os mocks foram chamados como esperado.
        verify(authenticationHelper, times(1)).getCurrentUser();
        verify(transactionRepository, times(1)).findAllByAppUserAndType(testUser, TransactionType.CONSUMPTION);
    }

    @Test
    @DisplayName("Deve apagar uma transação com sucesso se pertencer ao utilizador")
    void deveApagarTransacaoDoUtilizador() {
        // Arrange
        Long transactionIdToDelete = 20L;
        Transaction transactionBelongingToUser = new Transaction();
        transactionBelongingToUser.setId(transactionIdToDelete);
        transactionBelongingToUser.setAppUser(testUser); // Importante: a transação pertence ao utilizador de teste

        when(authenticationHelper.getCurrentUser()).thenReturn(testUser);
        // Simula que o repositório encontrou a transação e que ela pertence ao utilizador.
        when(transactionRepository.findByIdAndAppUser(transactionIdToDelete, testUser))
                .thenReturn(Optional.of(transactionBelongingToUser));

        // Act
        transactionService.deleteMyTransaction(transactionIdToDelete);

        // Assert
        // A verificação mais importante: garantimos que o método delete do repositório
        // foi chamado exatamente uma vez com o objeto de transação correto.
        verify(transactionRepository, times(1)).delete(transactionBelongingToUser);
    }

    @Test
    @DisplayName("Deve lançar uma exceção ao tentar apagar uma transação que não existe ou não pertence ao utilizador")
    void deveLancarExcecaoAoApagarTransacaoDeOutro() {
        // Arrange
        Long transactionIdToDelete = 30L;
        when(authenticationHelper.getCurrentUser()).thenReturn(testUser);
        // Simula o cenário de segurança: o repositório não encontrou a transação para este utilizador.
        when(transactionRepository.findByIdAndAppUser(transactionIdToDelete, testUser))
                .thenReturn(Optional.empty());

        // Act & Assert
        // Verificamos se a chamada ao método deleteMyTransaction lança a exceção que esperamos.
        assertThrows(IllegalStateException.class, () -> {
            transactionService.deleteMyTransaction(transactionIdToDelete);
        });

        // Verificamos que o método delete do repositório NUNCA foi chamado.
        verify(transactionRepository, never()).delete(any(Transaction.class));
    }
}