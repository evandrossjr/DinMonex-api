package com.essjr.DinMonex.transaction;

import com.essjr.DinMonex.security.AuthenticationHelper;
import com.essjr.DinMonex.transaction.dtos.CreditCardTransactionRequestDTO;
import com.essjr.DinMonex.transaction.enuns.TransactionType;
import com.essjr.DinMonex.user.AppUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para a classe TransactionService.
 * @ExtendWith(MockitoExtension.class) ativa a integração do JUnit 5 com o Mockito.
 */
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TransactionServiceTest {

    // @Mock cria uma versão "falsa" (um mock) das dependências.
    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AuthenticationHelper authenticationHelper;

    // @InjectMocks cria uma instância real do TransactionService e injeta os mocks.
    @InjectMocks
    private TransactionService transactionService;

    private AppUser testUser;

    @BeforeEach
    void setUp() {
        // Cria um utilizador de teste padrão para os nossos testes.
        testUser = new AppUser();
        testUser.setId(1L);
        testUser.setEmail("test@user.com");
    }

    @Test
    @DisplayName("Deve criar uma transação de cartão de crédito e gerar as parcelas corretamente")
    void deveCriarTransacaoDeCartaoComParcelas() {
        // Arrange (Preparação)
        // 1. Cria o DTO de requisição que o frontend enviaria.
        CreditCardTransactionRequestDTO requestDTO = new CreditCardTransactionRequestDTO();
        requestDTO.setDescription("Notebook Novo");
        requestDTO.setValue(new BigDecimal("3000.00"));
        requestDTO.setTotalInstallments(3);
        requestDTO.setDueDate(LocalDate.of(2025, 10, 15));

        // 2. Simula o comportamento das dependências.
        when(authenticationHelper.getCurrentUser()).thenReturn(testUser);
        // Quando o save for chamado, apenas retorna o objeto que foi passado para ele.
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // 3. Cria um "Captor" para "apanhar" o objeto que é passado para o método save.
        // Esta é a ferramenta chave para verificarmos se a lógica interna do serviço está correta.
        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);

        // Act (Ação)
        // Chama o método real que queremos testar.
        transactionService.createCreditCardTransaction(requestDTO);

        // Assert (Verificação)
        // 1. Verifica se o método save do repositório foi chamado exatamente uma vez
        // e captura o argumento que foi passado para ele.
        verify(transactionRepository, times(1)).save(transactionCaptor.capture());

        // 2. Obtém a transação que foi "apanhada" pelo captor.
        Transaction savedTransaction = transactionCaptor.getValue();

        // 3. Verifica os dados da transação "mãe".
        assertThat(savedTransaction).isNotNull();
        assertThat(savedTransaction.getDescription()).isEqualTo("Notebook Novo");
        assertThat(savedTransaction.getAppUser()).isEqualTo(testUser);
        assertThat(savedTransaction.getType()).isEqualTo(TransactionType.CREDIT_CARD);
        assertThat(savedTransaction.getTotalInstallments()).isEqualTo(3);

        // 4. Verifica os dados das parcelas geradas.
        List<Installment> installments = savedTransaction.getInstallments();
        assertThat(installments).hasSize(3);

        // Verifica a primeira parcela
        assertThat(installments.get(0).getInstallmentNumber()).isEqualTo(1);
        assertThat(installments.get(0).getValue()).isEqualTo(new BigDecimal("1000.00"));
        assertThat(installments.get(0).getDueDate()).isEqualTo(LocalDate.of(2025, 10, 15));

        // Verifica a segunda parcela
        assertThat(installments.get(1).getInstallmentNumber()).isEqualTo(2);
        assertThat(installments.get(1).getValue()).isEqualTo(new BigDecimal("1000.00"));
        assertThat(installments.get(1).getDueDate()).isEqualTo(LocalDate.of(2025, 11, 15));

        // Verifica a terceira parcela
        assertThat(installments.get(2).getInstallmentNumber()).isEqualTo(3);
        assertThat(installments.get(2).getValue()).isEqualTo(new BigDecimal("1000.00"));
        assertThat(installments.get(2).getDueDate()).isEqualTo(LocalDate.of(2025, 12, 15));
    }
}

