package com.essjr.DinMonex.transaction;


import com.essjr.DinMonex.security.AuthenticationHelper;
import com.essjr.DinMonex.transaction.dtos.CreateTransactionRequestDTO;
import com.essjr.DinMonex.transaction.dtos.CreditCardTransactionRequestDTO;
import com.essjr.DinMonex.transaction.dtos.TransactionRequestDTO;
import com.essjr.DinMonex.transaction.dtos.TransactionResponseDTO;
import com.essjr.DinMonex.transaction.enuns.TransactionType;
import com.essjr.DinMonex.user.AppUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

    /**
     * Cria uma nova transação de cartão de crédito e as suas parcelas.
     * @param dto Os dados da compra parcelada.
     * @return A transação "mãe" que foi criada.
     */
    @Transactional // Garante que a transação e todas as suas parcelas sejam salvas numa única operação.
    public TransactionResponseDTO createCreditCardTransaction(CreditCardTransactionRequestDTO dto) {
        AppUser currentUser = authenticationHelper.getCurrentUser();

        // 1. Cria a transação "mãe" que representa a compra total.
        Transaction parentTransaction = new Transaction();
        parentTransaction.setDescription(dto.getDescription());
        parentTransaction.setValue(dto.getValue()); // O valor total da compra
        parentTransaction.setDueDate(dto.getDueDate()); // A data da compra
        parentTransaction.setAppUser(currentUser);
        parentTransaction.setType(TransactionType.CREDIT_CARD);
        parentTransaction.setRecurring(false); // Uma compra parcelada não é recorrente no mesmo sentido que uma conta.
        parentTransaction.setTotalInstallments(dto.getTotalInstallments());

        // 2. Lógica para calcular e criar as parcelas.
        if (dto.getTotalInstallments() > 0) {
            // Calcula o valor de cada parcela, dividindo o total e arredondando para 2 casas decimais.
            BigDecimal installmentValue = dto.getValue().divide(new BigDecimal(dto.getTotalInstallments()), 2, RoundingMode.HALF_UP);

            for (int i = 1; i <= dto.getTotalInstallments(); i++) {
                Installment installment = new Installment();
                installment.setInstallmentNumber(i);
                installment.setValue(installmentValue);
                // A data de vencimento de cada parcela é i-1 meses após a data da primeira.
                installment.setDueDate(dto.getDueDate().plusMonths(i - 1));
                installment.setPaid(false);
                // A ligação crucial: diz a qual transação "mãe" esta parcela pertence.
                installment.setTransaction(parentTransaction);

                // Adiciona a parcela à lista da transação "mãe".
                parentTransaction.getInstallments().add(installment);
            }
        }

        // 3. Salva a transação "mãe".
        // Graças ao CascadeType.ALL, ao salvar a transação, todas as parcelas na sua lista
        // serão salvas automaticamente no banco de dados.
        Transaction savedTransaction = transactionRepository.save(parentTransaction);

        return convertToResponseDTO(savedTransaction);
    }


        public TransactionResponseDTO getMyTransactionById(Long id) {
        AppUser currentUser = authenticationHelper.getCurrentUser();
        Transaction transaction = transactionRepository.findByIdAndAppUser(id, currentUser)
                .orElseThrow(() -> new IllegalStateException("Transação não encontrada ou acesso negado para o id: " + id));
        return convertToResponseDTO(transaction);
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



