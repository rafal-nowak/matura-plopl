package pl.lodz.p.liceum.matura.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

//    private final List<String> allowedOrigins;
//    private final List<String> allowedMethods;
//    private final List<String> allowedHeaders;
//    private final List<String> exposedHeaders;

//    public SecurityConfig(
//            @Value("#{'${cors.allowed-origins}'.split(',')}") List<String> allowedOrigins,
//            @Value("#{'${cors.allowed-methods}'.split(',')}") List<String> allowedMethods,
//            @Value("#{'${cors.allowed-headers}'.split(',')}") List<String> allowedHeaders,
//            @Value("#{'${cors.exposed-headers}'.split(',')}") List<String> exposedHeaders
//    ) {
//        this.allowedOrigins = allowedOrigins;
//        this.allowedMethods = allowedMethods;
//        this.allowedHeaders = allowedHeaders;
//        this.exposedHeaders = exposedHeaders;
//    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder
    ) {
        DaoAuthenticationProvider daoAuthenticationProvider =
                new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        return daoAuthenticationProvider;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        CorsConfiguration corsConfiguration = new CorsConfiguration();

        corsConfiguration.applyPermitDefaultValues();
//        corsConfiguration.setAllowedOrigins(allowedOrigins);
//        corsConfiguration.setAllowedMethods(allowedMethods);
//        corsConfiguration.setAllowedHeaders(allowedHeaders);
//        corsConfiguration.setExposedHeaders(exposedHeaders);
        corsConfiguration.addAllowedOrigin("http://127.0.0.1:3000/");

        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }

}
