package com.essjr.DinMonex.debt;


import com.essjr.DinMonex.debt.dtos.CreateSharedDebtRequestDTO;
import com.essjr.DinMonex.debt.dtos.SharedDebtResponseDTO;
import com.essjr.DinMonex.debt.enums.SharedDebtStatus;
import com.essjr.DinMonex.security.AuthenticationHelper;
import com.essjr.DinMonex.user.AppUser;
import com.essjr.DinMonex.user.AppUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DebtService {

    private final SharedDebtRepository sharedDebtRepository;
    private final AppUserRepository appUserRepository;
    private final AuthenticationHelper authenticationHelper;

    public DebtService(SharedDebtRepository sharedDebtRepository, AppUserRepository appUserRepository, AuthenticationHelper authenticationHelper) {
        this.sharedDebtRepository = sharedDebtRepository;
        this.appUserRepository = appUserRepository;
        this.authenticationHelper = authenticationHelper;
    }

    @Transactional
    public SharedDebtResponseDTO createSharedDebt(CreateSharedDebtRequestDTO dto) {

        AppUser createdByUser = authenticationHelper.getCurrentUser();

        AppUser invitedUser = appUserRepository.findByEmail(dto.getInvitedUserEmail())
                .orElseThrow(()-> new IllegalArgumentException("Utilizador convidado não encontrado com o e-mail: " + dto.getInvitedUserEmail()));

        if (createdByUser.getId().equals(invitedUser.getId())) {
            throw new IllegalArgumentException("Você não pode criar uma dívida com você mesmo.");
        }

        SharedDebt newDebt = new SharedDebt();
        newDebt.setDescription(dto.getDescription());
        newDebt.setValue(dto.getValue());
        newDebt.setDueDate(dto.getDueDate());
        newDebt.setCreatedBy(createdByUser);
        newDebt.setInvitedUser(invitedUser);
        newDebt.setStatus(SharedDebtStatus.PENDING);

        SharedDebt saveDebt = sharedDebtRepository.save(newDebt);

        return convertToResponseDTO(saveDebt);
        }

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
