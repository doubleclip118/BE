package PNUMEAT.Backend.domain.auth.dto.request;

import jakarta.validation.constraints.NotNull;

public record MemberProfileCreateRequest(
        @NotNull
        String memberName,
        @NotNull
        String memberExplain
) {
}
