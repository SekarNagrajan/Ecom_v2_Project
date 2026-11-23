package com.svm.ecom.security;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.svm.ecom.security.decommissioned.DecommissionedEndpointGuardFilter;
import com.svm.ecom.security.decommissioned.DecommissionedEndpointProperties;
import com.svm.ecom.security.quickreg.QuickRegistrationGuardFilter;
import com.svm.ecom.security.shippeo.ShippeoApiAuthorizationGuardFilter;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableConfigurationProperties({UserPermissionsConfig.class, DecommissionedEndpointProperties.class})
@EnableCaching
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;
    private final DecommissionedEndpointGuardFilter decommissionedEndpointGuardFilter;
    private final QuickRegistrationGuardFilter quickRegistrationGuardFilter;
    private final ShippeoApiAuthorizationGuardFilter shippeoApiAuthorizationGuardFilter;
    private final CustomUserDetailsService userDetailsService;
    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    private final RestAccessDeniedHandler restAccessDeniedHandler;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter,
                         DecommissionedEndpointGuardFilter decommissionedEndpointGuardFilter,
                         QuickRegistrationGuardFilter quickRegistrationGuardFilter,
                         ShippeoApiAuthorizationGuardFilter shippeoApiAuthorizationGuardFilter,
                         CustomUserDetailsService userDetailsService,
                         RestAuthenticationEntryPoint restAuthenticationEntryPoint,
                         RestAccessDeniedHandler restAccessDeniedHandler) {
        this.jwtFilter = jwtFilter;
        this.decommissionedEndpointGuardFilter = decommissionedEndpointGuardFilter;
        this.quickRegistrationGuardFilter = quickRegistrationGuardFilter;
        this.shippeoApiAuthorizationGuardFilter = shippeoApiAuthorizationGuardFilter;
        this.userDetailsService = userDetailsService;
        this.restAuthenticationEntryPoint = restAuthenticationEntryPoint;
        this.restAccessDeniedHandler = restAccessDeniedHandler;
    }

    /**
     * Configure the security filter chain.
     * This replaces the deprecated WebSecurityConfigurerAdapter approach.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        System.out.println("SecurityConfig loaded.");
        http.csrf().disable()
            .cors()
            .and()
            .sessionManagement().sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
                .antMatchers(
                    "/api/v1/auth/**",           // Allow login (no JWT required)
                    "/ecomapi-service/api/v1/auth/**", // ✅ Allow login with context path
                    "/health/live",              // ✅ Liveness probe (JIRA TSHG-3008 NFR, unauthenticated)
                    "/ecomapi-service/health/live", // ✅ Liveness probe with context path
                    "/health/ready",             // ✅ Readiness probe (JIRA TSHG-3008 NFR, unauthenticated)
                    "/ecomapi-service/health/ready", // ✅ Readiness probe with context path
                    "/api/v1/ecom/refresh",      // Allow refresh token (no JWT required)
                    "/ecomapi-service/api/v1/ecom/refresh", // ✅ Allow refresh with context path
                    "/swagger-ui/**",            // ✅ Allow Swagger UI
                    "/ecomapi-service/swagger-ui/**", // ✅ Allow Swagger UI with context path
                    "/swagger-ui.html",          // ✅ Allow Swagger UI HTML
                    "/ecomapi-service/swagger-ui.html", // ✅ Allow Swagger UI HTML with context path
                    "/customer/swagger-ui.html", // ✅ Allow Customer Swagger UI
                    "/ecomapi-service/customer/swagger-ui.html", // ✅ Allow Customer Swagger UI with context path
                    "/customer/swagger-ui.htm",                 // ✅ Allow Customer Swagger UI (.htm alias)
                    "/ecomapi-service/customer/swagger-ui.htm", // ✅ Allow Customer Swagger UI (.htm) with context path
                    "/customer/swagger-config",  // ✅ Allow Customer Swagger Config
                    "/ecomapi-service/customer/swagger-config", // ✅ Allow Customer Swagger Config with context path
                    "/public/swagger-ui.html",   // ✅ Allow Public Clean Swagger UI
                    "/ecomapi-service/public/swagger-ui.html", // ✅ Allow Public Clean Swagger UI with context path
                    "/customer/**",               // ✅ Allow Customer Swagger UI paths
                    "/ecomapi-service/customer/**", // ✅ Allow Customer Swagger UI with context path
                    "/company/**",               // ✅ Allow Company Swagger UI paths (legacy)
                    "/ecomapi-service/company/**", // ✅ Allow Company Swagger UI with context path (legacy)
                    "/v2/api-docs/**",           // ✅ Allow Swagger v2 API docs
                    "/ecomapi-service/v2/api-docs/**",
                    "/v3/api-docs/**",           // ✅ Allow OpenAPI v3 docs
                    "/ecomapi-service/v3/api-docs/**",
                    "/swagger-resources",        // ✅ Allow custom Swagger resources endpoint
                    "/swagger-resources/**",     // ✅ Allow other Swagger resources  
                    "/ecomapi-service/swagger-resources",
                    "/ecomapi-service/swagger-resources/**",
                    "/swagger-auth/**",          // ✅ Allow Swagger auth support endpoints
                    "/ecomapi-service/swagger-auth/**", // ✅ Allow Swagger auth support with context path
                    "/swagger-test/**",          // ✅ Allow Swagger test endpoints
                    "/ecomapi-service/swagger-test/**", // ✅ Allow Swagger test endpoints with context path
                    "/swagger-ui-custom.js",     // ✅ Allow custom Swagger JS
                    "/ecomapi-service/swagger-ui-custom.js",
                    "/webjars/**",              // ✅ Allow Swagger UI static resources
                    "/ecomapi-service/webjars/**"
                ).permitAll()
                .anyRequest().authenticated()  // All other endpoints require JWT authentication (no permission checking)
            .and()
            .exceptionHandling()
                .authenticationEntryPoint(restAuthenticationEntryPoint)
                .accessDeniedHandler(restAccessDeniedHandler)
            .and()
            .headers()
                .frameOptions().deny()
                .contentTypeOptions();

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        // Block decommissioned routes before controllers (after JWT is available for logging context).
        http.addFilterAfter(decommissionedEndpointGuardFilter, JwtAuthenticationFilter.class);
        // Gate POST/PUT requests for Quick Registration (incomplete) users.
        // Must run AFTER jwtFilter so SecurityContextHolder is populated.
        http.addFilterAfter(quickRegistrationGuardFilter, DecommissionedEndpointGuardFilter.class);
        http.addFilterAfter(shippeoApiAuthorizationGuardFilter, QuickRegistrationGuardFilter.class);
        return http.build();
    }

    /**
     * Configure the authentication provider with UserDetailsService and PasswordEncoder.
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Configure the AuthenticationManager.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Configure BCrypt password encoder for secure password hashing.
     * BCrypt is a strong hashing function designed for passwords.
     *
     * @return PasswordEncoder bean using BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
