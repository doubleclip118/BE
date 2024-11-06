package PNUMEAT.Backend.domain.article.service;


import PNUMEAT.Backend.domain.article.dto.request.ArticleRequest;
import PNUMEAT.Backend.domain.article.entity.Article;
import PNUMEAT.Backend.domain.article.enums.Category;
import PNUMEAT.Backend.domain.article.repository.ArticleRepository;
import PNUMEAT.Backend.domain.auth.entity.Member;
import PNUMEAT.Backend.global.error.Team24Exception;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static PNUMEAT.Backend.global.error.ErrorCode.*;

@Service
public class ArticleService {
    private final ArticleRepository articleRepository;

    public ArticleService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    @Transactional
    public Article save(ArticleRequest articleRequest, Member member){
        Article article = toEntity(articleRequest, member);

        return articleRepository.save(article);
    }

    @Transactional(readOnly = true)
    public Article findById(Long id){
        return articleRepository.findById(id)
                .orElseThrow(() -> new Team24Exception(ARTICLE_NOT_FOUND_ERROR));
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

        if(!article.getMember().getId().equals(member.getId())){
            throw new Team24Exception(ARTICLE_FORBIDDEN_ERROR);
        }

        articleRepository.deleteById(id);
    }

    @Transactional
    public void updateById(Long id, ArticleRequest articleRequest, Member member){
        Article article = articleRepository.findById(id)
                .orElseThrow(()-> new Team24Exception(ARTICLE_NOT_FOUND_ERROR));

        if(!article.getMember().getId().equals(member.getId())){
            throw new Team24Exception(ARTICLE_FORBIDDEN_ERROR);
        }
        article.updateArticle(articleRequest.title(), articleRequest.content(), articleRequest.category(), articleRequest.image());
    }

    @Transactional(readOnly = true)
    public List<Article> findByCategory(Category category){
        return articleRepository.findByCategory(category);
    }


    public Article toEntity(ArticleRequest articleRequest,Member member) {
        return Article.builder()
                .member(member)
                .title(articleRequest.title())
                .content(articleRequest.content())
                .category(articleRequest.category())
                .image(articleRequest.image())
                .deleted(false)
                .build();
    }
}
