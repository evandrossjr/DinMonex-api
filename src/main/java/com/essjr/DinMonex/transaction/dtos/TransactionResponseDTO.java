package com.essjr.DinMonex.transaction.dtos;


import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para enviar os dados de uma transação para o frontend.
 * Expõe apenas os campos que o frontend precisa de ver.
 */
public class TransactionResponseDTO {

    private Long id;
    private String description;
    private BigDecimal value;
    private LocalDate dueDate;
    private boolean isRecurring;


    public TransactionResponseDTO(Long id, String description, BigDecimal value, LocalDate dueDate, boolean isRecurring) {
        this.id = id;
        this.description = description;
        this.value = value;
        this.dueDate = dueDate;
        this.isRecurring = isRecurring;
    }

    public TransactionResponseDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public boolean isRecurring() {
        return isRecurring;
    }

    public void setRecurring(boolean recurring) {
        isRecurring = recurring;
    }
}
