package com.essjr.DinMonex.debt;

import com.essjr.DinMonex.debt.dtos.CreateSharedDebtRequestDTO;
import com.essjr.DinMonex.debt.dtos.SharedDebtResponseDTO;
import com.essjr.DinMonex.shared.ApiResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
     *
     * @param dto O corpo da requisição com os detalhes da dívida e o e-mail do convidado.
     * @return Os detalhes da dívida criada ou uma mensagem de erro.
     */
    @PostMapping
    public ResponseEntity<?> createSharedDebt(@RequestBody CreateSharedDebtRequestDTO dto) {
        try {
            SharedDebtResponseDTO response = debtService.createSharedDebt(dto);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            // Se o serviço lançar uma exceção (ex: utilizador não encontrado, dívida consigo mesmo),
            // retorna uma resposta de erro 400 Bad Request com a mensagem da exceção.
            return ResponseEntity.badRequest().body(new ApiResponseDTO(e.getMessage()));
        }
    }
}
