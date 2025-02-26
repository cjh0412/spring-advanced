package org.example.expert.domain.common.exception;

import lombok.Getter;

@Getter
public class ServerException extends RuntimeException {
    private final String message;

    public ServerException(CommonErrorCode errorCode) {
        this.message = errorCode.getMessage();
    }

    // 문자열 메세지를 받은 생성자
    public ServerException(String message){
        super(message);
        this.message = null;
    }
}
