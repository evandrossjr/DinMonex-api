package com.essjr.DinMonex.transaction;

import com.essjr.DinMonex.transaction.dtos.CreateTransactionRequestDTO;
import com.essjr.DinMonex.transaction.dtos.CreditCardTransactionRequestDTO;
import com.essjr.DinMonex.transaction.dtos.TransactionRequestDTO;
import com.essjr.DinMonex.transaction.dtos.TransactionResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    /**
     * NOVO ENDPOINT: Obtém TODAS as transações do utilizador logado.
     * Mapeado para GET /api/transactions
     */
    @GetMapping
    public ResponseEntity<List<TransactionResponseDTO>> getAllMyTransactions() {
        return ResponseEntity.ok(transactionService.getAllMyTransactions());
    }

    // --- Endpoints para Contas de Consumo ---

    @GetMapping("/consumption")
    public ResponseEntity<List<TransactionResponseDTO>> getMyConsumptionTransactions() {
        return ResponseEntity.ok(transactionService.getMyConsumptionTransactions());
    }

    @PostMapping("/consumption")
    public ResponseEntity<TransactionResponseDTO> createConsumptionTransaction(@RequestBody TransactionRequestDTO dto) {
        return ResponseEntity.ok(transactionService.createConsumptionTransaction(dto));
    }

    @GetMapping("/consumption/{id}")
    public ResponseEntity<TransactionResponseDTO> getMyTransactionById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(transactionService.getMyTransactionById(id));
        } catch (IllegalStateException | SecurityException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/consumption/{id}")
    public ResponseEntity<TransactionResponseDTO> updateMyTransaction(@PathVariable Long id, @RequestBody TransactionRequestDTO dto) {
        try {
            return ResponseEntity.ok(transactionService.updateMyTransaction(id, dto));
        } catch (IllegalStateException | SecurityException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/consumption/{id}")
    public ResponseEntity<Void> deleteMyTransaction(@PathVariable Long id) {
        try {
            transactionService.deleteMyTransaction(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException | SecurityException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // --- Endpoint para Cartão de Crédito ---

    @PostMapping("/credit-card")
    public ResponseEntity<TransactionResponseDTO> createCreditCardTransaction(@RequestBody CreditCardTransactionRequestDTO dto) {
        return ResponseEntity.ok(transactionService.createCreditCardTransaction(dto));
    }
}

