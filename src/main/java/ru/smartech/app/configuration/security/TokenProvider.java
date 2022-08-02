package ru.smartech.app.configuration.security;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.smartech.app.entity.User;
import ru.smartech.app.service.TokenService;
import ru.smartech.app.service.UserService;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
public class TokenProvider {

    @Value("${token.access.secret:secretPhrase}")
    private String secret;
    @Value("${token.access.expired:PT2H}")
    private Duration sessionTime;

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


    public String createToken(User user) {
        String accessToken = createAccessToken(user);
        addTokensToUser(user, accessToken);

        return accessToken;
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = tokenToUserDetails(token);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    public String getUserId(String token) {
        try {
            return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getSubject();
        } catch (ExpiredJwtException ex) {
            return ex.getClaims().getSubject();
        }
    }

    public String tokenFromRequest(HttpServletRequest req) {
        String bearerToken = req.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public boolean validateAccessToken(String token) {
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

    private String createAccessToken(User user) {
        Claims claims = Jwts.claims().setSubject(user.getName());
        claims.put("id", user.getId());

        Date now = new Date();
        Date validateDate = new Date(now.getTime() + sessionTime.toMillis());

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validateDate)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    private String createRefreshToken(String token) {
        String tokenTail = token.substring(token.length() - 8);
        String randomString = UUID.randomUUID().toString();
        return tokenTail + "_" + randomString;
    }

    private UserDetails tokenToUserDetails(String token) {
        String login = getUserId(token);
        return org.springframework.security.core.userdetails.User.builder()
                .username(login)
                .password("")
                .authorities("USER")
                .build();
    }

    private void addTokensToUser(final User user, String accessToken) {
        tokenService.addToken(String.valueOf(user.getId()), accessToken);
    }

    private boolean validateRefreshToken(String refreshToken, String accessToken) {
        return refreshToken.startsWith(accessToken.substring(accessToken.length() - 8));
    }
}
