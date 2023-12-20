package news.crawler.config;

import news.crawler.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtAuthorizationFilter jwtAuthorizationFilter;

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, PasswordEncoder passwordEncoder) throws Exception {
        return http
                .getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder)
                .and()
                .build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
        .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.requestMatchers("/login/**", "/api/events/**", "/swagger-ui/**", "/v3/**")
                .permitAll())
                .authorizeHttpRequests(auth -> auth.requestMatchers("/api/source/**").hasRole("ADMIN")
                        // .authorizeHttpRequests(auth -> auth.requestMatchers("/user/**").hasAnyRole("USER", "ADMIN")
                .anyRequest()
                .authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();

      //  httpSecurity.csrf(AbstractHttpConfigurer::disable)
       //        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        //        .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
         //       .authorizeHttpRequests(auth -> auth.requestMatchers("/login/**", "/noauth/**", "/swagger-ui/**", "/v3/**").permitAll())
          //      .authorizeHttpRequests(auth -> auth.requestMatchers("/admin/**").hasRole("ADMIN"))
            //    .authorizeHttpRequests(auth -> auth.requestMatchers("/user/**").hasAnyRole("USER", "ADMIN"))
            //    .authorizeHttpRequests(auth -> auth.anyRequest().authenticated());
       // return httpSecurity.build();
    }

    @Bean
    @Deprecated
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
}