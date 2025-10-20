package com.feniksovich.bankcards.config;

import com.feniksovich.bankcards.entity.Role;
import com.feniksovich.bankcards.security.BearerJwtAuthenticationTokenConverter;
import com.feniksovich.bankcards.security.ExtendedUserDetailsService;
import com.feniksovich.bankcards.security.JwtAuthenticationProvider;
import com.feniksovich.bankcards.security.crypto.AesGcmCryptoService;
import com.feniksovich.bankcards.security.crypto.CryptoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.web.cors.CorsConfigurationSource;

import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfigurer {

    private final ExtendedUserDetailsService userDetailsService;

    private static final int PASSWORD_ENCODER_BCRYPT_ROUNDS = 12;

    @Autowired
    public SecurityConfigurer(ExtendedUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity httpSecurity,
            AuthenticationManager authenticationManager,
            BearerJwtAuthenticationTokenConverter bearerJwtConverter,
            CorsConfigurationSource corsConfigurationSource
    ) throws Exception {
        final AuthenticationFilter jwtAuthenticationFilter = new AuthenticationFilter(authenticationManager, bearerJwtConverter);
        jwtAuthenticationFilter.setRequestMatcher(PathPatternRequestMatcher.withDefaults().matcher("/**"));
        jwtAuthenticationFilter.setSuccessHandler((_, _, _) -> {});
        return httpSecurity
                .cors(configurer -> configurer.configurationSource(corsConfigurationSource))
                .sessionManagement(configurer ->
                        configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .exceptionHandling(configurer ->
                        configurer.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                )
                .authorizeHttpRequests(registry -> {
                    registry.requestMatchers("/account/**").hasRole(Role.USER.name());
                    registry.requestMatchers("/cards/**").hasRole(Role.ADMIN.name());
                    registry.requestMatchers("/users/**").hasRole(Role.ADMIN.name());
                    registry.requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll();
                    registry.anyRequest().authenticated();
                })
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        return Role.hierarchy();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(PASSWORD_ENCODER_BCRYPT_ROUNDS);
    }

    @Bean
    public AuthenticationManager authenticationManager(
            PasswordEncoder passwordEncoder,
            JwtAuthenticationProvider jwtAuthenticationProvider
    ) {
        // Username and password authentication provider
        final DaoAuthenticationProvider daoAuthenticationProvider =
                new DaoAuthenticationProvider(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);

        // Register along with JWT authentication provider
        return new ProviderManager(daoAuthenticationProvider, jwtAuthenticationProvider);
    }

    @Bean
    public CryptoService cryptoService(SecurityProperties securityProperties) {
        final String aesKeyBase64 = securityProperties.crypto().aesKeyBase64();
        final byte[] aesKeyBytes = Base64.getDecoder().decode(aesKeyBase64);
        final SecretKeySpec aesKey = new SecretKeySpec(aesKeyBytes, "AES");
        return new AesGcmCryptoService(aesKey);
    }
}
