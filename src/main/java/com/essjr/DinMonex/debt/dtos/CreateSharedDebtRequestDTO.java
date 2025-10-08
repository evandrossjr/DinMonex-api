package com.essjr.DinMonex.debt.dtos;

import org.hibernate.sql.results.graph.collection.internal.BagInitializer;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CreateSharedDebtRequestDTO {

    private String description;
    private BigDecimal value;
    private LocalDate dueDate;
    private String invitedUserEmail;


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

    public String getInvitedUserEmail() {
        return invitedUserEmail;
    }

    public void setInvitedUserEmail(String invitedUserEmail) {
        this.invitedUserEmail = invitedUserEmail;
    }
}
