package PNUMEAT.Backend.domain.article.controller;

import PNUMEAT.Backend.domain.article.dto.request.ArticleRequest;
import PNUMEAT.Backend.domain.article.entity.Article;
import PNUMEAT.Backend.domain.article.enums.Category;
import PNUMEAT.Backend.domain.article.service.ArticleService;
import PNUMEAT.Backend.domain.auth.entity.Member;
import PNUMEAT.Backend.global.security.annotation.LoginMember;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/articles")
@Slf4j
public class ArticleController {

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping
    public String getArticlesPage(Model model, HttpServletRequest request) {
        model.addAttribute("articles", articleService.findAllArticles());
        addAuthorizationHeaderInSession(request);
        return "article/list";
    }

    @GetMapping("/{id}")
    public String getArticleDetail(@PathVariable Long id, Model model, HttpServletRequest request) {
        Article article = articleService.findById(id);
        model.addAttribute("article", article);
        addAuthorizationHeaderInSession(request);
        return "article/detail";
    }

    @GetMapping("/new")
    public String getNewArticlePage(Model model, HttpServletRequest request) {
        model.addAttribute("articleRequest", new ArticleRequest(null,null,null,null));
        model.addAttribute("categories", Category.values());
        addAuthorizationHeaderInSession(request);
        return "article/new";
    }

    @PostMapping
    public String createArticle(@RequestBody ArticleRequest articleRequest,
                                @LoginMember Member member,
                                HttpServletRequest request) {
        addAuthorizationHeaderInSession(request);
        articleService.save(articleRequest, member);
        return "redirect:/articles/";
    }

    @GetMapping("/category/{category}")
    public String getArticlesByCategory(@PathVariable Category category, Model model, HttpServletRequest request) {
        List<Article> articles = articleService.findByCategory(category);

        addAuthorizationHeaderInSession(request);

        model.addAttribute("articles", articles);
        model.addAttribute("selectedCategory", category.getName());
        return "article/list";
    }

    @GetMapping("/{id}/edit")
    public String getEditArticlePage(@PathVariable Long id, Model model, HttpServletRequest request) {
        Article article = articleService.findById(id);
        model.addAttribute("article", article);
        model.addAttribute("categories", Category.values());

        addAuthorizationHeaderInSession(request);
        return "article/edit";
    }

    @PostMapping("/{id}")
    public String updateArticle(@PathVariable Long id,
                                @RequestBody ArticleRequest articleRequest,
                                @LoginMember Member member,
                                HttpServletRequest request) {
        articleService.updateById(id, articleRequest, member);
        addAuthorizationHeaderInSession(request);
        return "redirect:/articles";
    }
//
    @DeleteMapping("/{id}")
    public String deleteArticle(@PathVariable Long id,
                                @LoginMember Member member,
                                HttpServletRequest request) {
        articleService.deleteById(id, member);
        addAuthorizationHeaderInSession(request);
        return "redirect:/articles";
    }
//
//
//
//    @GetMapping
//    public String getMainPage() {
//        return "article/main";
//    }
//


    private void addAuthorizationHeaderInSession(HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.setAttribute("authToken", request.getHeader("Authorization"));
    }

}
