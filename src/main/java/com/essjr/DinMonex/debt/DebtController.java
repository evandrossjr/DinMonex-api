package com.essjr.DinMonex.debt;

import com.essjr.DinMonex.debt.dtos.CreateSharedDebtRequestDTO;
import com.essjr.DinMonex.debt.dtos.SharedDebtResponseDTO;
import com.essjr.DinMonex.debt.enums.SharedDebtStatus;
import com.essjr.DinMonex.shared.ApiResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/debts")
public class DebtController {

    private final DebtService debtService;

    @Autowired
    public DebtController(DebtService debtService) {
        this.debtService = debtService;
    }

    /**
     * Endpoint para criar um novo convite de dívida partilhada.
     * Mapeado para POST /api/debts
     */
    @PostMapping
    public ResponseEntity<?> createSharedDebt(@RequestBody CreateSharedDebtRequestDTO dto) {
        try {
            SharedDebtResponseDTO response = debtService.createSharedDebt(dto);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponseDTO(e.getMessage()));
        }
    }

    /**
     * NOVO ENDPOINT: Obtém a lista de convites de dívida pendentes para o utilizador logado.
     * Mapeado para GET /api/debts/invitations/pending
     */
    @GetMapping("/invitations/pending")
    public ResponseEntity<List<SharedDebtResponseDTO>> getMyPendingInvitations() {
        List<SharedDebtResponseDTO> invitations = debtService.getMyPendingInvitations();
        return ResponseEntity.ok(invitations);
    }

    /**
     * NOVO ENDPOINT: Permite que o utilizador convidado aceite um convite.
     * Mapeado para POST /api/debts/invitations/{id}/accept
     * @param id O ID do convite de dívida a ser aceite.
     */
    @PostMapping("/invitations/{id}/accept")
    public ResponseEntity<?> acceptInvitation(@PathVariable Long id) {
        try {
            SharedDebtResponseDTO response = debtService.respondToInvitation(id, SharedDebtStatus.ACCEPTED);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Retorna um erro genérico para diferentes tipos de falha (não encontrado, acesso negado, já respondido)
            return ResponseEntity.badRequest().body(new ApiResponseDTO(e.getMessage()));
        }
    }

    /**
     * NOVO ENDPOINT: Permite que o utilizador convidado recuse um convite.
     * Mapeado para POST /api/debts/invitations/{id}/reject
     * @param id O ID do convite de dívida a ser recusado.
     */
    @PostMapping("/invitations/{id}/reject")
    public ResponseEntity<?> rejectInvitation(@PathVariable Long id) {
        try {
            SharedDebtResponseDTO response = debtService.respondToInvitation(id, SharedDebtStatus.REJECTED);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponseDTO(e.getMessage()));
        }
    }
}


