package ru.smartech.app.service;

import org.jetbrains.annotations.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.smartech.app.configuration.security.AdvancedAuthentication;
import ru.smartech.app.configuration.security.TokenProvider;

public interface SecurityService {

    AdvancedAuthentication validate(String token);

    @Nullable
    static AdvancedAuthentication getAdvancedContext() {
        if (SecurityContextHolder.getContext() == null)
            return null;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null)
            return null;
        else if (auth instanceof AdvancedAuthentication)
            return (AdvancedAuthentication) auth;
        else
            return null;
    }
}
