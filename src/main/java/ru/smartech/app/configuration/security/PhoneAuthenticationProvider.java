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
public class PhoneAuthenticationProvider implements AuthenticationProvider {

    private final PasswordEncoder encoder;
    private final PhoneUserDetailsService userDetailsService;

    @Autowired
    public PhoneAuthenticationProvider(PasswordEncoder encoder, PhoneUserDetailsService userDetailsService) {
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
        return authentication.equals(PhonePasswordAuthentication.class);
    }
}
