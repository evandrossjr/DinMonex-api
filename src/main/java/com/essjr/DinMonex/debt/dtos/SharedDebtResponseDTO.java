package com.essjr.DinMonex.debt.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

public class SharedDebtResponseDTO {

    private Long id;
    private String description;
    private BigDecimal value;
    private LocalDate dueDate;
    private String status;
    private UserSummaryDTO createdBy;
    private UserSummaryDTO invitedUser;

    // Classe interna estática para representar um resumo do utilizador.
    // Isto evita expor a entidade AppUser completa, incluindo a senha.
    public static class UserSummaryDTO {
        private Long id;
        private String name;

        // Getters e Setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public UserSummaryDTO getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UserSummaryDTO createdBy) {
        this.createdBy = createdBy;
    }

    public UserSummaryDTO getInvitedUser() {
        return invitedUser;
    }

    public void setInvitedUser(UserSummaryDTO invitedUser) {
        this.invitedUser = invitedUser;
    }
}
