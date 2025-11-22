package com.essjr.DinMonex.debt.dtos;

import com.essjr.DinMonex.debt.SharedDebt;
import com.essjr.DinMonex.debt.SharedDebtRepository;
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
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test") // usa o application-test.properties (H2)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SharedDebtRepositoryTest {

    @Autowired
    private SharedDebtRepository sharedDebtRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    private AppUser creatorUser;
    private AppUser invitedUser;

    @BeforeEach
    void setUp() {
        sharedDebtRepository.deleteAll();
        appUserRepository.deleteAll();

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
        createTestDebt("Dívida 1", creatorUser, invitedUser, SharedDebtStatus.PENDING);
        createTestDebt("Dívida 2", creatorUser, invitedUser, SharedDebtStatus.ACCEPTED);
        createTestDebt("Dívida 3", invitedUser, creatorUser, SharedDebtStatus.PENDING);

        List<SharedDebt> foundDebts = sharedDebtRepository.findAllByCreatedBy(creatorUser);

        assertThat(foundDebts)
                .isNotNull()
                .hasSize(2)
                .extracting(SharedDebt::getDescription)
                .containsExactlyInAnyOrder("Dívida 1", "Dívida 2");
    }

    @Test
    @DisplayName("Deve encontrar todos os convites pendentes para um utilizador convidado")
    void deveEncontrarConvitesPendentesPorConvidado() {
        createTestDebt("Almoço", creatorUser, invitedUser, SharedDebtStatus.PENDING);
        createTestDebt("Cinema", creatorUser, invitedUser, SharedDebtStatus.ACCEPTED);
        createTestDebt("Jantar", invitedUser, creatorUser, SharedDebtStatus.PENDING);

        List<SharedDebt> foundDebts = sharedDebtRepository.findByInvitedUserAndStatus(invitedUser, SharedDebtStatus.PENDING);

        assertThat(foundDebts)
                .isNotNull()
                .hasSize(1)
                .first()
                .extracting(SharedDebt::getDescription)
                .isEqualTo("Almoço");
    }

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
