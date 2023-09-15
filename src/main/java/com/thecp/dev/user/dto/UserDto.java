package com.thecp.dev.user.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;


public class UserDto {

    @NoArgsConstructor // 기본 생성자 자동 추가
    @Getter
    public static class UserSignUpDto {

        private String email;
        private String password;
        private String nickname;
        private int age;
        private String city;

    }

    @NoArgsConstructor
    @Data // @Getter, @Setter, @RequiredArgsConstructor, @ToString, @EqualsAndHashCode을 한꺼번에 설정해주는 어노테이션
    public static class UserLoginDto {

        private String email;
        private String password;

    }

}
