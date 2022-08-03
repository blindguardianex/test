package ru.smartech.app.resources.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.RestController;
import ru.smartech.app.configuration.security.AdvancedAuthentication;
import ru.smartech.app.configuration.security.MailPasswordAuthentication;
import ru.smartech.app.configuration.security.PhonePasswordAuthentication;
import ru.smartech.app.configuration.security.TokenProvider;
import ru.smartech.app.dto.MailAuthenticationDto;
import ru.smartech.app.dto.PhoneAuthenticationDto;
import ru.smartech.app.resources.AuthenticationResource;

@Slf4j
@RestController
public class AuthenticationResourceImpl implements AuthenticationResource {

    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;

    @Autowired
    public AuthenticationResourceImpl(AuthenticationManager authenticationManager, TokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }


    @Override
    public ResponseEntity<String> mailSignIn(MailAuthenticationDto authRequest) {
        var authenticate = (AdvancedAuthentication) authenticationManager.authenticate(MailPasswordAuthentication.from(authRequest));
        return ResponseEntity.ok(tokenProvider.createToken(
                TokenProvider.UserInfo.builder()
                        .login(authenticate.getName())
                        .id(authenticate.getUserId())
                        .build()));
    }

    @Override
    public ResponseEntity<String> phoneSignIn(PhoneAuthenticationDto authRequest) {
        var authenticate = (AdvancedAuthentication) authenticationManager.authenticate(PhonePasswordAuthentication.from(authRequest));
        return ResponseEntity.ok(tokenProvider.createToken(
                TokenProvider.UserInfo.builder()
                        .login(authenticate.getName())
                        .id(authenticate.getUserId())
                        .build()));
    }
}