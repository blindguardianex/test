package ru.smartech.app.configuration.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class MailAuthenticationProvider implements AuthenticationProvider {

    private final PasswordEncoder encoder;
    private final MailUserDetailsService userDetailsService;

    @Autowired
    public MailAuthenticationProvider(PasswordEncoder encoder, MailUserDetailsService userDetailsService) {
        this.encoder = encoder;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials()
                .toString();

        SecurityUser details = (SecurityUser) userDetailsService.loadUserByUsername(username);
        if (encoder.matches(password, details.getPassword())) {
            return new AdvancedAuthentication(new UsernamePasswordAuthenticationToken(details, "",details.getAuthorities()), details.getUserId());
        } else {
            throw new BadCredentialsException("Authentication failed");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(MailPasswordAuthentication.class);
    }
}
