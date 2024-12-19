package PNUMEAT.Backend.domain.team.dto.request;

import PNUMEAT.Backend.global.validation.annotation.NotNullOrBlank;
import jakarta.validation.constraints.Size;

public record TeamAnnouncementRequest(
        @NotNullOrBlank
        @Size(
                max = 255,
                message = "글자 수를 초과했습니다."
        )
        String teamAnnouncement) {
}
