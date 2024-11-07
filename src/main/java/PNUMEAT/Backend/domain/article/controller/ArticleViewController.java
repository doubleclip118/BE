//package PNUMEAT.Backend.domain.article.controller;
//
//import PNUMEAT.Backend.domain.article.service.ArticleService;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//@Controller
//@RequestMapping("/articles-view")
//public class ArticleViewController {
//    private final ArticleService articleService;
//
//    public ArticleViewController(ArticleService articleService) {
//        this.articleService = articleService;
//    }
//
//    @GetMapping
//    public String getAllArticles(Model model) {
//        model.addAttribute("articles", articleService.findAllArticles());
//        return "articles/list";
//    }
//
//}
