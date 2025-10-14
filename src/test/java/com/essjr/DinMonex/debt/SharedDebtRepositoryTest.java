package com.essjr.DinMonex.debt;


import com.essjr.DinMonex.debt.enums.SharedDebtStatus;
import com.essjr.DinMonex.user.AppUser;
import com.essjr.DinMonex.user.AppUserRepository;
import com.essjr.DinMonex.user.enums.AppUserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
// É crucial dizer ao Spring para escanear todos os pacotes onde temos repositórios.
@EnableJpaRepositories(basePackages = {"com.essjr.DinMonex.user", "com.essjr.DinMonex.transaction", "com.essjr.DinMonex.debt"})
class SharedDebtRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgresqlContainer = new PostgreSQLContainer<>("postgres:15-alpine");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresqlContainer::getUsername);
        registry.add("spring.datasource.password", postgresqlContainer::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
    }

    @Autowired
    private SharedDebtRepository sharedDebtRepository;
    @Autowired
    private AppUserRepository appUserRepository;

    private AppUser creatorUser; // O utilizador que cria as dívidas
    private AppUser invitedUser; // O utilizador que é convidado

    @BeforeEach
    void setUp() {
        // Limpa as tabelas antes de cada teste para garantir o isolamento.
        sharedDebtRepository.deleteAll();
        appUserRepository.deleteAll();

        // Cria e salva dois utilizadores de teste.
        creatorUser = new AppUser();
        creatorUser.setName("Credor");
        creatorUser.setEmail("creator@email.com");
        creatorUser.setPassword("hash123");
        creatorUser.setRole(AppUserRole.REGULAR);
        appUserRepository.save(creatorUser);

        invitedUser = new AppUser();
        invitedUser.setName("Devedor");
        invitedUser.setEmail("invited@email.com");
        invitedUser.setPassword("hash456");
        invitedUser.setRole(AppUserRole.REGULAR);
        appUserRepository.save(invitedUser);
    }

    @Test
    @DisplayName("Deve encontrar todas as dívidas criadas por um utilizador específico")
    void deveEncontrarDividasPorCriador() {
        // Arrange (Preparação)
        // Cria 2 dívidas pelo 'creatorUser'
        createTestDebt("Dívida 1", creatorUser, invitedUser, SharedDebtStatus.PENDING);
        createTestDebt("Dívida 2", creatorUser, invitedUser, SharedDebtStatus.ACCEPTED);
        // Cria 1 dívida por outro utilizador (não deve ser encontrada)
        createTestDebt("Dívida 3", invitedUser, creatorUser, SharedDebtStatus.PENDING);

        // Act (Ação)
        List<SharedDebt> foundDebts = sharedDebtRepository.findAllByCreatedBy(creatorUser);

        // Assert (Verificação)
        assertThat(foundDebts).isNotNull();
        assertThat(foundDebts).hasSize(2); // Deve encontrar apenas as 2 dívidas criadas pelo 'creatorUser'
        assertThat(foundDebts).extracting(SharedDebt::getDescription).containsExactlyInAnyOrder("Dívida 1", "Dívida 2");
    }

    @Test
    @DisplayName("Deve encontrar todos os convites pendentes para um utilizador convidado")
    void deveEncontrarConvitesPendentesPorConvidado() {
        // Arrange
        // Cria 1 convite pendente para o 'invitedUser'
        createTestDebt("Almoço", creatorUser, invitedUser, SharedDebtStatus.PENDING);
        // Cria 1 convite já aceite para o 'invitedUser' (não deve ser encontrado)
        createTestDebt("Cinema", creatorUser, invitedUser, SharedDebtStatus.ACCEPTED);
        // Cria 1 convite pendente para outro utilizador (não deve ser encontrado)
        createTestDebt("Jantar", invitedUser, creatorUser, SharedDebtStatus.PENDING);

        // Act
        List<SharedDebt> foundDebts = sharedDebtRepository.findByInvitedUserAndStatus(invitedUser, SharedDebtStatus.PENDING);

        // Assert
        assertThat(foundDebts).isNotNull();
        assertThat(foundDebts).hasSize(1); // Deve encontrar apenas o convite pendente para o 'invitedUser'
        assertThat(foundDebts.get(0).getDescription()).isEqualTo("Almoço");
    }

    // Método de ajuda para criar e salvar dívidas de teste de forma rápida.
    private void createTestDebt(String desc, AppUser creator, AppUser invited, SharedDebtStatus status) {
        SharedDebt debt = new SharedDebt();
        debt.setDescription(desc);
        debt.setValue(new BigDecimal("100.00"));
        debt.setDueDate(LocalDate.now());
        debt.setCreatedBy(creator);
        debt.setInvitedUser(invited);
        debt.setStatus(status);
        sharedDebtRepository.save(debt);
    }
}
