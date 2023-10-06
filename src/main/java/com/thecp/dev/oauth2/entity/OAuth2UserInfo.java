package com.thecp.dev.oauth2.entity;

import java.util.Map;
import java.util.Objects;

public abstract class OAuth2UserInfo {

    protected Map<String, Object> attributes;

    public OAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    } // OAuth2User에서 반환하는 사용자 정보는 Map이기 때문에 값 하나하나를 변환해야만 한다.

    public abstract String getId();

    public abstract String getNickName();

    public abstract String getImageUrl();


}
