package com.essjr.DinMonex.transaction;


import com.essjr.DinMonex.security.AuthenticationHelper;
import com.essjr.DinMonex.transaction.dtos.*;
import com.essjr.DinMonex.transaction.enuns.TransactionType;
import com.essjr.DinMonex.user.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AuthenticationHelper authenticationHelper;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository, AuthenticationHelper authenticationHelper) {
        this.transactionRepository = transactionRepository;
        this.authenticationHelper = authenticationHelper;
    }

    /**
     * NOVO MÉTODO: Busca todas as transações do utilizador logado.
     * @return Uma lista de DTOs de transação, incluindo as parcelas.
     */
    @Transactional(readOnly = true)
    public List<TransactionResponseDTO> getAllMyTransactions() {
        AppUser currentUser = authenticationHelper.getCurrentUser();
        List<Transaction> transactions = transactionRepository.findAllByAppUser(currentUser);
        return transactions.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TransactionResponseDTO> getMyConsumptionTransactions() {
        AppUser currentUser = authenticationHelper.getCurrentUser();
        List<Transaction> transactions = transactionRepository.findAllByAppUserAndType(currentUser, TransactionType.CONSUMPTION);
        return transactions.stream().map(this::convertToResponseDTO).collect(Collectors.toList());
    }

    @Transactional

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

    @Transactional
    public TransactionResponseDTO createCreditCardTransaction(CreditCardTransactionRequestDTO dto) {
        AppUser currentUser = authenticationHelper.getCurrentUser();

        Transaction parentTransaction = new Transaction();
        parentTransaction.setDescription(dto.getDescription());
        parentTransaction.setValue(dto.getValue());
        parentTransaction.setDueDate(dto.getDueDate());
        parentTransaction.setAppUser(currentUser);
        parentTransaction.setType(TransactionType.CREDIT_CARD);
        parentTransaction.setRecurring(false);
        parentTransaction.setTotalInstallments(dto.getTotalInstallments());

        if (dto.getTotalInstallments() != null && dto.getTotalInstallments() > 0) {
            BigDecimal installmentValue = dto.getValue().divide(new BigDecimal(dto.getTotalInstallments()), 2, RoundingMode.HALF_UP);

            for (int i = 1; i <= dto.getTotalInstallments(); i++) {
                Installment installment = new Installment();
                installment.setInstallmentNumber(i);
                installment.setValue(installmentValue);
                installment.setDueDate(dto.getDueDate().plusMonths(i - 1));
                installment.setPaid(false);
                installment.setTransaction(parentTransaction);
                parentTransaction.getInstallments().add(installment);
            }
        }

        Transaction savedTransaction = transactionRepository.save(parentTransaction);
        return convertToResponseDTO(savedTransaction);
    }

    @Transactional(readOnly = true)
    public TransactionResponseDTO getMyTransactionById(Long id) {
        AppUser currentUser = authenticationHelper.getCurrentUser();
        Transaction transaction = transactionRepository.findByIdAndAppUser(id, currentUser)
                .orElseThrow(() -> new IllegalStateException("Transação não encontrada ou acesso negado para o id: " + id));
        return convertToResponseDTO(transaction);
    }

    @Transactional
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

    @Transactional
    public void deleteMyTransaction(Long id) {
        AppUser currentUser = authenticationHelper.getCurrentUser();
        Transaction transaction = transactionRepository.findByIdAndAppUser(id, currentUser)
                .orElseThrow(() -> new IllegalStateException("Transação não encontrada ou acesso negado para o id: " + id));

        transactionRepository.delete(transaction);
    }


    /**
     * Converte uma entidade Transaction (com as suas parcelas)
     * para um TransactionResponseDTO.
     */
    private TransactionResponseDTO convertToResponseDTO(Transaction transaction) {
        TransactionResponseDTO dto = new TransactionResponseDTO();
        dto.setId(transaction.getId());
        dto.setDescription(transaction.getDescription());
        dto.setValue(transaction.getValue());
        dto.setDueDate(transaction.getDueDate());
        dto.setRecurring(transaction.isRecurring());
        dto.setType(transaction.getType().name());
        dto.setTotalInstallments(transaction.getTotalInstallments());

        // Se houver parcelas na entidade, converte-as para DTOs também.
        if (transaction.getInstallments() != null && !transaction.getInstallments().isEmpty()) {
            List<InstallmentDTO> installmentDTOs = transaction.getInstallments().stream()
                    .map(this::convertToInstallmentDTO)
                    .collect(Collectors.toList());
            dto.setInstallments(installmentDTOs);
        } else {
            dto.setInstallments(Collections.emptyList()); // Garante que a lista nunca seja nula.
        }

        return dto;
    }

    /**
     * Converte uma entidade Installment para um InstallmentDTO.
     */
    private InstallmentDTO convertToInstallmentDTO(Installment installment) {
        InstallmentDTO dto = new InstallmentDTO();
        dto.setId(installment.getId());
        dto.setInstallmentNumber(installment.getInstallmentNumber());
        dto.setValue(installment.getValue());
        dto.setDueDate(installment.getDueDate());
        dto.setPaid(installment.isPaid());
        return dto;
    }


    public ResumeTransactionDTO resumoMensal(int ano, int mes){

        YearMonth anoMes = YearMonth.of(ano, mes);

        LocalDate incio = anoMes.atDay(1);
        LocalDate fim = anoMes.atEndOfMonth();

        return transactionRepository.resumeTransaction(incio, fim);
    }


}



