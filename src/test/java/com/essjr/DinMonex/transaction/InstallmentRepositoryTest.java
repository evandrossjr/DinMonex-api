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
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@EnableJpaRepositories(basePackages = {"com.essjr.DinMonex.user", "com.essjr.DinMonex.transaction"})
class InstallmentRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgresqlContainer = new PostgreSQLContainer<>("postgres:15-alpine");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresqlContainer::getUsername);
        registry.add("spring.datasource.password", postgresqlContainer::getPassword);
        // A SOLUÇÃO: Força o Spring a usar o driver do PostgreSQL para este teste.
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
    }

    @Autowired
    private InstallmentRepository installmentRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private AppUserRepository appUserRepository;

    private AppUser testUser;

    @BeforeEach
    void setUp() {
        installmentRepository.deleteAll();
        transactionRepository.deleteAll();
        appUserRepository.deleteAll();

        testUser = new AppUser();
        testUser.setName("Utilizador Teste");
        testUser.setEmail("user@test.com");
        testUser.setPassword("hash123");
        testUser.setRole(AppUserRole.REGULAR);
        appUserRepository.save(testUser);
    }

    @Test
    @DisplayName("Deve salvar as parcelas em cascata ao salvar a transação mãe")
    void deveSalvarParcelasEmCascata() {
        // Arrange
        Transaction parentTransaction = new Transaction();
        parentTransaction.setDescription("Compra Parcelada Teste");
        parentTransaction.setValue(new BigDecimal("1200.00"));
        parentTransaction.setDueDate(LocalDate.now());
        parentTransaction.setType(TransactionType.CREDIT_CARD);
        parentTransaction.setTotalInstallments(3);
        parentTransaction.setAppUser(testUser);

        Installment installment1 = new Installment();
        installment1.setInstallmentNumber(1);
        installment1.setValue(new BigDecimal("400.00"));
        installment1.setDueDate(LocalDate.now());
        installment1.setPaid(false);
        installment1.setTransaction(parentTransaction);

        Installment installment2 = new Installment();
        installment2.setInstallmentNumber(2);
        installment2.setValue(new BigDecimal("400.00"));
        installment2.setDueDate(LocalDate.now().plusMonths(1));
        installment2.setPaid(false);
        installment2.setTransaction(parentTransaction);

        parentTransaction.getInstallments().addAll(List.of(installment1, installment2));

        // Act
        transactionRepository.save(parentTransaction);

        // Assert
        List<Installment> savedInstallments = installmentRepository.findAll();

        assertThat(savedInstallments).isNotNull();
        assertThat(savedInstallments).hasSize(2);
        assertThat(savedInstallments).allMatch(installment ->
                installment.getTransaction().getId().equals(parentTransaction.getId())
        );
    }
}