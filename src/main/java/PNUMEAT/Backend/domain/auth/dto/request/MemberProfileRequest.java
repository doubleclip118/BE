package PNUMEAT.Backend.domain.auth.dto.request;

import jakarta.validation.constraints.NotNull;

public record MemberProfileRequest(
        @NotNull
        String memberName,
        @NotNull
        String memberExplain
) {
}
