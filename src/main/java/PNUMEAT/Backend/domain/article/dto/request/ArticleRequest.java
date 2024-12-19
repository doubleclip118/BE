package PNUMEAT.Backend.domain.article.dto.request;

import PNUMEAT.Backend.domain.article.enums.ArticleCategory;
import jakarta.validation.constraints.NotBlank;

public record ArticleRequest(
    @NotBlank(message = "팀 아이디는 필수요소입니다.")
    Long teamId,
    @NotBlank(message = "게시글 카테고리는 필수요소입니다")
    ArticleCategory articleCategory,
    @NotBlank(message = "게시글 제목은 필수요소입니다")
    String articleTitle,
    @NotBlank(message = "게시글 내용은 필수요소입니다")
    String articleBody
) {

}
