package com.jaky.ecomerce.eshop.configuration;

import com.jaky.ecomerce.eshop.properties.ApplicationProperties;
import com.jaky.ecomerce.eshop.security.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * @author : Jaiky Nguyen
 * @since : 10/24/2023, 8:33 PM
 **/
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class WebSecurityConfiguration {
    private final JwtFilter jwtFilter;
    private final ApplicationProperties applicationProperties;

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(httpSecurityCorsConfigurer ->
                        httpSecurityCorsConfigurer.configurationSource(corsConfigurationSource()))
                .sessionManagement(httpSecuritySessionManagementConfigurer ->
                        httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorizationManagerRequestMatcherRegistry ->
                        authorizationManagerRequestMatcherRegistry
                                .requestMatchers("/api/v1/rest",
                                        "/api/v1/rest/product/*",
                                        "/api/v1/rest/admin/*",
                                        "/api/v1/rest/admin/user/*",
                                        "/api/v1/rest/registration",
                                        "/api/v1/rest/forgot",
                                        "/api/v1/rest/reset/**",
                                        "/api/v1/rest/activate/*",
                                        "/api/v1/rest/menu/**",
                                        "/api/v1/rest/cart",
                                        "/api/v1/rest/cart/*",
                                        "/api/v1/rest/order",
                                        "/api/v1/rest/order/*",
                                        "/api/v1/rest/user/*",
                                        "/img/**",
                                        "/static/**",
                                        "/activate/*",
                                        "/menu/**").permitAll()
                                .requestMatchers("/api/v1/rest/login").permitAll()
                                .anyRequest().authenticated()
                )

                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
        ;
        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(applicationProperties.getAllowedOrigins());
        configuration.setAllowedMethods(applicationProperties.getAllowedMethods());
        configuration.setAllowedHeaders(applicationProperties.getAllowedHeaders());
        configuration.setAllowCredentials(applicationProperties.isAllowCredentials());
        configuration.setExposedHeaders(applicationProperties.getExposedHeaders());
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

}
