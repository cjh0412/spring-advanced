package org.example.expert.domain.auth.exception;

import lombok.Getter;

@Getter
public enum AuthErrorCode {
    WRONG_PASSWORD("잘못된 비밀번호입니다."),
    AUTH_USER_WITH_ANNOTATION("@Auth와 AuthUser 타입은 함께 사용되어야 합니다."),


    // jwt
    JWT_TOKEN_REQUIRED("JWT 토큰이 필요합니다."),
    UNSUPPORTED_JWT_TOKEN( "지원되지 않는 JWT 토큰입니다."),
    INVALID_JWT_TOKEN( "유효하지 않은 JWT 토큰입니다."),
    EXPIRED_JWT_TOKEN( "만료된 JWT 토큰입니다."),
    INVALID_JWT_SIGNATURE("유효하지 않는 JWT 서명입니다."),
    WRONG_JWT_TOKEN("잘못된 JWT 토큰입니다.")

    ;



    private final String message;

    AuthErrorCode(String message) {
        this.message = message;
    }
}
