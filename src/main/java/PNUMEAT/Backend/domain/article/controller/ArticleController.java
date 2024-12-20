package PNUMEAT.Backend.domain.article.controller;

import static PNUMEAT.Backend.global.response.ResponseMessageEnum.ARTICLE_CREATE_SUCCESS;
import static PNUMEAT.Backend.global.response.ResponseMessageEnum.ARTICLE_DELETE_SUCCESS;
import static PNUMEAT.Backend.global.response.ResponseMessageEnum.ARTICLE_PUT_SUCCESS;
import static PNUMEAT.Backend.global.response.ResponseMessageEnum.TEAM_CREATED_SUCCESS;

import PNUMEAT.Backend.domain.article.dto.request.ArticleRequest;
import PNUMEAT.Backend.domain.article.dto.response.ArticleResponse;
import PNUMEAT.Backend.domain.article.entity.Article;
import PNUMEAT.Backend.domain.article.service.ArticleService;
import PNUMEAT.Backend.domain.auth.entity.Member;
import PNUMEAT.Backend.global.error.dto.response.ApiResponse;
import PNUMEAT.Backend.global.security.annotation.LoginMember;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/articles")

public class ArticleController {
    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }
    // 게시글 저장
    @PostMapping
    public ResponseEntity<?> createArticle(
        @LoginMember Member member,
        @ModelAttribute @Valid ArticleRequest articleRequest,
        @RequestPart(value = "image", required = false) MultipartFile image) {

        articleService.isMemberInTeam(member.getId(),articleRequest.teamId());

        articleService.save(member.getId(), articleRequest, image);

        return ResponseEntity.status(ARTICLE_CREATE_SUCCESS.getStatusCode())
            .contentType(MediaType.APPLICATION_JSON)
            .body(ApiResponse.createResponseWithMessage(ARTICLE_CREATE_SUCCESS.getMessage()));
    }

    // 내 게시글 조회
    @GetMapping()
    public ResponseEntity<?> getMyArticles(@LoginMember Member member) {
        List<Article> myArticles = articleService.getMyArticles(member.getId());

        List<ArticleResponse> responses = myArticles.stream()
            .map(ArticleResponse::of)
            .toList();

        return ResponseEntity.status(HttpStatus.OK)
            .contentType(MediaType.APPLICATION_JSON)
            .body(ApiResponse.successResponse(responses));
    }

    // 팀 기준 게시글 조회
    @GetMapping("/team/{teamId}")
    public ResponseEntity<?> getArticlesByTeam(
        @PathVariable Long teamId,
        @LoginMember Member member) {

        articleService.isMemberInTeam(member.getId(), teamId);

        List<Article> articles = articleService.getArticlesByTeam(teamId);
        List<ArticleResponse> responses = articles.stream()
            .map(ArticleResponse::of)
            .toList();

        return ResponseEntity.status(HttpStatus.OK)
            .contentType(MediaType.APPLICATION_JSON)
            .body(ApiResponse.successResponse(responses));
    }

    // 특정 게시글 조회 이건 쓸일 없을것같음
    @GetMapping("/{id}")
    public ResponseEntity<?> getArticleById(
        @PathVariable Long id) {

        Article article = articleService.getArticleById(id);

        ArticleResponse response = ArticleResponse.of(article);

        return ResponseEntity.status(HttpStatus.OK)
            .contentType(MediaType.APPLICATION_JSON)
            .body(ApiResponse.successResponse(response));
    }

    // 게시글 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteArticle(
        @PathVariable Long id,
        @LoginMember Member member) {

        articleService.deleteArticle(id, member.getId());

        return ResponseEntity.status(ARTICLE_DELETE_SUCCESS.getStatusCode())
            .contentType(MediaType.APPLICATION_JSON)
            .body(ApiResponse.createResponseWithMessage(ARTICLE_DELETE_SUCCESS.getMessage()));
    }

    // 게시글 수정
    @PutMapping("/{id}")
    public ResponseEntity<?> updateArticle(
        @PathVariable Long id,
        @ModelAttribute ArticleRequest articleRequest,
        @RequestPart(value = "image", required = false) MultipartFile image,
        @LoginMember Member member) {

        articleService.updateArticle(id, articleRequest, image, member.getId());

        return ResponseEntity.status(ARTICLE_PUT_SUCCESS.getStatusCode())
            .contentType(MediaType.APPLICATION_JSON)
            .body(ApiResponse.createResponseWithMessage(ARTICLE_PUT_SUCCESS.getMessage()));
    }
    // 날짜기준 게시물 조회
    @GetMapping("/{teamId}/by-date")
    public ResponseEntity<?> getArticlesByTeamAndDate(
        @PathVariable Long teamId,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
        @LoginMember Member member,
        @PageableDefault(size = 10, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {

        // 팀 멤버 확인
        articleService.isMemberInTeam(member.getId(), teamId);

        // 페이지네이션된 결과 가져오기
        Page<Article> articlePage = articleService.getArticlesByTeamAndDate(teamId, date, pageable);

        // Article -> ArticleResponse로 매핑
        Page<ArticleResponse> responsePage = articlePage.map(ArticleResponse::of);

        // 응답 반환
        return ResponseEntity.status(HttpStatus.OK)
            .contentType(MediaType.APPLICATION_JSON)
            .body(ApiResponse.successResponse(responsePage));
    }

}
