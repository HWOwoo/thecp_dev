package com.thecp.dev.oauth2;

import com.thecp.dev.jwt.JwtProvider;
import com.thecp.dev.oauth2.entity.CustomOAuth2User;
import com.thecp.dev.user.entity.Role;
import com.thecp.dev.user.repositroy.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("OAuth2 Login 성공!");
        try {
            CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

            // User의 Role이 GUEST일 경우 처음 요청한 회원이므로 회원가입 페이지로 리다이렉트
            // TODO : createToken 매개인자값 추가
            if(oAuth2User.getSocialRole() == Role.GUEST) {
                String accessToken = jwtProvider.createToken(oAuth2User.getEmail(), null, null, null);
                response.addHeader("Authorization", "Bearer " + accessToken);
                response.sendRedirect("oauth2/sign-up"); // 프론트의 회원가입 추가 정보 입력 폼으로 리다이렉트

                jwtProvider.sendAccessAndRefreshToken(response, accessToken, null);

            } else {
                loginSuccess(response, oAuth2User); // 로그인에 성공한 경우 access, refresh 토큰 생성
            }
        } catch (Exception e) {
            throw e;
        }

    }

    // TODO : 소셜 로그인 시에도 무조건 토큰 생성하지 말고 JWT 인증 필터처럼 RefreshToken 유/무에 따라 다르게 처리해보기
    private void loginSuccess(HttpServletResponse response, CustomOAuth2User oAuth2User) throws IOException {
        String accessToken = jwtProvider.createToken(oAuth2User.getEmail(), null, null, null);
        String refreshToken = jwtProvider.createRefreshToken();
        response.addHeader("Authorization", "Bearer " + accessToken);
        response.addHeader("Authorization-refresh", "Bearer " + refreshToken);

        jwtProvider.sendAccessAndRefreshToken(response, accessToken, refreshToken);
        jwtProvider.updateRefreshToken(oAuth2User.getEmail(), refreshToken);
    }
}