package com.thecp.dev.user.dto;

import com.thecp.dev.user.entity.Authority;
import com.thecp.dev.user.entity.Role;
import com.thecp.dev.user.entity.SocialType;
import com.thecp.dev.user.entity.User;
import lombok.*;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.List;


public class UserDto {

    @Getter @Setter
    public static class SignRequest {

        private Long id;

        private String password;

        private String nickname;

        private String name;

        private String email;

        private SocialType socialType;

        private Role socialRole;
    }

    @Getter
    @Builder @AllArgsConstructor
    @NoArgsConstructor
    public static class SignResponse {

        private Long id;

        private String nickname;

        private String name;

        private String email;

        private List<Authority> roles = new ArrayList<>();

        private SocialType socialType;

        private Role socialRole;

        private String token;

        private String refreshToken;

        public SignResponse(User user) {
            this.id = user.getId();
            this.nickname = user.getNickname();
            this.name = user.getName();
            this.email = user.getEmail();
            this.roles = user.getRoles();
            this.socialRole = user.getSocialRole();
            this.refreshToken = user.getRefreshToken();
        }
    }

    @NoArgsConstructor // 기본 생성자 자동 추가
    @Getter
    public static class UserSignUpDto {

        private String email;
        private String password;
        private String nickname;
        private String name;
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
