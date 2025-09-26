package com.essjr.DinMonex.controllers;

import com.essjr.DinMonex.dtos.CreateTransactionRequestDTO;
import com.essjr.DinMonex.dtos.TransactionResponseDTO;
import com.essjr.DinMonex.services.TransactionService;
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

    @GetMapping
    public ResponseEntity<List<TransactionResponseDTO>> getMyTransactions() {
        return ResponseEntity.ok(transactionService.getMyTransactions());
    }

    @PostMapping
    public ResponseEntity<TransactionResponseDTO> createMyTransaction(@RequestBody CreateTransactionRequestDTO dto) {
        return ResponseEntity.ok(transactionService.createMyTransaction(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMyTransaction(@PathVariable Long id) {
        try {
            transactionService.deleteMyTransaction(id);
            return ResponseEntity.noContent().build(); // Retorna 204 No Content em caso de sucesso
        } catch (Exception e) {
            // Pode adicionar tratamento de erros mais específico aqui
            return ResponseEntity.notFound().build();
        }
    }

}
