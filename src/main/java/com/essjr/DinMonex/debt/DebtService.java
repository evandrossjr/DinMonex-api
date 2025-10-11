package com.essjr.DinMonex.debt;


import com.essjr.DinMonex.debt.dtos.CreateSharedDebtRequestDTO;
import com.essjr.DinMonex.debt.dtos.SharedDebtResponseDTO;
import com.essjr.DinMonex.debt.enums.SharedDebtStatus;
import com.essjr.DinMonex.security.AuthenticationHelper;
import com.essjr.DinMonex.user.AppUser;
import com.essjr.DinMonex.user.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DebtService {

    private final SharedDebtRepository sharedDebtRepository;
    private final AppUserRepository appUserRepository;
    private final AuthenticationHelper authenticationHelper;

    @Autowired
    public DebtService(SharedDebtRepository sharedDebtRepository, AppUserRepository appUserRepository, AuthenticationHelper authenticationHelper) {
        this.sharedDebtRepository = sharedDebtRepository;
        this.appUserRepository = appUserRepository;
        this.authenticationHelper = authenticationHelper;
    }

    @Transactional
    public SharedDebtResponseDTO createSharedDebt(CreateSharedDebtRequestDTO dto) {
        AppUser createdByUser = authenticationHelper.getCurrentUser();
        AppUser invitedUser = appUserRepository.findByEmail(dto.getInvitedUserEmail())
                .orElseThrow(() -> new IllegalArgumentException("Utilizador convidado não encontrado com o e-mail: " + dto.getInvitedUserEmail()));

        if (createdByUser.getId().equals(invitedUser.getId())) {
            throw new IllegalArgumentException("Você не pode criar uma dívida consigo mesmo.");
        }

        SharedDebt newDebt = new SharedDebt();
        newDebt.setDescription(dto.getDescription());
        newDebt.setValue(dto.getValue());
        newDebt.setDueDate(dto.getDueDate());
        newDebt.setCreatedBy(createdByUser);
        newDebt.setInvitedUser(invitedUser);
        newDebt.setStatus(SharedDebtStatus.PENDING);

        SharedDebt savedDebt = sharedDebtRepository.save(newDebt);
        return convertToResponseDTO(savedDebt);
    }

    /**
     * MÉTODO: Obtém todos os convites de dívida pendentes para o utilizador logado.
     * @return Uma lista de DTOs com os detalhes dos convites.
     */
    @Transactional(readOnly = true)
    public List<SharedDebtResponseDTO> getMyPendingInvitations() {
        AppUser currentUser = authenticationHelper.getCurrentUser();
        List<SharedDebt> pendingDebts = sharedDebtRepository.findByInvitedUserAndStatus(currentUser, SharedDebtStatus.PENDING);

        return pendingDebts.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * MÉTODO: Permite que o utilizador logado responda a um convite de dívida.
     * @param debtId O ID do convite de dívida.
     * @param newStatus O novo status (ACCEPTED ou REJECTED).
     * @return Um DTO com os detalhes da dívida atualizada.
     */
    @Transactional
    public SharedDebtResponseDTO respondToInvitation(Long debtId, SharedDebtStatus newStatus) {
        AppUser currentUser = authenticationHelper.getCurrentUser();

        // 1. Encontra a dívida pelo seu ID.
        SharedDebt debt = sharedDebtRepository.findById(debtId)
                .orElseThrow(() -> new IllegalArgumentException("Convite de dívida não encontrado com o ID: " + debtId));

        // 2. VERIFICAÇÃO DE SEGURANÇA CRUCIAL:
        // Garante que o utilizador que está a responder é realmente o utilizador que foi convidado.
        if (!debt.getInvitedUser().getId().equals(currentUser.getId())) {
            throw new SecurityException("Acesso negado. Você não pode responder a este convite.");
        }

        // 3. VERIFICAÇÃO DE LÓGICA DE NEGÓCIO:
        // Garante que só se pode responder a convites que estão pendentes.
        if (debt.getStatus() != SharedDebtStatus.PENDING) {
            throw new IllegalStateException("Este convite já foi respondido.");
        }

        // 4. Garante que a resposta é válida (aceitar ou recusar).
        if (newStatus != SharedDebtStatus.ACCEPTED && newStatus != SharedDebtStatus.REJECTED) {
            throw new IllegalArgumentException("Resposta inválida. O status deve ser ACCEPTED ou REJECTED.");
        }

        // 5. Atualiza o status e salva.
        debt.setStatus(newStatus);
        SharedDebt updatedDebt = sharedDebtRepository.save(debt);

        return convertToResponseDTO(updatedDebt);
    }


    // --- Método de Mapeamento ---
    private SharedDebtResponseDTO convertToResponseDTO(SharedDebt debt) {
        SharedDebtResponseDTO dto = new SharedDebtResponseDTO();
        dto.setId(debt.getId());
        dto.setDescription(debt.getDescription());
        dto.setValue(debt.getValue());
        dto.setDueDate(debt.getDueDate());
        dto.setStatus(debt.getStatus().name());

        SharedDebtResponseDTO.UserSummaryDTO createdByDto = new SharedDebtResponseDTO.UserSummaryDTO();
        createdByDto.setId(debt.getCreatedBy().getId());
        createdByDto.setName(debt.getCreatedBy().getName());
        dto.setCreatedBy(createdByDto);

        SharedDebtResponseDTO.UserSummaryDTO invitedUserDto = new SharedDebtResponseDTO.UserSummaryDTO();
        invitedUserDto.setId(debt.getInvitedUser().getId());
        invitedUserDto.setName(debt.getInvitedUser().getName());
        dto.setInvitedUser(invitedUserDto);

        return dto;
    }
}
