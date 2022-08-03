package ru.smartech.app.configuration.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import ru.smartech.app.dto.MailAuthenticationDto;

import java.util.Collection;

public class MailPasswordAuthentication extends UsernamePasswordAuthenticationToken {

    public MailPasswordAuthentication(Object principal, Object credentials) {
        super(principal, credentials);
    }

    public MailPasswordAuthentication(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
    }

    public static MailPasswordAuthentication from(MailAuthenticationDto authenticationDto) {
        return new MailPasswordAuthentication(authenticationDto.getEmail(), authenticationDto.getPassword());
    }
}
