package PNUMEAT.Backend.domain.article.service;


import PNUMEAT.Backend.domain.article.dto.request.ArticleRequest;
import PNUMEAT.Backend.domain.article.entity.Article;
import PNUMEAT.Backend.domain.article.enums.Category;
import PNUMEAT.Backend.domain.article.repository.ArticleRepository;
import PNUMEAT.Backend.domain.auth.entity.Member;
import PNUMEAT.Backend.global.error.Team24Exception;
import PNUMEAT.Backend.global.images.ImageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import static PNUMEAT.Backend.global.error.ErrorCode.*;

@Service
@Slf4j
public class ArticleService {
    private final ImageService imageService;
    private final ArticleRepository articleRepository;

    public ArticleService(ImageService imageService, ArticleRepository articleRepository) {
        this.imageService = imageService;
        this.articleRepository = articleRepository;
    }

    @Transactional
    public Article save(ArticleRequest articleRequest, Member member, MultipartFile multipartFile){

        Article article = toEntity(articleRequest, member,null);

        Article savedArticle = articleRepository.save(article);

        Long articleId = article.getArticleId();

        article.insertimgUrl(imageService.imageload(multipartFile, articleId));

        return savedArticle;
    }

    @Transactional(readOnly = true)
    public Article findById(Long id, Member member){
        Article article =  articleRepository.findById(id)
                .orElseThrow(() -> new Team24Exception(ARTICLE_NOT_FOUND_ERROR));

        if(!member.getRole().equals("ROLE_ADMIN")){
            if(!article.getMember().getId().equals(member.getId())){
                throw new Team24Exception(ARTICLE_FORBIDDEN_ERROR);
            }
        }
        return article;
    }

    @Transactional(readOnly = true)
    public List<Article> findAllArticles(){
        return articleRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Article> findByMemberId(Long memberId){
        return articleRepository.findByMemberId(memberId);
    }

    @Transactional
    public void deleteById(Long id, Member member){
        Article article = articleRepository.findById(id)
                .orElseThrow(()-> new Team24Exception(ARTICLE_NOT_FOUND_ERROR));

        if(!member.getRole().equals("ROLE_ADMIN")){
            if(!article.getMember().getId().equals(member.getId())){
                throw new Team24Exception(ARTICLE_FORBIDDEN_ERROR);
            }
        }

        imageService.deleteImageByUrl(article.getImage());

        articleRepository.deleteById(id);
    }

    @Transactional
    public void updateById(Long id, ArticleRequest articleRequest, Member member, MultipartFile multipartFile){
        Article article = articleRepository.findById(id)
                .orElseThrow(()-> new Team24Exception(ARTICLE_NOT_FOUND_ERROR));

        if(!member.getRole().equals("ROLE_ADMIN")){
            if(!article.getMember().getId().equals(member.getId())){
                throw new Team24Exception(ARTICLE_FORBIDDEN_ERROR);
            }
        }

        String imageload = article.getImage();
        if (multipartFile != null && !multipartFile.isEmpty()) {
            imageload = imageService.imageload(multipartFile, article.getArticleId());
        }

        article.updateArticle(articleRequest.title(), articleRequest.content(), articleRequest.category(), imageload);
    }

    @Transactional(readOnly = true)
    public List<Article> findByCategory(Category category){
        return articleRepository.findByCategory(category);
    }


    public Article toEntity(ArticleRequest articleRequest, Member member, String imgUrl) {
        return Article.builder()
                .member(member)
                .title(articleRequest.title())
                .content(articleRequest.content())
                .category(articleRequest.category())
                .image(imgUrl)
                .deleted(false)
                .build();
    }
}
