package com.est.tablelink.global.error;

import lombok.Getter;

/**
 * [공통 코드] API 통신에 대한 '에러 코드'를 Enum 형태로 관리를 한다.<br/>
 * Global Error CodeList : 전역으로 발생하는 에러코드를 관리한다.<br/>
 * Custom Error CodeList : 업무 페이지에서 발생하는 에러코드를 관리한다.<br/>
 * Error Code Constructor : 에러코드를 직접적으로 사용하기 위한 생성자를 구성한다.<br/>
 *
 * @author jojunho
 */

@Getter
public enum ErrorCode {

    /**
     * ******************************* Global Error CodeList ***************************************
     * <br/>HTTP Status Code<br/>
     * 400 : Bad Request<br/>
     * 401 : Unauthorized<br/>
     * 403 : Forbidden<br/>
     * 404 : Not Found<br/>
     * 500 : Internal Server Error
     * *********************************************************************************************
     */

    // 잘못된 요청
    BAD_REQUEST_ERROR(400, "G001", "잘못된 요청입니다. 요청 데이터를 확인하세요."),

    // 요청 본문 누락
    REQUEST_BODY_MISSING_ERROR(400, "G002", "요청 본문이 없습니다."),

    // 유효하지 않은 데이터 타입
    INVALID_TYPE_VALUE(400, "G003", "유효하지 않은 데이터 타입입니다."),

    // 요청 매개변수 누락
    MISSING_REQUEST_PARAMETER_ERROR(400, "G004", "요청 매개변수가 누락되었습니다."),

    // 입력/출력 오류
    IO_ERROR(400, "G005", "입출력 오류가 발생했습니다."),

    // JSON 파싱 실패
    JSON_PARSE_ERROR(400, "G006", "JSON 데이터 파싱에 실패했습니다."),

    // Jackson 데이터 처리 실패
    JACKSON_PROCESS_ERROR(400, "G007", "데이터 처리 중 오류가 발생했습니다."),

    // 권한이 없음
    FORBIDDEN_ERROR(403, "G008", "권한이 없습니다."),

    // 요청한 리소스를 찾을 수 없음
    NOT_FOUND_ERROR(404, "G009", "요청한 리소스를 찾을 수 없습니다."),

    // NULL 포인터 예외
    NULL_POINT_ERROR(500, "G010", "내부 오류가 발생했습니다."),

    // 유효하지 않은 요청 데이터
    NOT_VALID_ERROR(400, "G011", "요청 데이터가 유효하지 않습니다."),

    // 헤더 누락
    NOT_VALID_HEADER_ERROR(400, "G012", "헤더에 필수 데이터가 없습니다."),

    // 인증 실패
    UNAUTHORIZED_ERROR(401, "G013", "인증이 실패했습니다. 사용자명 또는 비밀번호를 확인하세요."),

    // 중복된 이메일 (충돌)
    EMAIL_ALREADY_EXISTS(409, "C001", "이메일이 이미 존재합니다."),

    // 서버 오류
    INTERNAL_SERVER_ERROR(500, "G999", "서버 내부 오류가 발생했습니다."),


    /**
     * ******************************* Custom Error CodeList ***************************************
     */
    // Transaction 관련 오류들
    INSERT_ERROR(500, "9999", "데이터 삽입 중 오류가 발생했습니다."),
    UPDATE_ERROR(500, "9999", "데이터 업데이트 중 오류가 발생했습니다."),
    DELETE_ERROR(500, "9999", "데이터 삭제 중 오류가 발생했습니다."),

    ; // End

    /**
     * ******************************* Error Code Constructor ***************************************
     */
    private final int status; // HTTP 상태 코드
    private final String divisionCode; // 에러 구분 코드
    private final String message; // 에러 메시지

    // 생성자 구성
    ErrorCode(final int status, final String divisionCode, final String message) {
        this.status = status;
        this.divisionCode = divisionCode;
        this.message = message;
    }
}