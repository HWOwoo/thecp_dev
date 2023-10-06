package com.thecp.dev.user.repositroy;

import com.thecp.dev.user.entity.SocialType;
import com.thecp.dev.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByNickname(String nickname);

    List<User> findAll();

    /**
     * 리프레시 토큰으로 유저를 찾기 위한 메소드
     * @param refreshToken
     * @return
     */
    Optional<User> findByRefreshToken(String refreshToken);

    /**
     * 소셜 로그인한 유저를 찾기 위한 메소드
     * @param socialType
     * @param socialId
     * @return
     */
    Optional<User> findBySocialTypeAndSocialId(SocialType socialType, String socialId);

}
