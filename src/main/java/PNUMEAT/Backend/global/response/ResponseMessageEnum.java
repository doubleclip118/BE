package PNUMEAT.Backend.global.response;

import lombok.Getter;

@Getter
public enum ResponseMessageEnum {
    // TEAM
    TEAM_CREATED_SUCCESS("팀이 성공적으로 생성되었습니다.", 201);


    private final String message;
    private final int statusCode;

    ResponseMessageEnum(String message, int statusCode) {
        this.message = message;
        this.statusCode = statusCode;
    }
}
