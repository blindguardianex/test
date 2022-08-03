package ru.smartech.app.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.smartech.app.configuration.security.AdvancedAuthentication;
import ru.smartech.app.configuration.security.TokenProvider;
import ru.smartech.app.service.SecurityService;

@Slf4j
@Service
public class SecurityServiceImpl implements SecurityService{

    private final TokenProvider tokenProvider;

    @Autowired
    public SecurityServiceImpl(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    public AdvancedAuthentication validate(String token) {
        if (!tokenProvider.validateAccessToken(token))
            return null;

        return (AdvancedAuthentication) tokenProvider.getAuthentication(token);
    }
}
