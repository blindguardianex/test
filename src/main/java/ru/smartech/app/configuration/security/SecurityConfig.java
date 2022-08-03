package ru.smartech.app.configuration.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.annotation.PostConstruct;

@Configuration
//@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final String[] GET_WHITE_LIST = {
            // -- swagger ui
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui/**",
            "/webjars/**",
            //status
            "/actuator/**",
            "/api/v1/healthStatus",
    };
    private final String[] POST_WHITE_LIST = {
            //sign
            "/api/v1/authenticate/sign/mail",
            "/api/v1/authenticate/sign/phone",
    };

    private final MailAuthenticationProvider mailAuthenticationProvider;
    private final PhoneAuthenticationProvider phoneAuthenticationProvider;
    private final TokenAuthorizationFilter tokenAuthorizationFilter;

    public SecurityConfig(MailAuthenticationProvider mailAuthenticationProvider,
                          PhoneAuthenticationProvider phoneAuthenticationProvider,
                          TokenAuthorizationFilter tokenAuthorizationFilter) {
        this.mailAuthenticationProvider = mailAuthenticationProvider;
        this.phoneAuthenticationProvider = phoneAuthenticationProvider;
        this.tokenAuthorizationFilter = tokenAuthorizationFilter;
    }

    @Bean
    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return new ProviderManager(mailAuthenticationProvider, phoneAuthenticationProvider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable()
                .formLogin().disable()
                .csrf().disable()
                .addFilterAt(tokenAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling().authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        authorizeRequests(
                http.authorizeRequests()
        )
                .anyRequest()
                .authenticated();

    }

    @PostConstruct
    public void enableAuthCtxOnSpawnedThreads() {
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_THREADLOCAL);
    }

    public ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry authorizeRequests(
            ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry authorizeRequests) {
        authorizeRequests
                // status
                .antMatchers(HttpMethod.GET, GET_WHITE_LIST).permitAll()
                .antMatchers(HttpMethod.POST, POST_WHITE_LIST).permitAll()
                .antMatchers(HttpMethod.GET, "/api/v1/public/**").permitAll()
                // preloaders
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll();

        return authorizeRequests;
    }
}
