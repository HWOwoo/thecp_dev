package com.thecp.dev.admin.controller;

import com.thecp.dev.user.dto.UserDto;
import com.thecp.dev.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    @GetMapping("/admin/info")
    public ResponseEntity<UserDto.SignResponse> getUserForAdmin(@RequestParam String email) throws Exception {
        return new ResponseEntity<>( userService.getUser(email), HttpStatus.OK);
    }

}
