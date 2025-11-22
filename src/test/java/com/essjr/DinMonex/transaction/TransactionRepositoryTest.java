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
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY) // Usa o H2 automaticamente
@ActiveProfiles("test")
class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    private AppUser user1;
    private AppUser user2;

    @BeforeEach
    void setUp() {
        transactionRepository.deleteAll();
        appUserRepository.deleteAll();

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
        createTestTransaction("Conta de Luz", new BigDecimal("150.00"), LocalDate.now(), user1, TransactionType.CONSUMPTION, false);
        createTestTransaction("Conta de Água", new BigDecimal("80.50"), LocalDate.now(), user1, TransactionType.CONSUMPTION, true);
        createTestTransaction("Compra Online", new BigDecimal("300.00"), LocalDate.now(), user1, TransactionType.CREDIT_CARD, false);
        createTestTransaction("Gás", new BigDecimal("95.00"), LocalDate.now(), user2, TransactionType.CONSUMPTION, true);

        List<Transaction> foundTransactions = transactionRepository.findAllByAppUserAndType(user1, TransactionType.CONSUMPTION);

        assertThat(foundTransactions).isNotNull();
        assertThat(foundTransactions).hasSize(2);
        assertThat(foundTransactions)
                .extracting(Transaction::getDescription)
                .containsExactlyInAnyOrder("Conta de Luz", "Conta de Água");
    }

    @Test
    @DisplayName("Deve encontrar uma transação pelo ID se pertencer ao utilizador correto")
    void deveEncontrarTransacaoPorIdEUtilizador() {
        Transaction savedTransaction = createTestTransaction("Aluguer", new BigDecimal("1200.00"), LocalDate.now(), user1, TransactionType.CONSUMPTION, true);

        Optional<Transaction> foundTransactionOpt = transactionRepository.findByIdAndAppUser(savedTransaction.getId(), user1);

        assertThat(foundTransactionOpt).isPresent();
        assertThat(foundTransactionOpt.get().getId()).isEqualTo(savedTransaction.getId());
    }

    @Test
    @DisplayName("NÃO deve encontrar uma transação pelo ID se pertencer a outro utilizador")
    void naoDeveEncontrarTransacaoDeOutroUtilizador() {
        Transaction savedTransaction = createTestTransaction("Internet", new BigDecimal("100.00"), LocalDate.now(), user1, TransactionType.CONSUMPTION, true);

        Optional<Transaction> foundTransactionOpt = transactionRepository.findByIdAndAppUser(savedTransaction.getId(), user2);

        assertThat(foundTransactionOpt).isNotPresent();
    }

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