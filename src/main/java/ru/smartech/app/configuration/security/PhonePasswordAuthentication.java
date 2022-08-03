package ru.smartech.app.configuration.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import ru.smartech.app.dto.MailAuthenticationDto;
import ru.smartech.app.dto.PhoneAuthenticationDto;

import java.util.Collection;

public class PhonePasswordAuthentication extends UsernamePasswordAuthenticationToken {

    public PhonePasswordAuthentication(Object principal, Object credentials) {
        super(principal, credentials);
    }

    public PhonePasswordAuthentication(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
    }

    public static PhonePasswordAuthentication from(PhoneAuthenticationDto authenticationDto) {
        return new PhonePasswordAuthentication(authenticationDto.getPhone(), authenticationDto.getPassword());
    }
}
