package com.essjr.DinMonex.transaction;


import com.essjr.DinMonex.security.AuthenticationHelper;
import com.essjr.DinMonex.strategy.AutoClassificationService;
import com.essjr.DinMonex.transaction.dtos.*;
import com.essjr.DinMonex.transaction.enuns.TransactionStatus;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AuthenticationHelper authenticationHelper;
    private final TransactionGroupRepository transactionGroupRepository;
    private final AutoClassificationService autoClassificationService;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository, AuthenticationHelper authenticationHelper, TransactionGroupRepository transactionGroupRepository, AutoClassificationService autoClassificationService) {
        this.transactionRepository = transactionRepository;
        this.authenticationHelper = authenticationHelper;
        this.transactionGroupRepository = transactionGroupRepository;
        this.autoClassificationService = autoClassificationService;
    }

    /**
     * NOVO MÉTODO: Busca todas as transações do utilizador logado.
     * @return Uma lista de DTOs de transação, incluindo as parcelas.
     */
    @Transactional(readOnly = true)
    public List<TransactionResponseDTO> getAllMyTransactions(int mes, int ano) {
        AppUser currentUser = authenticationHelper.getCurrentUser();

        DateRange datas = calcularDatas(mes, ano);

        List<Transaction> transactions = transactionRepository.findAllByAppUserAndDueDateBetween(currentUser, datas.inicio(), datas.fim());
        return transactions.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    public ResumeTransactionDTO resumoMensal(int ano, int mes){

        AppUser currentUser = authenticationHelper.getCurrentUser();

        DateRange datas = calcularDatas(mes, ano);
        return transactionRepository.resumeTransaction(datas.inicio(), datas.fim(), currentUser);
    }

    private DateRange calcularDatas(int mes, int ano){
        if (mes == 0 || ano == 0){
            LocalDate hoje = LocalDate.now();
            mes = hoje.getMonthValue();
            ano = hoje.getYear();
        }

        YearMonth anoMes = YearMonth.of(ano, mes);
        return new DateRange(anoMes.atDay(1), anoMes.atEndOfMonth());
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

        if (dto.getStatus() == TransactionStatus.PAID) {
            newTransaction.setStatus(TransactionStatus.PAID);
            newTransaction.setPaymentDate(LocalDate.now()); // <--- ADICIONE ISTO
        } else {
            newTransaction.setStatus(TransactionStatus.PENDING);
        }
        // Novo metodo para preencher o grupo utilizando o strategy
        TransactionGroup group = autoClassificationService.classify(dto);
        newTransaction.setGroup(group);

        Transaction savedTransaction = transactionRepository.save(newTransaction);
        return convertToResponseDTO(savedTransaction);
    }

    @Transactional
    public List<TransactionResponseDTO> createCreditCardTransaction(CreditCardTransactionRequestDTO dto) {

        AppUser currentUser = authenticationHelper.getCurrentUser();
        List<Transaction> transactionToSave = new ArrayList<>();

        // Novo metodo para preencher o grupo utilizando o strategy
        TransactionGroup group = autoClassificationService.classify(dto);

        int totalParcelas = (dto.getTotalInstallments() != null && dto.getTotalInstallments() > 0)
                ? dto.getTotalInstallments() : 1;

        BigDecimal valorParcela = dto.getValue().divide(BigDecimal.valueOf(totalParcelas), 2, RoundingMode.DOWN);

        BigDecimal sobra = dto.getValue().subtract(valorParcela.multiply(BigDecimal.valueOf(totalParcelas)));

        for (int i = 0; i < totalParcelas; i++) {
            Transaction t = new Transaction();

            t.setAppUser(currentUser);
            t.setGroup(group);
            t.setType(TransactionType.CREDIT_CARD);
            t.setRecurring(false);
            t.setStatus(TransactionStatus.PENDING);
            t.setTotalInstallments(totalParcelas);
            t.setCurrentInstallment(i + 1);


            if (i == 0){
                t.setDueDate(dto.getDueDate());
            } else {
                t.setDueDate(dto.getDueDate().plusMonths(i));
            }
            if (i == 0) {
                t.setValue((valorParcela.add(sobra)));
            } else {
                t.setValue(valorParcela);
            }



            String novaDescricao = String.format("%s (%d/%d)", dto.getDescription(), (i + 1), totalParcelas);
            t.setDescription(novaDescricao);

            transactionToSave.add(t);
        }



        List<Transaction> savedTransactions = transactionRepository.saveAll(transactionToSave);

        return savedTransactions.stream().map(this::convertToResponseDTO).collect(Collectors.toList());
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

        if (dto.getGroupId() != null){
            TransactionGroup group = transactionGroupRepository.findById(dto.getGroupId())
                    .orElseThrow(() -> new RuntimeException("Grupo não Encontrado"));

            transaction.setGroup(group);
        } else {
            transaction.setGroup(null);
        }

        if (dto.getStatus() != null) {
            // Se mudou para PAGO e não tinha data, marca hoje
            if (dto.getStatus() == TransactionStatus.PAID && transaction.getStatus() != TransactionStatus.PAID) {
                transaction.setStatus(TransactionStatus.PAID);
                transaction.setPaymentDate(LocalDate.now());
            }
            // Se mudou para PENDENTE, limpa a data
            else if (dto.getStatus() == TransactionStatus.PENDING) {
                transaction.setStatus(TransactionStatus.PENDING);
                transaction.setPaymentDate(null);
            }
        }

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

        if(transaction.getStatus() != null){
            dto.setStatus(transaction.getStatus().name());
        }
        dto.setTotalInstallments(transaction.getTotalInstallments());

        if(transaction.getGroup() != null){
            dto.setGroupId(transaction.getGroup().getId());
            dto.setGroupName(transaction.getGroup().getName());
            dto.setGroupColor(transaction.getGroup().getHexColor());
        }
        return dto;
    }

    @Transactional
    public void updateTransactionStatus(Long id, boolean isPaid) {
        AppUser currentUser = authenticationHelper.getCurrentUser();

        Transaction transaction = transactionRepository.findByIdAndAppUser(id, currentUser)
                .orElseThrow(() -> new IllegalStateException("Transação não Encontrada"));

        if (isPaid) {
            transaction.setStatus(TransactionStatus.PAID);
            if (transaction.getPaymentDate() == null) {
                transaction.setPaymentDate(LocalDate.now());
            }
        } else {
                transaction.setStatus(TransactionStatus.PENDING);
                transaction.setPaymentDate(null);
        }
        transactionRepository.save(transaction);

    }

}



