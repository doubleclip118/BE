package PNUMEAT.Backend.domain.article.controller;

import PNUMEAT.Backend.domain.article.dto.request.ArticleRequest;
import PNUMEAT.Backend.domain.article.entity.Article;
import PNUMEAT.Backend.domain.article.enums.Category;
import PNUMEAT.Backend.domain.article.service.ArticleService;
import PNUMEAT.Backend.domain.auth.entity.Member;
import PNUMEAT.Backend.global.security.annotation.LoginMember;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/articles")
public class ArticleController {

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    /*
    @GetMapping
    public String getArticlesPage(Model model, HttpServletRequest request) {
        model.addAttribute("articles", articleService.findAllArticles());
        HttpSession session = request.getSession();
        session.setAttribute("authToken", request.getHeader("Authorization"));
        return "article/list";
    } */

    @GetMapping("/new")
    public String getNewArticlePage(Model model, HttpServletRequest request) {
        model.addAttribute("articleRequest", new ArticleRequest(null,null,null,null));
        model.addAttribute("categories", Category.values());
        HttpSession session = request.getSession();
        session.setAttribute("authToken", request.getHeader("Authorization"));
        return "article/new";
    }

    @PostMapping
    @ResponseBody
    public Map<String, String> createArticle(@ModelAttribute ArticleRequest articleRequest, HttpServletRequest request, @LoginMember Member member) {
        Article article = articleService.save(articleRequest, member);
        HttpSession session = request.getSession();
        session.setAttribute("authToken", request.getHeader("Authorization"));
        Map<String, String> response = new HashMap<>();
        response.put("category", article.getCategory().name());
        return response;
    }


    @GetMapping("/{id}/edit")
    public String getEditArticlePage(@PathVariable Long id, Model model, HttpServletRequest request) {
        Article article = articleService.findById(id);
        model.addAttribute("article", article);
        model.addAttribute("categories", Category.values());

        HttpSession session = request.getSession();
        session.setAttribute("authToken", request.getHeader("Authorization"));
        return "article/edit";
    }

    @PostMapping("/{id}")
    public String updateArticle(@PathVariable Long id, @ModelAttribute ArticleRequest articleRequest, @LoginMember Member member) {
        articleService.updateById(id, articleRequest, member);
        return "redirect:/articles";
    }

    @PostMapping("/{id}/delete")
    public String deleteArticle(@PathVariable Long id, @LoginMember Member member) {
        articleService.deleteById(id, member);
        return "redirect:/articles";
    }

    @GetMapping("/{id}")
    public String getArticleDetail(@PathVariable Long id, Model model, HttpServletRequest request) {
        Article article = articleService.findById(id);
        model.addAttribute("article", article);

        HttpSession session = request.getSession();
        session.setAttribute("authToken", request.getHeader("Authorization"));
        return "article/detail";
    }

    @GetMapping
    public String getMainPage() {
        return "article/main";
    }

    @GetMapping("/category/{category}")
    public String getArticlesByCategory(@PathVariable Category category, Model model, HttpServletRequest request) {
        List<Article> articles = articleService.findByCategory(category);

        HttpSession session = request.getSession();
        session.setAttribute("authToken", request.getHeader("Authorization"));

        model.addAttribute("articles", articles);
        model.addAttribute("selectedCategory", category.getName());
        return "article/list";
    }


}
