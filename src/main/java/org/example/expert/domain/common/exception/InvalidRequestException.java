package org.example.expert.domain.common.exception;

import lombok.Getter;

@Getter
public class InvalidRequestException extends RuntimeException {
    private final String message;

    public InvalidRequestException(CommonErrorCode errorCode) {
        this.message = errorCode.getMessage();
    }
}
