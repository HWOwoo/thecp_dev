package com.thecp.dev.user.controller;

import com.thecp.dev.jwt.dto.TokenInfo;
import com.thecp.dev.user.dto.UserDto;
import com.thecp.dev.user.service.CustomUserDetailsService;
import com.thecp.dev.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/signup")
    public ResponseEntity<Map<String, String>> signUp(@RequestBody UserDto.UserSignUpDto userSignUpDto) {
        try {
            userService.signUp(userSignUpDto);
            Map<String, String> response = new HashMap<>();

            response.put("message", "회원가입에 성공하였습니다.");
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            Map<String, String> errorResponse = new HashMap<>();

            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/login")
    public TokenInfo login(@RequestBody UserDto.UserLoginDto userLoginDto) {
        String userEmail = userLoginDto.getEmail();
        String password = userLoginDto.getPassword();
        TokenInfo tokenInfo = userService.login(userEmail, password);
        return tokenInfo;
    }

}
