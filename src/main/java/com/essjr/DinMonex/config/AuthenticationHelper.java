package com.essjr.DinMonex.config;

import com.essjr.DinMonex.model.AppUser;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Componente de ajuda para obter informações do utilizador autenticado.
 */
@Component
public class AuthenticationHelper {

    /**
     * Obtém o objeto AppUser do utilizador atualmente autenticado a partir do contexto de segurança.
     * @return O AppUser autenticado.
     * @throws IllegalStateException se não houver um utilizador autenticado ou o objeto principal não for do tipo AppUser.
     */
    public AppUser getCurrentUser() {

        // Obtém o "principal" (a identidade do utilizador) do contexto de segurança do Spring.
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Como o nosso JpaUserDetailsService retorna um AppUser, podemos fazer o cast com segurança.
        if (principal instanceof AppUser){
            return (AppUser) principal;
        } else {
            // Este erro não deve acontecer numa aplicação configurada corretamente.
            throw new IllegalStateException("O objeto de autenticação principal não é uma instância de AppUser.");
        }
    }
}
