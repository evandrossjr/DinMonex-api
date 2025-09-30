package com.essjr.DinMonex.transaction.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para receber os dados de uma nova transação do frontend.
 * Contém apenas os campos que o utilizador precisa de fornecer.
 */
public class CreateTransactionRequestDTO {

    private String description;
    private BigDecimal amount;
    private LocalDate date;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
