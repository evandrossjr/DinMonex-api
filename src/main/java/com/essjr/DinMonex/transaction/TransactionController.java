package com.essjr.DinMonex.transaction;

import com.essjr.DinMonex.transaction.dtos.CreateTransactionRequestDTO;
import com.essjr.DinMonex.transaction.dtos.TransactionRequestDTO;
import com.essjr.DinMonex.transaction.dtos.TransactionResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions/consumption")
public class TransactionController {

    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    /**
     * GET /api/transactions/consumption
     * Obtém todas as transações de consumo do utilizador logado.
     */
    @GetMapping
    public ResponseEntity<List<TransactionResponseDTO>> getMyConsumptionTransactions() {
        return ResponseEntity.ok(transactionService.getMyConsumptionTransactions());
    }

    /**
     * POST /api/transactions/consumption
     * Cria uma nova transação de consumo para o utilizador logado.
     */
    @PostMapping
    public ResponseEntity<TransactionResponseDTO> createConsumptionTransaction(@RequestBody TransactionRequestDTO dto) {
        return ResponseEntity.ok(transactionService.createConsumptionTransaction(dto));
    }

    /**
     * GET /api/transactions/consumption/{id}
     * Obtém uma transação de consumo específica pelo seu ID, se pertencer ao utilizador logado.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponseDTO> getMyTransactionById(@PathVariable Long id) {
        try {
            TransactionResponseDTO transaction = transactionService.getMyTransactionById(id);
            return ResponseEntity.ok(transaction);
        } catch (IllegalStateException | SecurityException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * PUT /api/transactions/consumption/{id}
     * Atualiza uma transação de consumo existente, se pertencer ao utilizador logado.
     */
    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponseDTO> updateMyTransaction(@PathVariable Long id, @RequestBody TransactionRequestDTO dto) {
        try {
            TransactionResponseDTO updatedTransaction = transactionService.updateMyTransaction(id, dto);
            return ResponseEntity.ok(updatedTransaction);
        } catch (IllegalStateException | SecurityException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * DELETE /api/transactions/consumption/{id}
     * Apaga uma transação de consumo existente, se pertencer ao utilizador logado.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMyTransaction(@PathVariable Long id) {
        try {
            transactionService.deleteMyTransaction(id);
            return ResponseEntity.noContent().build(); // Retorna 204 No Content em caso de sucesso
        } catch (IllegalStateException | SecurityException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
