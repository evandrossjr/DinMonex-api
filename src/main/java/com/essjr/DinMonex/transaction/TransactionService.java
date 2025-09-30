package com.essjr.DinMonex.transaction;


import com.essjr.DinMonex.security.AuthenticationHelper;
import com.essjr.DinMonex.transaction.dtos.CreateTransactionRequestDTO;
import com.essjr.DinMonex.transaction.dtos.TransactionRequestDTO;
import com.essjr.DinMonex.transaction.dtos.TransactionResponseDTO;
import com.essjr.DinMonex.transaction.enuns.TransactionType;
import com.essjr.DinMonex.user.AppUser;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AuthenticationHelper authenticationHelper;

    public TransactionService(TransactionRepository transactionRepository, AuthenticationHelper authenticationHelper) {
        this.transactionRepository = transactionRepository;
        this.authenticationHelper = authenticationHelper;
    }


    public List<TransactionResponseDTO> getMyConsumptionTransactions(){
        // 1. Obtém o utilizador da sessão de segurança.
        AppUser currentUser = authenticationHelper.getCurrentUser();
        List<Transaction> transactions = transactionRepository.findAllByAppUserAndType(currentUser, TransactionType.CONSUMPTION);


        // 2. Chama o método do repositório que já filtra por utilizador, garantindo o isolamento.
        return transactions.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }


    public TransactionResponseDTO getMyTransactionById(Long id) {
        AppUser currentUser = authenticationHelper.getCurrentUser();
        Transaction transaction = transactionRepository.findByIdAndAppUser(id, currentUser)
                .orElseThrow(() -> new IllegalStateException("Transação não encontrada ou acesso negado para o id: " + id));
        return convertToResponseDTO(transaction);
    }



    public TransactionResponseDTO createConsumptionTransaction(TransactionRequestDTO dto) {
        AppUser currentUser = authenticationHelper.getCurrentUser();

        Transaction newTransaction = new Transaction();
        newTransaction.setDescription(dto.getDescription());
        newTransaction.setValue(dto.getValue());
        newTransaction.setDueDate(dto.getDueDate());
        newTransaction.setRecurring(dto.isRecurring());
        newTransaction.setAppUser(currentUser);
        newTransaction.setType(TransactionType.CONSUMPTION);

        Transaction savedTransaction = transactionRepository.save(newTransaction);
        return convertToResponseDTO(savedTransaction);
    }




    public TransactionResponseDTO updateMyTransaction(Long id, TransactionRequestDTO dto) {
        AppUser currentUser = authenticationHelper.getCurrentUser();
        Transaction transaction = transactionRepository.findByIdAndAppUser(id, currentUser)
                .orElseThrow(() -> new IllegalStateException("Transação não encontrada ou acesso negado para o id: " + id));

        transaction.setDescription(dto.getDescription());
        transaction.setValue(dto.getValue());
        transaction.setDueDate(dto.getDueDate());
        transaction.setRecurring(dto.isRecurring());

        Transaction updatedTransaction = transactionRepository.save(transaction);
        return convertToResponseDTO(updatedTransaction);
    }

    public void deleteMyTransaction(Long id) {
        AppUser currentUser = authenticationHelper.getCurrentUser();
        Transaction transaction = transactionRepository.findByIdAndAppUser(id, currentUser)
                .orElseThrow(() -> new IllegalStateException("Transação não encontrada ou acesso negado para o id: " + id));

        transactionRepository.delete(transaction);
    }

    private TransactionResponseDTO convertToResponseDTO(Transaction transaction) {
        TransactionResponseDTO dto = new TransactionResponseDTO();
        dto.setId(transaction.getId());
        dto.setDescription(transaction.getDescription());
        dto.setValue(transaction.getValue());
        dto.setDueDate(transaction.getDueDate());
        dto.setRecurring(transaction.isRecurring());
        return dto;
    }
}



