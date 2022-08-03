package ru.smartech.app.configuration.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.smartech.app.service.SecurityService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class TokenAuthorizationFilter extends OncePerRequestFilter {

    private final SecurityService securityService;
    private final String AUTHORIZATION_HEADER = "Authorization";

    @Autowired
    public TokenAuthorizationFilter(SecurityService securityService) {
        this.securityService = securityService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (isAuthenticationTokenPresent(request)) {
            log.debug("Authenticate with token...");
            String token = request.getHeader(AUTHORIZATION_HEADER);
            AdvancedAuthentication auth = securityService.validate(token);
            if (auth != null)
                setContext(auth);
        }
        filterChain.doFilter(request, response);
    }

    private void setContext(AdvancedAuthentication auth) {
        if (auth == null)
            SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext());
        else
            SecurityContextHolder.getContext().setAuthentication(auth);

    }

    private boolean isAuthenticationTokenPresent(HttpServletRequest request) {
        return request.getHeader(AUTHORIZATION_HEADER) != null;
    }
}
