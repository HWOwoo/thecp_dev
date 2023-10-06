package com.thecp.dev.jwt.controller;

import com.thecp.dev.jwt.JwtProvider;
import com.thecp.dev.user.entity.User;
import com.thecp.dev.user.repositroy.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/jwt")
@RequiredArgsConstructor
public class JwtController {

    private final JwtProvider jwtProvider;

    private final UserRepository userRepository;

    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(JwtController.class);

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestParam String refreshToken) {
        // 리프레시 토큰 검증
        if (!jwtProvider.validateToken("BEARER " + refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
        }

        // DB에서 리프레시 토큰 확인
        User user = userRepository.findByRefreshToken(refreshToken).orElseThrow(() ->
                new BadCredentialsException("Invalid refresh token"));

        // 새로운 액세스 토큰 생성
        String newAccessToken = jwtProvider.createToken(user.getEmail(), user.getRoles(), user.getName(), user.getNickname());
        logger.info("newAccessToken: " + newAccessToken);

        return ResponseEntity.ok(newAccessToken);
    }

}
