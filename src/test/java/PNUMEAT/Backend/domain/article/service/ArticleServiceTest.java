package PNUMEAT.Backend.domain.article.service;

import PNUMEAT.Backend.domain.article.dto.request.ArticleRequest;
import PNUMEAT.Backend.domain.article.entity.Article;
import PNUMEAT.Backend.domain.article.entity.ArticleImage;
import PNUMEAT.Backend.domain.article.enums.ArticleCategory;
import PNUMEAT.Backend.domain.article.repository.ArticleImageRepository;
import PNUMEAT.Backend.domain.article.repository.ArticleRepository;
import PNUMEAT.Backend.domain.auth.entity.Member;
import PNUMEAT.Backend.domain.auth.repository.MemberRepository;
import PNUMEAT.Backend.domain.team.entity.Team;
import PNUMEAT.Backend.domain.team.enums.Topic;
import PNUMEAT.Backend.domain.team.repository.TeamRepository;
import PNUMEAT.Backend.domain.teamMember.repository.TeamMemberRepository;
import PNUMEAT.Backend.global.error.Member.MemberNotFoundException;
import PNUMEAT.Backend.global.error.articles.MemberNotInTeamException;
import PNUMEAT.Backend.global.error.articles.UnauthorizedActionException;
import PNUMEAT.Backend.global.images.ImageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
class ArticleServiceTest {

    @Autowired
    private ArticleService articleService;

    @MockBean
    private ArticleRepository articleRepository;

    @MockBean
    private MemberRepository memberRepository;

    @MockBean
    private TeamRepository teamRepository;

    @MockBean
    private TeamMemberRepository teamMemberRepository;

    @MockBean
    private ArticleImageRepository articleImageRepository;

    @MockBean
    private ImageService imageService;

    private Member testMember;
    private Team testTeam;
    private Article testArticle;

    @BeforeEach
    void setup() {
        testMember = new Member("test@example.com", "testuser", "USER");

        testTeam = new Team("Test Team", Topic.STUDY, "This is a test team.", 10, "password", testMember);

        testArticle = new Article(
            testTeam,
            testMember,
            "Test Title",
            "Test Body",
            null,
            new ArrayList<>()
        );

        when(memberRepository.findById(1L)).thenReturn(Optional.of(testMember));
        when(memberRepository.findById(99L)).thenReturn(Optional.empty());

        when(teamRepository.findById(1L)).thenReturn(Optional.of(testTeam));
        when(teamRepository.findById(99L)).thenReturn(Optional.empty());

        when(teamMemberRepository.existsByTeamTeamIdAndMemberId(1L, 1L)).thenReturn(true);
        when(teamMemberRepository.existsByTeamTeamIdAndMemberId(1L, 99L)).thenReturn(false);

        when(articleRepository.findByIdWithImages(1L)).thenReturn(Optional.of(testArticle));
        when(articleRepository.findById(1L)).thenReturn(Optional.of(testArticle));
        when(articleRepository.findById(99L)).thenReturn(Optional.empty());

        when(imageService.articleImageUpload(any())).thenReturn("http://image-url.com");
    }


    @Test
    void save_withValidData_shouldSaveArticle() {
        ArticleRequest articleRequest = new ArticleRequest(1L, ArticleCategory.NOMAL, "Test Title", "Test Body");
        MockMultipartFile mockImage = new MockMultipartFile(
            "image", "image.jpg", "image/jpeg", "mock-image-content".getBytes()
        );

        articleService.save(1L, articleRequest, mockImage);

        Mockito.verify(articleRepository).save(Mockito.any(Article.class));
        Mockito.verify(articleImageRepository).save(Mockito.any(ArticleImage.class));
    }

    @Test
    void save_withNonExistingMember_shouldThrowException() {
        when(memberRepository.findById(99L)).thenReturn(Optional.empty());
        ArticleRequest articleRequest = new ArticleRequest(1L, ArticleCategory.NOMAL, "Test Title", "Test Body");
        MockMultipartFile mockImage = new MockMultipartFile(
            "image", "image.jpg", "image/jpeg", "mock-image-content".getBytes()
        );

        assertThrows(MemberNotFoundException.class, () -> articleService.save(99L, articleRequest, mockImage));
    }

    @Test
    void getMyArticles_shouldReturnArticles() {
        when(articleRepository.findByMemberIdWithImages(1L)).thenReturn(List.of(testArticle));

        List<Article> articles = articleService.getMyArticles(1L);

        assertThat(articles).hasSize(1);
        assertThat(articles.get(0).getArticleTitle()).isEqualTo("Test Title");
    }

    @Test
    void isMemberInTeam_withNonExistingMember_shouldThrowException() {
        when(teamMemberRepository.existsByTeamTeamIdAndMemberId(1L, 99L)).thenReturn(false);

        assertThrows(MemberNotInTeamException.class, () -> articleService.isMemberInTeam(99L, 1L));
    }
}
