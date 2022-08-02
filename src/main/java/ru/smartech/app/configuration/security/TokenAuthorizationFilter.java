//package ru.smartech.app.configuration.security;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.extern.slf4j.Slf4j;
//import org.brutforcer.user.dto.TokenInfoDto;
//import org.brutforcer.user.enums.HeaderNames;
//import org.brutforcer.user.service.SecurityService;
//import org.jetbrains.annotations.NotNull;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.util.UUID;
//import java.util.stream.Collectors;
//
//import static org.brutforcer.user.enums.HeaderNames.AUTHORIZATION_HEADER;
//import static org.brutforcer.user.enums.HeaderNames.TOKEN_INFO_HEADER;
//
//@Slf4j
//@Component
//public class TokenAuthorizationFilter extends OncePerRequestFilter {
//
//    private final ObjectMapper mapper;
//    private final SecurityService securityService;
//
//    @Autowired
//    public TokenAuthorizationFilter(ObjectMapper mapper,
//                                    SecurityService securityService) {
//        this.mapper = mapper;
//        this.securityService = securityService;
//    }
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        if (isAuthorizationInfoPresent(request)) {
//            log.debug("Filtered request with {} header: {}", TOKEN_INFO_HEADER, request.getHeader(TOKEN_INFO_HEADER.getKey()));
//            TokenInfoDto tokenInfo = mapper.readValue(request.getHeader(TOKEN_INFO_HEADER.getKey()), TokenInfoDto.class);
//            setContext(tokenInfo, getCorrelationId(request));
//        } else if (isAuthenticationTokenPresent(request)) {
//            log.info("Request no have {} header. Authenticate with token...", TOKEN_INFO_HEADER);
//            String token = request.getHeader(AUTHORIZATION_HEADER.getKey());
//            TokenInfoDto tokenInfo = securityService.validate(token);
//            if (tokenInfo != null)
//                setContext(tokenInfo, getCorrelationId(request));
//        }
//        filterChain.doFilter(request, response);
//    }
//
//    @NotNull
//    private UUID getCorrelationId(HttpServletRequest request) {
//        if (request.getHeader(HeaderNames.CORRELATION_HEADER.getKey()) != null)
//            return UUID.fromString(request.getHeader(HeaderNames.CORRELATION_HEADER.getKey()));
//        else {
//            UUID correlationId = UUID.randomUUID();
//            log.info("tmx-correlation-id generated in token authentication filter: {}.", correlationId);
//            return correlationId;
//        }
//    }
//
//    private void setContext(TokenInfoDto tokenInfo, UUID correlationId) {
//        if (tokenInfo == null) {
//            SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext());
//        } else {
//            SecurityContextHolder.getContext().setAuthentication(
//                    new AdvancedAuthentication(
//                            new UsernamePasswordAuthenticationToken(
//                                    new User(tokenInfo.username(), "", tokenInfo.roles().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList())),
//                                    "",
//                                    tokenInfo.roles().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList())
//                            ),
//                            tokenInfo.id(),
//                            correlationId)
//            );
//        }
//    }
//
//    private boolean isAuthorizationInfoPresent(HttpServletRequest request) {
//        return request.getHeader(TOKEN_INFO_HEADER.getKey()) != null;
//    }
//
//    private boolean isAuthenticationTokenPresent(HttpServletRequest request) {
//        return request.getHeader(AUTHORIZATION_HEADER.getKey()) != null;
//    }
//}
