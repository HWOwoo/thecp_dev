package com.thecp.dev.user.controller;

import com.thecp.dev.user.dto.UserDto;
import com.thecp.dev.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api") //
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping(value = "/login")
    public ResponseEntity<UserDto.SignResponse> signin(@RequestBody UserDto.SignRequest request) throws Exception {
        return new ResponseEntity<>(userService.login(request), HttpStatus.OK);
    }

    @PostMapping(value = "/register")
    public ResponseEntity<Boolean> signup(@RequestBody UserDto.SignRequest request) throws Exception {
        return new ResponseEntity<>(userService.register(request), HttpStatus.OK);
    }

    @GetMapping("/user/info")
    public ResponseEntity<UserDto.SignResponse> getUser(@RequestParam String email) throws Exception {
        return new ResponseEntity<>( userService.getUser(email), HttpStatus.OK);
    }

}
