package com.essjr.DinMonex.debt;

import com.essjr.DinMonex.debt.enums.SharedDebtStatus;
import com.essjr.DinMonex.user.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SharedDebtRepository extends JpaRepository<SharedDebt, Long> {
    /**
     * Encontra todas as dívidas partilhadas onde o utilizador fornecido é o CRIADOR.
     * @param createdBy O utilizador que criou as dívidas.
     * @return Uma lista das dívidas criadas pelo utilizador.
     */
    List<SharedDebt> findAllByCreatedBy(AppUser createdBy);




    /**
     * Encontra todos os convites de dívida pendentes para um utilizador específico.
     * @param invitedUser O utilizador que foi convidado.
     * @param status O status a ser procurado (neste caso, PENDING).
     * @return Uma lista de dívidas partilhadas pendentes.
     */
    List<SharedDebt> findByInvitedUserAndStatus(AppUser invitedUser, SharedDebtStatus status);
}
