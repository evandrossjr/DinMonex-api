package com.essjr.DinMonex.transaction;

import com.essjr.DinMonex.transaction.dtos.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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
    public ResponseEntity<List<TransactionResponseDTO>> getAllMyTransactions(
            @RequestParam(required = false, defaultValue = "0") int mes,
            @RequestParam(required = false, defaultValue = "0") int ano) {

        List<TransactionResponseDTO> transactions = transactionService.getAllMyTransactions(mes, ano);

        return ResponseEntity.ok(transactions);
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
    public ResponseEntity<List<TransactionResponseDTO>> createCreditCardTransaction(@RequestBody CreditCardTransactionRequestDTO dto) {
        List<TransactionResponseDTO> transactions = transactionService.createCreditCardTransaction(dto);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/dashboard/resumo")
    public ResponseEntity<ResumeTransactionDTO> getResumo(@RequestParam(defaultValue = "0") int mes,
                                                           @RequestParam(defaultValue = "0") int ano){

        if (mes == 0 || ano == 0) {
            LocalDate hoje = LocalDate.now();
            mes = hoje.getMonthValue();
            ano = hoje.getYear();
        }

        var resumo = transactionService.resumoMensal(ano, mes);
        return ResponseEntity.ok(resumo);
    }


}

