package ru.smartech.app.configuration.security;

import io.jsonwebtoken.*;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.smartech.app.enums.Roles;
import ru.smartech.app.service.TokenService;
import ru.smartech.app.service.UserService;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class TokenProvider {

    @Value("${token.access.secret:secretPhrase}")
    private String secret;
    @Value("${token.access.expired:PT2H}")
    private Duration sessionTime;
    private final String TOKEN_PREFIX = "Bearer ";

    private final TokenService tokenService;
    private final UserService userService;

    @Autowired
    public TokenProvider(TokenService tokenService,
                         UserService userService) {
        this.tokenService = tokenService;
        this.userService = userService;
    }

    @PostConstruct
    void init() {
        secret = Base64.getEncoder().encodeToString(secret.getBytes());
    }


    public String createToken(UserInfo info) {
        String accessToken = createAccessToken(info);
        addTokensToUser(info, accessToken);

        return accessToken;
    }

    public Authentication getAuthentication(String token) {
        if (token.startsWith(TOKEN_PREFIX))
            token = token.substring(TOKEN_PREFIX.length());

        UserInfo info = tokenInfo(token);
        return new AdvancedAuthentication(
                new UsernamePasswordAuthenticationToken(
                        info.getLogin(),
                        "",
                        List.of(new SimpleGrantedAuthority(Roles.USER.name())))
                , info.getId());
    }

    public String getLogin(String token) {
        try {
            return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getSubject();
        } catch (ExpiredJwtException ex) {
            return ex.getClaims().getSubject();
        }
    }

    public long getUserId(String token) {
        try {
            return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().get("userId", Long.class);
        } catch (ExpiredJwtException ex) {
            return 0;
        }
    }

    public boolean validateAccessToken(String token) {
        if (token == null)
            return false;
        if (token.startsWith(TOKEN_PREFIX))
            token = token.substring(TOKEN_PREFIX.length());

        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            if (claims.getBody().getExpiration().before(new Date())) {
                log.warn("Token is expired!");
                return false;
            }
            if (!tokenService.tokenExists(token)) {
                log.error("Access token is not found");
                return false;
            }
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            log.warn("JWT token is expired or invalid");
            return false;
        }
    }

    private String createAccessToken(UserInfo user) {
        Claims claims = Jwts.claims().setSubject(user.getLogin());
        claims.put("userId", user.getId());

        Date now = new Date();
        Date validateDate = new Date(now.getTime() + sessionTime.toMillis());

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validateDate)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    private UserInfo tokenInfo(String token) {
        String login = getLogin(token);
        long id = getUserId(token);
        return UserInfo.builder()
                .login(login)
                .id(id)
                .build();
    }

    private void addTokensToUser(final UserInfo user, String accessToken) {
        tokenService.addToken(accessToken, String.valueOf(user.getId()));
    }

    private boolean validateRefreshToken(String refreshToken, String accessToken) {
        return refreshToken.startsWith(accessToken.substring(accessToken.length() - 8));
    }

    @Builder
    @Getter
    public static class UserInfo {
        private final String login;
        private final long id;
    }
}
