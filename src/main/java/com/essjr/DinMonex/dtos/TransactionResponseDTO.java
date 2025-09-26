package com.essjr.DinMonex.dtos;


import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para enviar os dados de uma transação para o frontend.
 * Expõe apenas os campos que o frontend precisa de ver.
 */
public class TransactionResponseDTO {

    private Long id;
    private String description;
    private BigDecimal amount;
    private LocalDate date;

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
