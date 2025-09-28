package com.essjr.DinMonex.services;


import com.essjr.DinMonex.config.AuthenticationHelper;
import com.essjr.DinMonex.dtos.CreateTransactionRequestDTO;
import com.essjr.DinMonex.dtos.TransactionResponseDTO;
import com.essjr.DinMonex.model.AppUser;
import com.essjr.DinMonex.model.Transaction;
import com.essjr.DinMonex.repositories.TransactionRepository;
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

    /**
     * Obtém APENAS as transações do utilizador atualmente logado.
     * @return Uma lista das transações do utilizador.
     */
    public List<TransactionResponseDTO> getMyTransactions(){
        // 1. Obtém o utilizador da sessão de segurança.
        AppUser currentUser = authenticationHelper.getCurrentUser();
        List<Transaction> transactions = transactionRepository.findAllByAppUser(currentUser);


        // 2. Chama o método do repositório que já filtra por utilizador, garantindo o isolamento.
        return transactions.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }



    /**
     * Cria uma nova transação a partir de um DTO.
     */
    public TransactionResponseDTO createMyTransaction(CreateTransactionRequestDTO dto) {
        AppUser currentUser = authenticationHelper.getCurrentUser();

        // Converte o DTO para uma Entidade antes de salvar.
        Transaction newTransaction = new Transaction();
        newTransaction.setDescription(dto.getDescription());
        newTransaction.setAmount(dto.getAmount());
        newTransaction.setDate(dto.getDate());
        newTransaction.setAppUser(currentUser); // Associa ao utilizador logado.

        Transaction savedTransaction = transactionRepository.save(newTransaction);

        // Converte a Entidade salva de volta para um DTO para retornar ao frontend.
        return convertToResponseDTO(savedTransaction);

    }


    /**
     * Apaga uma transação APENAS se ela pertencer ao utilizador logado.
     * @param transactionId O ID da transação a ser apagada.
     */
    public void deleteMyTransaction(Long transactionId) {
        AppUser currentuser = authenticationHelper.getCurrentUser();

        // Busca a transação no banco de dados.
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalStateException("Transação não encontrada com o id: " + transactionId));

        // A VERIFICAÇÃO DE SEGURANÇA CRUCIAL:
        // Compara o ID do "dono" da transação com o ID do utilizador que está a fazer o pedido.
        if (!transaction.getAppUser().getId().equals(currentuser.getId())) {
            // Lança uma exceção de segurança para impedir a operação.
            // É uma boa prática lançar uma exceção genérica para não revelar que o recurso existe.
            throw new SecurityException("Acesso negado para este recurso");
        }

        transactionRepository.delete((transaction));
    }

    /**
     * Converte uma entidade Transaction para um TransactionResponseDTO.
     */
    private TransactionResponseDTO convertToResponseDTO(Transaction transaction) {
        TransactionResponseDTO dto = new TransactionResponseDTO();
        dto.setId(transaction.getId());
        dto.setDescription(transaction.getDescription());
        dto.setAmount(transaction.getAmount());
        dto.setDate(transaction.getDate());
        return dto;
    }

}
