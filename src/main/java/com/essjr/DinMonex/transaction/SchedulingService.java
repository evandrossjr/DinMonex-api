package com.essjr.DinMonex.transaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
public class SchedulingService {

    //Logger oara ver as mensagens do robo que verifica as transações
    private static final Logger log = LoggerFactory.getLogger(SchedulingService.class);

    private final TransactionRepository transactionRepository;

    public SchedulingService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    /**
     * Tarefa agendada para criar transações recorrentes para o mês atual.
     * A expressão cron "0 0 1 * * ?" significa: "Executa à 1 da manhã, todos os dias do mês".
     * A lógica interna garante que as transações só são criadas uma vez.
     */
    @Scheduled(cron = "0 0 1 * * ?")
    @Transactional // Garante que todas as operações na base de dados neste método sejam atómicas.
    public void createNextMonthRecurringTransactions() {
        log.info("Iniciando a verificação de transações recorrentes...");

        // 1. CALCULAR O PERÍODO: Mês passado.
        LocalDate today = LocalDate.now();
        YearMonth previousMonth = YearMonth.from(today.minusMonths(1));
        LocalDate startDate = previousMonth.atDay(1);
        LocalDate endDate = previousMonth.atEndOfMonth();

        // 2. EXECUTAR A CONSULTA: Obter as transações recorrentes do mês passado.
        List<Transaction> recurringTransactionsFromLastMonth = transactionRepository.findByIsRecurringTrueAndDueDateBetween(startDate, endDate);

        log.info("Encontradas {} transações recorrentes do mês passado para processar.", recurringTransactionsFromLastMonth.size());

        // 3. CRIAR AS NOVAS TRANSAÇÕES
        for (Transaction oldTransaction : recurringTransactionsFromLastMonth) {
            LocalDate newDueDate = oldTransaction.getDueDate().plusMonths(1);

            // BÓNUS DE SEGURANÇA: Verifica se uma transação para o novo mês já não foi cria
            // da.
            boolean alreadyExists = transactionRepository.existsByDescriptionAndDueDateAndAppUser(
                    oldTransaction.getDescription(), newDueDate, oldTransaction.getAppUser());

            if (!alreadyExists) {
                //Se não existir cria uma nova transação
                Transaction newTransaction = new Transaction();
                newTransaction.setDescription((oldTransaction.getDescription()));
                newTransaction.setValue(oldTransaction.getValue());
                newTransaction.setType(oldTransaction.getType());
                newTransaction.setRecurring(true); // Mantém a recorrência
                newTransaction.setAppUser(oldTransaction.getAppUser());
                newTransaction.setDueDate(newDueDate); // A nova data de vencimento

                transactionRepository.save(newTransaction);
                log.info("Criada nova transação recorrente {} para o utilizador {}", newTransaction.getDescription(), newTransaction.getAppUser().getEmail() );
            }else {
                log.warn("Transação recorrente '{}' para o utilizador {} já existe para a data {}. A saltar.",
                        oldTransaction.getDescription(), oldTransaction.getAppUser().getEmail(), newDueDate);
            }
        }
        log.info("Verificação de transações recorrentes concluída.");
    }


}
