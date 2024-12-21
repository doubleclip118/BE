package PNUMEAT.Backend.domain.article.repository;

import PNUMEAT.Backend.domain.article.entity.Article;
import PNUMEAT.Backend.domain.article.entity.ArticleImage;
import PNUMEAT.Backend.domain.article.enums.ArticleCategory;
import PNUMEAT.Backend.domain.auth.entity.Member;
import PNUMEAT.Backend.domain.auth.repository.MemberRepository;
import PNUMEAT.Backend.domain.team.entity.Team;
import PNUMEAT.Backend.domain.team.enums.Topic;
import PNUMEAT.Backend.domain.team.repository.TeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ArticleRepositoryTest {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TeamRepository teamRepository;

    private Member testMember;
    private Team testTeam;
    private Article testArticle;

    @BeforeEach
    void setup() {
        // Create and save a test Member
        testMember = memberRepository.save(new Member("test@example.com", "testuser", "USER"));

        // Create and save a test Team
        testTeam = teamRepository.save(
            new Team("Test Team", Topic.STUDY, "This is a test team.", 10, "password", testMember)
        );

        // Create and save a test Article
        testArticle = articleRepository.save(
            new Article(testTeam, testMember, "Test Title", "Test Body", ArticleCategory.NOMAL, new ArrayList<>())
        );

        // Add an ArticleImage to the Article
        ArticleImage testImage = new ArticleImage("http://test-image-url.com", testArticle);
        testArticle.addImage(testImage);
        articleRepository.save(testArticle);
    }

    @Test
    @DisplayName("게시글 ID로 게시글 조회")
    void findByArticleId_shouldReturnArticle() {
        Optional<Article> article = articleRepository.findByArticleId(testArticle.getArticleId());

        assertThat(article).isPresent();
        assertThat(article.get().getArticleTitle()).isEqualTo("Test Title");
    }

    @Test
    @DisplayName("회원 ID로 게시글 및 이미지 조회")
    void findByMemberIdWithImages_shouldReturnArticlesWithImages() {
        List<Article> articles = articleRepository.findByMemberIdWithImages(testMember.getId());

        assertThat(articles).hasSize(1);
        Article retrievedArticle = articles.get(0);
        assertThat(retrievedArticle.getArticleTitle()).isEqualTo("Test Title");
        assertThat(retrievedArticle.getImages()).hasSize(1);
        assertThat(retrievedArticle.getImages().get(0).getImageUrl()).isEqualTo("http://test-image-url.com");
    }

    @Test
    @DisplayName("팀 ID로 게시글 및 이미지 조회")
    void findByTeamTeamIdWithImages_shouldReturnArticlesWithImages() {
        List<Article> articles = articleRepository.findByTeamTeamIdWithImages(testTeam.getTeamId());

        assertThat(articles).hasSize(1);
        Article retrievedArticle = articles.get(0);
        assertThat(retrievedArticle.getArticleTitle()).isEqualTo("Test Title");
        assertThat(retrievedArticle.getImages()).hasSize(1);
        assertThat(retrievedArticle.getImages().get(0).getImageUrl()).isEqualTo("http://test-image-url.com");
    }

    @Test
    @DisplayName("게시글 ID로 게시글 및 이미지 조회")
    void findByIdWithImages_shouldReturnArticleWithImages() {
        Optional<Article> article = articleRepository.findByIdWithImages(testArticle.getArticleId());

        assertThat(article).isPresent();
        assertThat(article.get().getArticleTitle()).isEqualTo("Test Title");
        assertThat(article.get().getImages()).hasSize(1);
        assertThat(article.get().getImages().get(0).getImageUrl()).isEqualTo("http://test-image-url.com");
    }

    @Test
    @DisplayName("팀 ID와 날짜로 게시글 페이징 조회")
    void findByTeamIdAndDate_shouldReturnPagedArticles() {
        LocalDate today = LocalDate.now();
        Page<Article> articles = articleRepository.findByTeamIdAndDate(
            testTeam.getTeamId(), today, PageRequest.of(0, 10)
        );

        assertThat(articles.getContent()).hasSize(1);
        Article retrievedArticle = articles.getContent().get(0);
        assertThat(retrievedArticle.getArticleTitle()).isEqualTo("Test Title");
    }
}
