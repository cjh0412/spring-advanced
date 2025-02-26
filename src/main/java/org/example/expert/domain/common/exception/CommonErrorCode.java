package org.example.expert.domain.common.exception;

import lombok.Getter;

@Getter
public enum CommonErrorCode {

    // InvalidRequestException
    UNREGISTERED_USER("가입되지 않은 유저입니다."),
    TODO_NOT_FOUND("Todo not found"),
    MANAGER_NOT_ALLOWED("담당자를 등록하려고 하는 유저가 일정을 만든 유저가 유효하지 않습니다."),
    MANAGER_NOT_FOUND("등록하려고 하는 담당자 유저가 존재하지 않습니다."),
    TODO_CREATOR_CANNOT_BE_MANAGER("일정 작성자는 본인을 담당자로 등록할 수 없습니다."),
    USER_NOT_FOUND("User not found"),
    INVALID_TODO_CREATOR("해당 일정을 만든 유저가 유효하지 않습니다."),
    MANAGER_NOT_ASSIGNED("해당 일정에 등록된 담당자가 아닙니다."),
    WRONG_PASSWORD("잘못된 비밀번호입니다."),
    PASSWORD_CANNOT_BE_SAME_AS_OLD("새 비밀번호는 기존 비밀번호와 같을 수 없습니다."),
    INVALID_USER_ROLE("유효하지 않은 UerRole"),

    // ServerException
    FAILED_TO_FETCH_WEATHER_DATA("날씨 데이터를 가져오는데 실패했습니다."),
    WEATHER_DATA_NOT_FOUND("날씨 데이터가 없습니다."),
    TODAY_WEATHER_DATA_NOT_FOUND("오늘에 해당하는 날씨 데이터를 찾을 수 없습니다."),
    TOKEN_NOT_FOUND("Not Found Token"),
    ONLY_ADMIN_ALLOWED("관리자로 로그인 후 사용가능합니다."),

    // etc
    EMAIL_ALREADY_EXISTS("이미 존재하는 이메일입니다.")


    ;


    private final String message;

    CommonErrorCode(String message) {
        this.message = message;
    }
}
