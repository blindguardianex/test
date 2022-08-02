package ru.smartech.app.service.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.smartech.app.service.TokenService;

import java.time.Duration;

@Slf4j
@Service
public class CacheTokenService implements TokenService {

    private final Cache<String, String> cache;

    public CacheTokenService(@Value("${token.access.expired:PT2H}") Duration sessionTime) {
        cache = CacheBuilder.newBuilder()
                .expireAfterWrite(sessionTime)
                .build();
    }

    @Override
    public boolean tokenExists(String token) {
        return cache.getIfPresent(token) != null;
    }

    @Override
    public boolean addToken(String accessToken, String username) {
        cache.put(accessToken, username);
        return true;
    }
}
