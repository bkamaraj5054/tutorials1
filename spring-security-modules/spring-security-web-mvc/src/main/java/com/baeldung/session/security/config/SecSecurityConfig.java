package com.baeldung.session.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import com.baeldung.security.MySimpleUrlAuthenticationSuccessHandler;

@Configuration
// @ImportResource({ "classpath:webSecurityConfig.xml" })
public class SecSecurityConfig {

    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        UserDetails user1 = User.withUsername("user1")
            .password(passwordEncoder().encode("user1Pass"))
            .roles("USER")
            .build();

        UserDetails admin1 = User.withUsername("admin1")
            .password(passwordEncoder().encode("admin1Pass"))
            .roles("ADMIN")
            .build();

        return new InMemoryUserDetailsManager(user1, admin1);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf()
            .disable()
            .authorizeRequests()
            .antMatchers("/anonymous*")
            .anonymous()
            .antMatchers("/login*", "/invalidSession*", "/sessionExpired*", "/foo/**")
            .permitAll()
            .anyRequest()
            .authenticated()
            .and()
            .formLogin()
            .loginPage("/login.html")
            .loginProcessingUrl("/login")
            .successHandler(successHandler())
            .failureUrl("/login.html?error=true")
            .and()
            .logout()
            .deleteCookies("JSESSIONID")
            .and()
            .rememberMe()
            .key("uniqueAndSecret")
            .tokenValiditySeconds(86400)
            .and()
            .sessionManagement()
            .sessionFixation()
            .migrateSession()
            .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            .invalidSessionUrl("/invalidSession.html")
            .maximumSessions(2)
            .expiredUrl("/sessionExpired.html");
        return http.build();
    }

    private AuthenticationSuccessHandler successHandler() {
        return new MySimpleUrlAuthenticationSuccessHandler();
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
