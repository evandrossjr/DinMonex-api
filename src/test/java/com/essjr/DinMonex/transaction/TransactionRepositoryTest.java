package com.essjr.DinMonex.transaction;

import com.essjr.DinMonex.transaction.enuns.TransactionType;
import com.essjr.DinMonex.user.AppUser;
import com.essjr.DinMonex.user.AppUserRepository;
import com.essjr.DinMonex.user.enums.AppUserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TransactionRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgresqlContainer = new PostgreSQLContainer<>("postgres:15-alpine");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresqlContainer::getUsername);
        registry.add("spring.datasource.password", postgresqlContainer::getPassword);
    }

    // Injetamos ambos os repositórios. O AppUserRepository é necessário para criar os "donos" das transações.
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private AppUserRepository appUserRepository;

    // Utilizadores de teste que serão usados nos cenários.
    private AppUser user1;
    private AppUser user2;

    @BeforeEach
    void setUp() {
        // Limpa as tabelas antes de cada teste para garantir o isolamento.
        // Apagamos as transações primeiro por causa da chave estrangeira.
        transactionRepository.deleteAll();
        appUserRepository.deleteAll();

        // Cria e salva dois utilizadores de teste.
        user1 = new AppUser();
        user1.setName("Utilizador Um");
        user1.setEmail("user1@email.com");
        user1.setPassword("hash123");
        user1.setRole(AppUserRole.REGULAR);
        appUserRepository.save(user1);

        user2 = new AppUser();
        user2.setName("Utilizador Dois");
        user2.setEmail("user2@email.com");
        user2.setPassword("hash456");
        user2.setRole(AppUserRole.REGULAR);
        appUserRepository.save(user2);
    }

    @Test
    @DisplayName("Deve encontrar todas as transações de consumo de um utilizador, e apenas as dele")
    void deveEncontrarTransacoesDeConsumoPorUtilizador() {
        // Arrange (Preparação)
        // Cria 2 transações de consumo para o user1
        createTestTransaction("Conta de Luz", new BigDecimal("150.00"), LocalDate.now(), user1, TransactionType.CONSUMPTION, false);
        createTestTransaction("Conta de Água", new BigDecimal("80.50"), LocalDate.now(), user1, TransactionType.CONSUMPTION, true);
        // Cria 1 transação de outro tipo para o user1 (não deve ser encontrada)
        createTestTransaction("Compra Online", new BigDecimal("300.00"), LocalDate.now(), user1, TransactionType.CREDIT_CARD, false);
        // Cria 1 transação de consumo para o user2 (não deve ser encontrada)
        createTestTransaction("Gás", new BigDecimal("95.00"), LocalDate.now(), user2, TransactionType.CONSUMPTION, true);

        // Act (Ação)
        List<Transaction> foundTransactions = transactionRepository.findAllByAppUserAndType(user1, TransactionType.CONSUMPTION);

        // Assert (Verificação)
        assertThat(foundTransactions).isNotNull();
        assertThat(foundTransactions).hasSize(2); // Deve encontrar apenas as 2 transações de consumo do user1
        assertThat(foundTransactions).extracting(Transaction::getDescription).containsExactlyInAnyOrder("Conta de Luz", "Conta de Água");
    }

    @Test
    @DisplayName("Deve encontrar uma transação pelo ID se pertencer ao utilizador correto")
    void deveEncontrarTransacaoPorIdEUtilizador() {
        // Arrange
        Transaction savedTransaction = createTestTransaction("Aluguer", new BigDecimal("1200.00"), LocalDate.now(), user1, TransactionType.CONSUMPTION, true);

        // Act
        Optional<Transaction> foundTransactionOpt = transactionRepository.findByIdAndAppUser(savedTransaction.getId(), user1);

        // Assert
        assertThat(foundTransactionOpt).isPresent();
        assertThat(foundTransactionOpt.get().getId()).isEqualTo(savedTransaction.getId());
    }

    @Test
    @DisplayName("NÃO deve encontrar uma transação pelo ID se pertencer a outro utilizador")
    void naoDeveEncontrarTransacaoDeOutroUtilizador() {
        // Arrange
        Transaction savedTransaction = createTestTransaction("Internet", new BigDecimal("100.00"), LocalDate.now(), user1, TransactionType.CONSUMPTION, true);

        // Act: Procura pela transação do user1, mas como se fosse o user2 a pedir.
        Optional<Transaction> foundTransactionOpt = transactionRepository.findByIdAndAppUser(savedTransaction.getId(), user2);

        // Assert
        // Este é o teste de segurança mais importante!
        assertThat(foundTransactionOpt).isNotPresent();
    }

    // Método de ajuda para criar e salvar transações de teste de forma rápida.
    private Transaction createTestTransaction(String desc, BigDecimal value, LocalDate date, AppUser owner, TransactionType type, boolean isRecurring) {
        Transaction tx = new Transaction();
        tx.setDescription(desc);
        tx.setValue(value);
        tx.setDueDate(date);
        tx.setAppUser(owner);
        tx.setType(type);
        tx.setRecurring(isRecurring);
        return transactionRepository.save(tx);
    }
}
