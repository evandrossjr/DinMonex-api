package com.essjr.DinMonex.config;

import com.essjr.DinMonex.services.JpaUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


/**
 * @Configuration indica que esta é uma classe de configuração do Spring.
 * @EnableWebSecurity ativa a configuração de segurança web do Spring Security.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JpaUserDetailsService jpaUserDetailsService;
    private final JwtAuthFilter jwtAuthFilter;


    public SecurityConfig(JpaUserDetailsService jpaUserDetailsService, JwtAuthFilter jwtAuthFilter) {
        this.jpaUserDetailsService = jpaUserDetailsService;
        this.jwtAuthFilter = jwtAuthFilter;
    }

    /**
     * @Bean Este é o Bean mais importante. Ele define a cadeia de filtros de segurança que
     * protege a nossa aplicação contra pedidos não autorizados. É como o segurança principal
     * da nossa API.
     * @param http O objeto HttpSecurity que usamos para construir as regras.
     * @return A cadeia de filtros de segurança configurada.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Desabilita a proteção CSRF. Isto é comum para APIs REST stateless
                // que não usam sessões baseadas em cookies.
                .csrf(csrf -> csrf.disable())

                // 2. Define as regras de autorização para os pedidos HTTP.
                .authorizeHttpRequests(auth -> auth
                        // Permite que qualquer pessoa (mesmo sem autenticação) aceda aos endpoints
                        // que começam com /api/auth/ (ex: /login, /register).
                        .requestMatchers("/api/auth/**").permitAll()

                        // Exige que todos os outros pedidos na aplicação sejam autenticados.
                        .anyRequest().authenticated()
                )

                // 3. Configura a gestão de sessão para ser STATELESS (sem estado).
                // Isto diz ao Spring Security para NÃO criar sessões HTTP, pois usaremos JWTs.
                // Cada pedido deve conter o seu próprio token para ser autenticado.
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 4. Regista o nosso 'AuthenticationProvider' (definido abaixo).
                .authenticationProvider(authenticationProvider())

                // 5. Adiciona o nosso filtro JWT personalizado na cadeia de filtros.
                // Ele será executado ANTES do filtro de autenticação padrão de username/password.
                // É o nosso "porteiro" que verifica se o pedido tem um token válido.
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * @Bean Este Bean define o provedor de autenticação. É o componente que
     * realmente faz a verificação das credenciais.
     * @return Um DaoAuthenticationProvider configurado.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        // Usamos o DaoAuthenticationProvider, que é o padrão para autenticação baseada em base de dados.
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        // Diz ao provedor como encontrar um utilizador (usando o nosso JpaUserDetailsService).
        authProvider.setUserDetailsService(jpaUserDetailsService);
        // Diz ao provedor qual o algoritmo de criptografia a usar para verificar as senhas.
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * @Bean Expõe o AuthenticationManager do Spring Security como um Bean.
     * O AuthenticationManager é o orquestrador que utiliza o AuthenticationProvider
     * para tentar autenticar um utilizador. Iremos injetá-lo no nosso AuthService para
     * processar os pedidos de login.
     * @param config A configuração de autenticação do Spring.
     * @return O AuthenticationManager.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * @Bean Este Bean define qual o algoritmo de criptografia de senhas que
     * a nossa aplicação irá usar.
     * @return Uma instância de BCryptPasswordEncoder.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        // Usamos o BCrypt, que é o padrão da indústria: forte, lento e inclui um "salt"
        // aleatório em cada senha para máxima segurança.
        return new BCryptPasswordEncoder();
    }
}
