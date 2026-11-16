package com.homeservices.config;

import com.homeservices.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
public class SecurityConfig {
    @Bean
    PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    DaoAuthenticationProvider authenticationProvider(CustomUserDetailsService userDetailsService,
                                                       PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (request, response, authentication) -> {
            boolean admin = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            boolean provider = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_PROVIDER"));
            if (admin) response.sendRedirect("/admin/dashboard");
            else if (provider) response.sendRedirect("/provider/dashboard");
            else response.sendRedirect("/customer/dashboard");
        };
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http,
                                             AuthenticationSuccessHandler successHandler,
                                             DaoAuthenticationProvider authenticationProvider) throws Exception {
        http
            .authenticationProvider(authenticationProvider)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/services", "/services/**", "/login", "/register", "/register/**",
                    "/css/**", "/js/**", "/images/**", "/error", "/access-denied",
                    "/api/v1/categories", "/api/v1/services", "/api/v1/providers/*/availability").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/provider/**").hasRole("PROVIDER")
                .requestMatchers("/customer/**").hasRole("CUSTOMER")
                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/v1/provider/**").hasRole("PROVIDER")
                .requestMatchers("/api/v1/bookings/**").hasRole("CUSTOMER")
                .anyRequest().authenticated())
            .formLogin(form -> form.loginPage("/login").loginProcessingUrl("/login")
                .usernameParameter("email").passwordParameter("password")
                .successHandler(successHandler).failureUrl("/login?error"))
            .logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/?logout").invalidateHttpSession(true).deleteCookies("JSESSIONID"))
            .csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
            .exceptionHandling(ex -> ex
                .defaultAuthenticationEntryPointFor(
                    new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                    new AntPathRequestMatcher("/api/**"))
                .accessDeniedHandler((request, response, exception) -> {
                    if (request.getRequestURI().startsWith("/api/")) {
                        response.sendError(HttpStatus.FORBIDDEN.value(), "Access denied");
                    } else {
                        response.sendRedirect("/access-denied");
                    }
                }));
        return http.build();
    }
}
