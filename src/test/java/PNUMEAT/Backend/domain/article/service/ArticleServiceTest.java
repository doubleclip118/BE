package PNUMEAT.Backend.domain.article.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

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
import PNUMEAT.Backend.global.images.ImageService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private ArticleImageRepository articleImageRepository;

    @Mock
    private ImageService imageService;

    @InjectMocks
    private ArticleService articleService;

    @Test
    @DisplayName("게시글 저장 시 이미지가 함께 저장된다.")
    void save_ShouldSaveArticleWithImage() {
        // Given
        Member member = new Member( "username", "email@example.com", "ROLE_USER");
        Team team = new Team( "Team Name", Topic.CODINGTEST, "Description", 10, "password", member);

        ArticleRequest request = new ArticleRequest(1L, ArticleCategory.NOMAL, "Title", "Body");
        MultipartFile image = new MockMultipartFile("image", "image.jpg", "image/jpeg", "test-image".getBytes());

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
        when(imageService.articleImageUpload(image)).thenReturn("uploaded-image-url");

        // When
        articleService.save(1L, request, image);

        // Then
        verify(articleRepository, times(1)).save(any(Article.class));
        verify(articleImageRepository, times(1)).save(any(ArticleImage.class));
        verify(imageService, times(1)).articleImageUpload(image);
    }


    @Test
    @DisplayName("멤버의 게시글 목록을 이미지와 함께 반환한다.")
    void getMyArticles_ShouldReturnArticlesWithImages() {
        // Given
        ArticleImage articleImage = new ArticleImage(1L, "image-url", null);
        Article article = Article.builder()
            .articleId(1L)
            .articleTitle("Title")
            .articleBody("Body")
            .articleCategory(ArticleCategory.NOMAL)
            .images(List.of(articleImage))
            .build();

        when(articleRepository.findByMemberIdWithImages(1L)).thenReturn(List.of(article));

        // When
        List<Article> result = articleService.getMyArticles(1L);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getArticleTitle()).isEqualTo("Title");
        assertThat(result.get(0).getImages().get(0).getImageUrl()).isEqualTo("image-url");
    }
}
