package com.thecp.dev.jwt;

import com.thecp.dev.user.entity.Authority;
import com.thecp.dev.user.repositroy.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.buf.UEncoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;


@RequiredArgsConstructor
@Component
@Slf4j
public class JwtProvider {

    @Value("${jwt.secret}")
    private String salt;

    private Key secretKey;

    // 만료시간 : 1Hour
    private final long exp = 1000L * 60 * 60;

    // 만료시간 : 2Hour
    private final long expRefresh = 1000L * 60 * 60 * 2;

    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;

    @PostConstruct
    protected void init() {
        secretKey = Keys.hmacShaKeyFor(salt.getBytes(StandardCharsets.UTF_8));
    }

    public String createRefreshToken() {

        Date now = new Date();

        return Jwts.builder()
                .setExpiration(new Date(now.getTime() + expRefresh))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }
    // 해당 메서드의 예상 결과값은 아래와 같다.
    // eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0aGVjcCIsInJvbGVzIjpbeyJhdXRob3JpdHkiOiJST0xFX1VTRVIifV0sImlhdCI6MTYxNjQ0NjQyMCwiZXhwIjo

    // 토큰 생성
    public String createToken(String account, List<Authority> roles, String name, String nickname) {
        Claims claims = Jwts.claims().setSubject(account);
        claims.put("roles", roles);
        claims.put("name", name);
        claims.put("nickname", nickname);


        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + exp))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // 권한정보 획득
    // Spring Security 인증과정에서 권한확인을 위한 기능
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getAccount(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // 토큰에 담겨있는 유저 account 획득
    public String getAccount(String token) {
        return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody().getSubject();
    }

    // Authorization Header를 통해 인증을 한다.
    public String resolveToken(HttpServletRequest request) {
        return request.getHeader("Authorization");
    }

    public void sendAccessAndRefreshToken(HttpServletResponse response, String accessToken, String refreshToken){
        response.setStatus(HttpServletResponse.SC_OK);

        response.setHeader("Authorization", accessToken);
        response.setHeader("Authorization-refresh", refreshToken);
        log.info("Access Token, Refresh Token 헤더 설정 완료");
    }

    /**
     * AccessToken 헤더 설정
     */
    public void setAccessTokenHeader(HttpServletResponse response, String accessToken) {
        response.setHeader("Authorization", accessToken);
    }

    /**
     * RefreshToken 헤더 설정
     */
    public void setRefreshTokenHeader(HttpServletResponse response, String refreshToken) {
        response.setHeader("Authorization-refresh", refreshToken);
    }

    /**
     * AccessToken, RefreshToken 헤더 설정
     * @param response
     * @param accessToken
     * @param refreshToken
     */
    public void setAccessAndRefreshTokenHeaders(HttpServletResponse response, String accessToken, String refreshToken) {
        setAccessTokenHeader(response, accessToken);
        setRefreshTokenHeader(response, refreshToken);
        log.info("Access Token, Refresh Token 헤더 설정 완료");
    }

    /**
     * RefreshToken DB 저장(업데이트)
     */
    public void updateRefreshToken(String email, String refreshToken) {
        userRepository.findByEmail(email)
                .ifPresentOrElse(
                        user -> user.updateRefreshToken(refreshToken),
                        () -> new Exception("일치하는 회원이 없습니다.")
                );
    }

    // 토큰 검증
    public boolean validateToken(String token) {
        try {
            // Bearer 검증
            if (!token.substring(0, "BEARER ".length()).equalsIgnoreCase("BEARER ")) {
                return false;
            } else {
                token = token.split(" ")[1].trim();
            }
            Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            // 만료되었을 시 false
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }


}