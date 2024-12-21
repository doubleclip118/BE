package PNUMEAT.Backend.domain.article.service;

import PNUMEAT.Backend.domain.article.dto.request.ArticleRequest;
import PNUMEAT.Backend.domain.article.entity.Article;
import PNUMEAT.Backend.domain.article.enums.ArticleCategory;
import PNUMEAT.Backend.domain.article.repository.ArticleRepository;
import PNUMEAT.Backend.domain.auth.entity.Member;
import PNUMEAT.Backend.domain.auth.repository.MemberRepository;
import PNUMEAT.Backend.domain.team.entity.Team;
import PNUMEAT.Backend.domain.team.enums.Topic;
import PNUMEAT.Backend.domain.team.repository.TeamRepository;
import PNUMEAT.Backend.global.error.Member.MemberNotFoundException;
import PNUMEAT.Backend.global.error.articles.UnauthorizedActionException;
import PNUMEAT.Backend.global.images.ImageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
class ArticleServiceTest {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TeamRepository teamRepository;

    @MockBean
    private ImageService imageService;

    private Member testMember;
    private Team testTeam;
    private Article testArticle;

    @BeforeEach
    void setup() {
        testMember = memberRepository.save(new Member("test@example.com", "testuser", "USER"));
        testTeam = teamRepository.save(
            new Team("Test Team", Topic.STUDY, "This is a test team.", 10, "password", testMember)
        );
        testArticle = articleRepository.save(
            new Article(testTeam, testMember, "Test Title", "Test Body", ArticleCategory.NOMAL, new ArrayList<>())
        );

        when(imageService.articleImageUpload(new MockMultipartFile(
            "image", "image.jpg", "image/jpeg", "mock-image-content".getBytes())))
            .thenReturn("http://mock-s3-url.com/image.jpg");
    }

    @Test
    @DisplayName("게시글 저장 - 유효한 데이터로 게시글 저장 성공")
    void saveArticle_withValidData_shouldSaveSuccessfully() {
        ArticleRequest articleRequest = new ArticleRequest(
            testTeam.getTeamId(), ArticleCategory.NOMAL, "New Test Title", "New Test Body"
        );
        MockMultipartFile mockImage = new MockMultipartFile(
            "image", "image.jpg", "image/jpeg", "mock-image-content".getBytes()
        );

        articleService.save(testMember.getId(), articleRequest, mockImage);

        List<Article> articles = articleRepository.findAll();
        assertThat(articles).hasSize(2);
        Article savedArticle = articles.get(1);
        assertThat(savedArticle.getArticleTitle()).isEqualTo("New Test Title");
        assertThat(savedArticle.getArticleBody()).isEqualTo("New Test Body");
    }

    @Test
    @DisplayName("게시글 저장 - 존재하지 않는 회원으로 요청 시 예외 발생")
    void saveArticle_withNonExistingMember_shouldThrowException() {
        ArticleRequest articleRequest = new ArticleRequest(
            testTeam.getTeamId(), ArticleCategory.NOMAL, "New Test Title", "New Test Body"
        );
        MockMultipartFile mockImage = new MockMultipartFile(
            "image", "image.jpg", "image/jpeg", "mock-image-content".getBytes()
        );

        assertThrows(MemberNotFoundException.class, () ->
            articleService.save(999L, articleRequest, mockImage)
        );
    }

    @Test
    @DisplayName("내 게시글 조회 - 성공적으로 게시글 반환")
    void getMyArticles_shouldReturnArticlesSuccessfully() {
        List<Article> articles = articleService.getMyArticles(testMember.getId());

        assertThat(articles).hasSize(1);
        Article retrievedArticle = articles.get(0);
        assertThat(retrievedArticle.getArticleTitle()).isEqualTo("Test Title");
        assertThat(retrievedArticle.getArticleBody()).isEqualTo("Test Body");
    }

    @Test
    @DisplayName("게시물 삭제 - 게시물 소유자가 정상적으로 삭제")
    void deleteArticle_byOwner_shouldSucceed() {
        articleService.deleteArticle(testArticle.getArticleId(), testMember.getId());

        List<Article> articles = articleRepository.findAll();
        assertThat(articles).isEmpty();
    }

    @Test
    @DisplayName("게시물 수정 - 게시물 소유자가 성공적으로 수정")
    void updateArticle_byOwner_shouldSucceed() {
        ArticleRequest updateRequest = new ArticleRequest(
            testArticle.getArticleId(), ArticleCategory.ANOUNCE, "Updated Title", "Updated Body"
        );
        MockMultipartFile mockImage = new MockMultipartFile(
            "image", "updated.jpg", "image/jpeg", "updated-content".getBytes()
        );

        articleService.updateArticle(testArticle.getArticleId(), updateRequest, mockImage, testMember.getId());

        Article updatedArticle = articleRepository.findById(testArticle.getArticleId()).orElseThrow();
        assertThat(updatedArticle.getArticleTitle()).isEqualTo("Updated Title");
        assertThat(updatedArticle.getArticleBody()).isEqualTo("Updated Body");
        assertThat(updatedArticle.getArticleCategory()).isEqualTo(ArticleCategory.ANOUNCE);
    }

    @Test
    @DisplayName("게시물 수정 - 게시물 소유자가 아닌 경우 예외 발생")
    void updateArticle_byNonOwner_shouldThrowException() {
        Member anotherMember = memberRepository.save(new Member("another@example.com", "anotheruser", "USER"));
        ArticleRequest updateRequest = new ArticleRequest(
            testArticle.getArticleId(), ArticleCategory.ANOUNCE, "Updated Title", "Updated Body"
        );
        MockMultipartFile mockImage = new MockMultipartFile(
            "image", "updated.jpg", "image/jpeg", "updated-content".getBytes()
        );

        assertThrows(UnauthorizedActionException.class, () ->
            articleService.updateArticle(testArticle.getArticleId(), updateRequest, mockImage, anotherMember.getId())
        );
    }
}
