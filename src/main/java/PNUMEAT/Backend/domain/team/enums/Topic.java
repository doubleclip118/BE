package PNUMEAT.Backend.domain.team.enums;

import PNUMEAT.Backend.global.error.ErrorCode;
import PNUMEAT.Backend.global.error.Team24Exception;

import java.util.Arrays;

import static PNUMEAT.Backend.global.error.ErrorCode.TOPIC_INVALID_ERROR;

public enum Topic {
    CODINGTEST(1, "코딩테스트"),
    STUDY(2, "스터디");

    private final int code;
    private final String name;

    Topic(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static Topic fromName(String name){
        return Arrays.stream(Topic.values())
                .filter(topic -> topic.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new Team24Exception(TOPIC_INVALID_ERROR));
    }
}
