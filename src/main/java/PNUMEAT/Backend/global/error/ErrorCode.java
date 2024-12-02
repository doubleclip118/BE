package PNUMEAT.Backend.global.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    //AUTH
    UNAUTHORIZED_USER_ERROR(HttpStatus.UNAUTHORIZED, "인증 되지 않은 사용자 입니다."),
    USER_NOT_FOUND_ERROR(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다. 유저를 찾을 수 없습니다."),
    TOKEN_EXPIRED_ERROR(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다."),
    TOKEN_MALFORMED_ERROR(HttpStatus.UNAUTHORIZED, "올바른 Token 형식이 아닙니다."),
    TOKEN_UNSUPPORTED_ERROR(HttpStatus.UNAUTHORIZED, "지원하지 않는 Token 입니다."),
    TOKEN_SIGNATURE_ERROR(HttpStatus.UNAUTHORIZED, "Token 의 서명이 유효하지 않습니다."),
    TOKEN_ERROR(HttpStatus.UNAUTHORIZED, "잘못된 Token 입니다."),

    //ARTICLE
    ARTICLE_FORBIDDEN_ERROR(HttpStatus.FORBIDDEN, "게시글 권한이 없습니다."),

    //TEAM
    TOPIC_INVALID_ERROR(HttpStatus.BAD_REQUEST, "주제가 올바른 형식이 아닙니다."),

    // ARTICLE
    ARTICLE_NOT_FOUND_ERROR(HttpStatus.NOT_FOUND, "존재 하지 않는 게시글 입니다."),

    //IMAGE
    IMAGE_FILE_UPLOAD_ERROR(HttpStatus.BAD_REQUEST,"이미지 파일 업로드 에러발생."),
    IMAGE_FILE_DELETE_ERROR(HttpStatus.BAD_REQUEST,"이미지 파일 삭제 에러발생."),
    S3_NETWORK_ERROR(HttpStatus.BAD_REQUEST,"S3 연결 에러 발생");

    private final HttpStatus status;

    private final String message;
}
