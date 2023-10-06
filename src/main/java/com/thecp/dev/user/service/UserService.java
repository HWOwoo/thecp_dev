package com.thecp.dev.user.service;

import com.thecp.dev.jwt.JwtProvider;
import com.thecp.dev.user.dto.UserDto;
import com.thecp.dev.user.entity.Authority;
import com.thecp.dev.user.entity.Role;
import com.thecp.dev.user.entity.SocialType;
import com.thecp.dev.user.entity.User;
import com.thecp.dev.user.repositroy.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collections;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor // final이 선언된 모든 필드를 인자값으로 하는 생성자를 생성
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public UserDto.SignResponse login(UserDto.SignRequest request) throws Exception {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() ->
                new BadCredentialsException("잘못된 계정정보입니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("잘못된 비밀번호입니다.");
        }

        String token = jwtProvider.createRefreshToken(); // 리프레시 토큰 생성

        user.updateRefreshToken(token); // 토큰 업데이트

        return UserDto.SignResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .name(user.getName())
                .socialRole(user.getSocialRole())
                .roles(user.getRoles())
                .token(jwtProvider.createToken(user.getEmail(), user.getRoles(), user.getName(), user.getNickname())) //
                .refreshToken(token) // 리프레시 토큰
                .build();
    }

    public boolean register(UserDto.SignRequest request) throws Exception {
        try {
            User user = User.builder()
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .name(request.getName())
                    .nickname(request.getNickname())
                    .socialRole(Role.USER) // 일반 회원가입
                    .build();

            user.setRoles(Collections.singletonList(Authority.builder().name("ROLE_USER").build()));

            userRepository.save(user);
        } catch (Exception e) {
            throw new Exception("회원가입 실패");
        }

        return true;
    }

    public UserDto.SignResponse getUser(String email) throws Exception {
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new BadCredentialsException("잘못된 계정정보입니다."));

        return new UserDto.SignResponse(user);
    }
}
