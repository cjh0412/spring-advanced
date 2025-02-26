package org.example.expert.domain.auth.exception;

import lombok.Getter;

@Getter
public class AuthException extends RuntimeException {
    private final String message;

    public AuthException(AuthErrorCode message) {
        this.message = message.getMessage();
    }
}
