
package PNUMEAT.Backend.domain.article.service;


import PNUMEAT.Backend.domain.article.dto.request.ArticleRequest;
import PNUMEAT.Backend.domain.article.entity.Article;
import PNUMEAT.Backend.domain.article.entity.ArticleImage;
import PNUMEAT.Backend.domain.article.repository.ArticleImageRepository;
import PNUMEAT.Backend.domain.article.repository.ArticleRepository;
import PNUMEAT.Backend.domain.auth.entity.Member;
import PNUMEAT.Backend.domain.auth.repository.MemberRepository;
import PNUMEAT.Backend.domain.team.entity.Team;
import PNUMEAT.Backend.domain.team.repository.TeamRepository;
import PNUMEAT.Backend.global.error.Member.MemberNotFoundException;
import PNUMEAT.Backend.global.error.Team.TeamNotFoundException;
import PNUMEAT.Backend.global.error.articles.ArticleNotFoundException;
import PNUMEAT.Backend.global.images.ImageService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


@Service
@Slf4j
public class ArticleService {
    private final ImageService imageService;
    private final ArticleRepository articleRepository;
    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;
    private final ArticleImageRepository articleImageRepository;

    public ArticleService(ImageService imageService, ArticleRepository articleRepository,
        MemberRepository memberRepository, TeamRepository teamRepository,
        ArticleImageRepository articleImageRepository) {
        this.imageService = imageService;
        this.articleRepository = articleRepository;
        this.memberRepository = memberRepository;
        this.teamRepository = teamRepository;
        this.articleImageRepository = articleImageRepository;
    }

    @Transactional
    public void save(Long memberId, ArticleRequest articleRequest, MultipartFile images){

        Member member = memberRepository.findById(memberId).orElseThrow(
            MemberNotFoundException::new
        );

        Team team = teamRepository.findById(articleRequest.teamId()).orElseThrow(
            TeamNotFoundException::new
        );

        Article article = Article.builder()
            .articleTitle(articleRequest.articleTitle())
            .articleBody(articleRequest.articleBody())
            .member(member)
            .team(team)
            .build();

        articleRepository.save(article);

        handleImageUpload(article, images);
    }

    @Transactional
    public List<Article> getMyArticles(Long userId){
        return articleRepository.findByMemberIdWithImages(userId);
    }

    @Transactional
    public List<Article> getArticlesByTeam(Long teamId){
        return articleRepository.findByTeamTeamIdWithImages(teamId);
    }

    @Transactional
    public Article getArticleById(Long articleId){
        return articleRepository.findByIdWithImages(articleId).orElseThrow(
            ArticleNotFoundException::new
        );
    }

    @Transactional
    public void deleteArticle(Long articleId) {
        Article article = articleRepository.findById(articleId).orElseThrow(
            ArticleNotFoundException::new
        );

        articleRepository.delete(article);
    }

    @Transactional
    public void updateArticle(Long articleId, ArticleRequest articleRequest, MultipartFile image) {
        Article article = articleRepository.findByIdWithImages(articleId).orElseThrow(
            ArticleNotFoundException::new
        );

        updateArticleFields(article, articleRequest);

        handleImageUpdate(article, image);
    }

    private void handleImageUpload(Article article, MultipartFile images) {
        if (images != null && !images.isEmpty()) {
            String imageUrl = imageService.articleImageUpload(images);

            ArticleImage articleImage = ArticleImage.builder()
                .article(article)
                .imageUrl(imageUrl)
                .build();

            articleImageRepository.save(articleImage);
        }
    }

    private void updateArticleFields(Article article, ArticleRequest articleRequest) {
        if (articleRequest.articleTitle() != null) {
            article.updateTitle(articleRequest.articleTitle());
        }
        if (articleRequest.articleBody() != null) {
            article.updateBody(articleRequest.articleBody());
        }
        if (articleRequest.articleCategory() != null) {
            article.updateCategory(articleRequest.articleCategory());
        }
    }

    private void handleImageUpdate(Article article, MultipartFile image) {
        if (image != null && !image.isEmpty()) {
            // 1. 기존 이미지 삭제
            ArticleImage existingImage = article.getImages().stream()
                .findFirst() // 첫 번째 이미지를 삭제 대상으로 가정
                .orElse(null);

            if (existingImage != null) {
                imageService.deleteImageByUrl(existingImage.getImageUrl());
                article.getImages().remove(existingImage);
            }

            String newImageUrl = imageService.articleImageUpload(image);

            ArticleImage newArticleImage = ArticleImage.builder()
                .article(article)
                .imageUrl(newImageUrl)
                .build();
            article.getImages().add(newArticleImage);
        }
    }

}
