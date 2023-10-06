package com.thecp.dev.oauth2.dto;

import com.thecp.dev.oauth2.entity.OAuth2UserInfo;
import com.thecp.dev.user.entity.Role;
import com.thecp.dev.user.entity.SocialType;
import com.thecp.dev.user.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;
import java.util.UUID;

/**
 * 각 소셜에서 받는 데이터 컨트롤
 */
@Getter
public class OAuthAttributes {

    private String nameAttributeKey; // OAuth2 로그인 진행 시 키가 되는 필드값 (PK)
    private OAuth2UserInfo oauth2UserInfo; // OAuth2 로그인 진행시 타입별 유저 정보

    @Builder
    public OAuthAttributes(String nameAttributeKey, OAuth2UserInfo oauth2UserInfo) {
        this.nameAttributeKey = nameAttributeKey;
        this.oauth2UserInfo = oauth2UserInfo;
    }

    /**
     * OAuth2User 에서 반환하는 사용자 정보는 Map 이기 때문에 값 하나하나를 변환해야만 한다.
     * @param socialType
     * @param userNameAttributeName
     * @param attributes
     * @return
     */
    public static OAuthAttributes of(SocialType socialType,
                                     String userNameAttributeName, Map<String, Object> attributes) {
        if (socialType == SocialType.NAVER) {
            return ofNaver(userNameAttributeName, attributes);
        }
        if (socialType == SocialType.KAKAO) {
            return ofKakao(userNameAttributeName, attributes);
        }
        return ofGoogle(userNameAttributeName, attributes);
    }

    private static OAuthAttributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .nameAttributeKey(userNameAttributeName)
                .oauth2UserInfo(new KakaoOAuth2UserInfo(attributes))
                .build();
    }

    public static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .nameAttributeKey(userNameAttributeName)
                .oauth2UserInfo(new GoogleOAuth2UserInfo(attributes))
                .build();
    }

    public static OAuthAttributes ofNaver(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .nameAttributeKey(userNameAttributeName)
                .oauth2UserInfo(new NaverOAuth2UserInfo(attributes))
                .build();
    }

    /**
     * of메소드로 OAuthAttributes 객체가 생성되어, 유저 정보들이 담긴 OAuth2UserInfo가 소셜 타입별로 주입된 상태
     * OAuth2UserInfo에서 socialId(식별값), nickname, imageUrl을 가져와서 build
     * email에는 UUID로 중복 없는 랜덤 값 생성
     * role은 GUEST로 설정
     */
    public User toEntity(SocialType socialType, OAuth2UserInfo oauth2UserInfo) {
        return User.builder()
                .socialType(socialType)
                .socialId(oauth2UserInfo.getId())
                .email(UUID.randomUUID() + "@socialUser.com")
                .nickname(oauth2UserInfo.getNickName())
                .imageUrl(oauth2UserInfo.getImageUrl())
                .socialRole(Role.GUEST)
                .build();
    }

}
