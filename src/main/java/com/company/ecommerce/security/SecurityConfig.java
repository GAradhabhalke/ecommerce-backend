package com.company.ecommerce.security;

import com.company.ecommerce.entity.Role;
import com.company.ecommerce.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomUserDetailsService customUserDetailsService;
    private final PasswordEncoder passwordEncoder;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          CustomAccessDeniedHandler customAccessDeniedHandler,
                          CustomUserDetailsService customUserDetailsService,
                          PasswordEncoder passwordEncoder) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.customAccessDeniedHandler = customAccessDeniedHandler;
        this.customUserDetailsService = customUserDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // 🟢 PUBLIC Endpoints
                .requestMatchers(
                    "/", "/index.html", "/style.css", "/app.js", "/categories.js",
                    "/cart.html", "/cart.js", "/checkout.html", "/checkout.js", 
                    "/order-success.html", "/register.html", "/register.js", 
                    "/profile.html", "/profile.js", "/favicon.ico", "/orders.html", "/orders.js"
                ).permitAll()
                .requestMatchers(HttpMethod.POST, "/auth/login", "/users").permitAll()
                .requestMatchers(HttpMethod.POST, "/payments/stripe/webhook").permitAll()
                .requestMatchers(HttpMethod.GET, "/products/**", "/categories").permitAll()
                .requestMatchers(HttpMethod.GET, "/products/search").permitAll()
                
                // 🔐 ADMIN Endpoints
                .requestMatchers("/admin.html", "/admin.js").hasAuthority(Role.ROLE_ADMIN.name())
                .requestMatchers("/categories/**").hasAuthority(Role.ROLE_ADMIN.name())
                .requestMatchers(HttpMethod.POST, "/products").hasAuthority(Role.ROLE_ADMIN.name())
                .requestMatchers(HttpMethod.PUT, "/products/**").hasAuthority(Role.ROLE_ADMIN.name())
                .requestMatchers(HttpMethod.DELETE, "/products/**").hasAuthority(Role.ROLE_ADMIN.name())
                .requestMatchers(HttpMethod.PUT, "/orders/*/status").hasAuthority(Role.ROLE_ADMIN.name())
                .requestMatchers(HttpMethod.DELETE, "/reviews/**").hasAuthority(Role.ROLE_ADMIN.name())

                // 🙍‍♂️ CUSTOMER Endpoints
                .requestMatchers(HttpMethod.PUT, "/users/me").hasAnyAuthority(Role.ROLE_CUSTOMER.name(), Role.ROLE_ADMIN.name())
                .requestMatchers("/cart/**").hasAuthority(Role.ROLE_CUSTOMER.name())
                .requestMatchers(HttpMethod.POST, "/orders").hasAuthority(Role.ROLE_CUSTOMER.name())
                .requestMatchers(HttpMethod.GET, "/orders/**").hasAnyAuthority(Role.ROLE_CUSTOMER.name(), Role.ROLE_ADMIN.name())
                .requestMatchers(HttpMethod.POST, "/reviews").hasAuthority(Role.ROLE_CUSTOMER.name())

                .anyRequest().authenticated()
            )
            .authenticationProvider(authenticationProvider())
            .exceptionHandling(exceptions -> exceptions.accessDeniedHandler(customAccessDeniedHandler))
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
