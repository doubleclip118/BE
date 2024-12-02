package PNUMEAT.Backend.domain.team.dto.request;

import PNUMEAT.Backend.global.validation.annotation.NotNullOrBlank;
import jakarta.validation.constraints.*;

public record TeamRequest(
        @NotNullOrBlank
        @Pattern(
                regexp = "^[가-힣a-zA-Z0-9\\s]*$",
                message = "한글, 영어, 숫자, 공백만 입력 가능합니다."
        )
        @Size(
                max = 10,
                message = "글자 수를 초과했습니다."
        )
        String teamName,

        @Pattern(
                regexp = "^[가-힣a-zA-Z0-9!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?`~ ]*$",
                message = "팀 설명은 한글, 영어, 숫자, 기본 특수문자만 사용가능합니다."
        )
        @Size(
                max = 50,
                message = "글자 수를 초과했습니다."
        )
        String teamExplain,

        @NotNullOrBlank
        String topic,

        @NotNull(message="null 값이 될 수 없습니다.")
        @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "2 ~ 20 사이의 숫자를 입력해주세요.")
        @Min(value = 2, message = "2 ~ 20 사이의 숫자를 입력해주세요.")
        @Max(value = 20, message = "2 ~ 20 사이의 숫자를 입력해주세요.")
        int memberLimit,

        @Pattern(
                regexp = "^[0-9]{4}$",
                message = "숫자 4자리를 입력해주세요."
        )
        String password
) {
}
