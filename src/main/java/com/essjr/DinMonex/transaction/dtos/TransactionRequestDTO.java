package com.essjr.DinMonex.transaction.dtos;

import com.essjr.DinMonex.transaction.enuns.TransactionStatus;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para receber dados do frontend para criar ou atualizar uma transação.
 */
public class TransactionRequestDTO {

    private String description;
    private BigDecimal value;
    private LocalDate dueDate;

    //A anotação @JsonProperty("isRecurring") diz ao Jackson
    // para procurar explicitamente por um campo chamado "isRecurring" no JSON
    // e mapeá-lo para este campo Java.
    @JsonProperty("isRecurring")
    private boolean isRecurring;

    private TransactionStatus status;


    // Getters e Setters
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getValue() { return value; }
    public void setValue(BigDecimal value) { this.value = value; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public boolean isRecurring() { return isRecurring; }
    public void setRecurring(boolean recurring) { this.isRecurring = recurring; }
    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }
}