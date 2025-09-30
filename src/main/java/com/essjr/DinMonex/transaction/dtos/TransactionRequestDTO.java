package com.essjr.DinMonex.transaction.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para receber dados do frontend para criar ou atualizar uma transação.
 */
public class TransactionRequestDTO {

    private String description;
    private BigDecimal value;
    private LocalDate dueDate;
    private boolean isRecurring;

    // Getters e Setters
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getValue() { return value; }
    public void setValue(BigDecimal value) { this.value = value; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public boolean isRecurring() { return isRecurring; }
    public void setRecurring(boolean recurring) { this.isRecurring = recurring; }
}