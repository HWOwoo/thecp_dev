package com.thecp.dev.user.entity;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Builder
@Table(name = "USERS")
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false, unique = true)
    private String email;

    @Column(length = 300, nullable = false)
    private String password;

    @Column(length = 50, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING) // JPA로 데이터베이스를 저장할 때 Enum 값을 어떤 형태로 저장할지 결정합니다.
    private Role socialRole;

    @Enumerated(EnumType.STRING)
    private SocialType socialType;

    private String socialId;

    private String refreshToken;

    @Column(length = 100, nullable = false, unique = true)
    private String nickname;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @Builder.Default
    private List<Authority> roles = new ArrayList<>();

    private String imageUrl;

    public void setRoles(List<Authority> role) {
        this.roles = role;
        role.forEach(o -> o.setMember(this));
    }

    // 유저 권한 설정 메소드
    public void authorizeUser() {
        this.socialRole = Role.USER;
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

}
