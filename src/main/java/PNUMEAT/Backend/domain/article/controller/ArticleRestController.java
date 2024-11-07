//package PNUMEAT.Backend.domain.article.controller;
//
//import PNUMEAT.Backend.domain.article.dto.request.ArticleRequest;
//import PNUMEAT.Backend.domain.article.dto.response.ArticleResponse;
//import PNUMEAT.Backend.domain.article.entity.Article;
//import PNUMEAT.Backend.domain.article.enums.Category;
//import PNUMEAT.Backend.domain.article.service.ArticleService;
//import PNUMEAT.Backend.domain.auth.entity.Member;
//import PNUMEAT.Backend.global.security.annotation.LoginMember;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/articles")
//public class ArticleRestController {
//
//    private final ArticleService articleService;
//
//    public ArticleRestController(ArticleService articleService) {
//        this.articleService = articleService;
//    }
//
//    @PostMapping
//    public ResponseEntity<ArticleResponse> createArticle(@RequestBody ArticleRequest articleRequest, @LoginMember Member member) {
//        Article savedArticle = articleService.save(articleRequest, member);
//        return ResponseEntity.status(HttpStatus.CREATED)
//                .contentType(MediaType.APPLICATION_JSON)
//                .body(ArticleResponse.of(savedArticle));
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<Article> getArticleById(@PathVariable Long id) {
//        Article article = articleService.findById(id);
//        return new ResponseEntity<>(article, HttpStatus.OK);
//    }
//
//    @GetMapping
//    public ResponseEntity<List<Article>> getAllArticles() {
//        List<Article> articles = articleService.findAllArticles();
//        return new ResponseEntity<>(articles, HttpStatus.OK);
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<Void> updateArticle(@PathVariable Long id, @RequestBody ArticleRequest articleRequest, @LoginMember Member member) {
//        articleService.updateById(id, articleRequest, member);
//        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteArticle(@PathVariable Long id, @LoginMember Member member) {
//        articleService.deleteById(id, member);
//        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//    }
//
//    @GetMapping("/{id}/edit")
//    public ResponseEntity<Void> editArticleCheck(@PathVariable Long id) {
//        return new ResponseEntity<>(HttpStatus.OK);
//    }
//
//    @GetMapping("/category/{category}")
//    @ResponseBody
//    public List<Article> getArticlesByCategoryApi(@PathVariable Category category) {
//        return articleService.findByCategory(category);
//    }
//}
