package com.pg30.webechannellingspringboot.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@EnableWebSecurity
@Configuration
public class SecurityConfiguration {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/","/user/signin", "/user/signup", "/css/**", "/js/**"
                                        ,"/feedback/**","/admin/feedback","/admin/feedback/**","/doctor/report").permitAll()
//                        .requestMatchers("/appointments","/timeslots/*","/book","/my-bookings").hasAnyRole("ADMIN", "DOCTOR", "PATIENT")
                        .requestMatchers("/appointments","/timeslots/*","/book/**","/my-bookings","/doctor/signup","/doctor/login").permitAll()
                        .requestMatchers("/doctor/slots","/api/doctor/**", "/api/doctor/slots/**","/api/doctor/slots", "/doctor/dashboard", "/doctor/api/profile","/doctor/signup").hasAnyRole("DOCTOR","ADMIN")
                                .requestMatchers(
                                        "/doctor/report",
                                        "/doctor/report/save",
                                        "/patient/reports",
                                        "/admin/reports",
                                        "/report/download/**",
                                        "/admin/reports/delete/**"
                                ).permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/user/signin")
                        .loginProcessingUrl("/user/signin")
                        .successHandler(roleBasedAuthenticationSuccessHandler())
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )    .csrf(csrf -> csrf
                        .ignoringRequestMatchers(
                                "/book",
                                "/cancel-booking",
                                "/doctor/signup",
                                "/doctor/api/**",
                                "/doctor/report/save"
                        )

                );

        return http.build();
    }
    @Bean
    public AuthenticationSuccessHandler roleBasedAuthenticationSuccessHandler() {
        return new RoleBasedAuthenticationSuccessHandler();
    }
}