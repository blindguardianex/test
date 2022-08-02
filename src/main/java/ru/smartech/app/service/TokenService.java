package ru.smartech.app.service;

public interface TokenService {

    boolean tokenExists(String token);
    boolean addToken(String accessToken, String username);
}
