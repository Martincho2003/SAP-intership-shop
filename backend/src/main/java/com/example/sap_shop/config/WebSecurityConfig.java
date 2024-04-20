package com.example.sap_shop.config;

import com.example.sap_shop.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig{


    private final JwtRequestFilter jwtRequestFilter;

    @Autowired
    public WebSecurityConfig(JwtRequestFilter jwtRequestFilter) {
        this.jwtRequestFilter = jwtRequestFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((requests) -> {
                    requests.requestMatchers("/home", "/signup", "/login").permitAll();
                    requests.requestMatchers("/products/search", "/products", "/categories/search/{categoryName}",
                            "/categories", "/categories/search", "/sale/search", "/discount/search/**").permitAll();

                    requests.requestMatchers("/admin/**").hasRole("ADMIN");

                    requests.requestMatchers("/products/create", "/products/{name}", "/categories/{name}",
                            "/products/{productName}/assignCategory", "/categories/create",
                            "/sale/create", "/sale/update-settings", "/sale/update-categories", "/sale/delete/{saleName}",
                            "/discount", "/discount/update-settings", "/discount/update-products",
                            "/discount/delete/**").hasAnyRole("ADMIN", "WORKER");

                    requests.requestMatchers("/user").hasAnyRole("ADMIN", "WORKER", "USER");

                    requests.requestMatchers("/user/**", "/shopping-cart", "/shopping-cart/update").hasRole("USER");
                    requests.anyRequest().authenticated();

                })
                .formLogin(formLogin -> formLogin.disable())
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService();
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("*"); // Configure as needed
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}