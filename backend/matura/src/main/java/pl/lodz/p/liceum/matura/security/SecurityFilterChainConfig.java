package pl.lodz.p.liceum.matura.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityFilterChainConfig {

    private final AuthenticationProvider authenticationProvider;
    private final JWTAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/actuator/health").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/users").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/actuator/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/users/me").authenticated()
                        .requestMatchers("/api/v1/users/**").hasAnyRole("ADMIN", "STUDENT", "INSTRUCTOR") // TODO Revert it back to ADMIN only when security is properly configured
                        .requestMatchers(HttpMethod.POST, "api/v1/templates").hasAnyRole("ADMIN", "INSTRUCTOR")
                        .requestMatchers(HttpMethod.PUT, "api/v1/templates").hasAnyRole("ADMIN", "INSTRUCTOR")
                        .requestMatchers(HttpMethod.DELETE, "api/v1/templates/**").hasAnyRole("ADMIN", "INSTRUCTOR")
                        .requestMatchers(HttpMethod.POST, "/api/v1/tasks").hasAnyRole("ADMIN", "INSTRUCTOR", "STUDENT")
                        .requestMatchers(HttpMethod.PUT, "api/v1/tasks").hasAnyRole("ADMIN", "INSTRUCTOR")
                        .requestMatchers(HttpMethod.DELETE, "api/v1/templates/**").hasAnyRole("ADMIN", "INSTRUCTOR")
                        .requestMatchers(HttpMethod.GET, "api/v1/tasks/**").hasAnyRole("ADMIN", "INSTRUCTOR", "STUDENT")
                        .anyRequest().authenticated()
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
