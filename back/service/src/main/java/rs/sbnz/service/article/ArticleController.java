package rs.sbnz.service.article;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import rs.sbnz.model.User;
import rs.sbnz.model.api.Packet;
import rs.sbnz.model.article.Article;
import rs.sbnz.service.article.dto.ArticleDTO;
import rs.sbnz.service.article.dto.ArticleDetailsDTO;
import rs.sbnz.service.article.dto.NewArticleCommentDTO;
import rs.sbnz.service.exceptions.NotFoundException;
import rs.sbnz.service.request.RequestService;
import rs.sbnz.service.util.AuthenticationFacade;
import rs.sbnz.service.util.RBACUtil;

@RestController
@RequestMapping("api/article")
public class ArticleController {
    @Autowired private RequestService requestService;
    @Autowired private ArticleService articleService;
    @Autowired private RBACUtil rbacUtil;
    @Autowired private AuthenticationFacade authenticationFacade;

    @GetMapping("/all")
    public ResponseEntity<?> findAllArticles(Packet packet) {
        requestService.onRequest(packet);
        List<ArticleDTO> articles = articleService.findAllArticles().stream().map(a -> new ArticleDTO(a)).toList();
        return ResponseEntity.status(HttpStatus.OK).body(articles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(Packet packet, @PathVariable Long id) {
        requestService.onRequest(packet);
        Article article = articleService.findArticleById(id).orElseThrow(() -> new NotFoundException());
        ArticleDetailsDTO dto = new ArticleDetailsDTO(article);
        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

    @PostMapping("/{id}/comment")
    public ResponseEntity<?> leaveComment(Packet packet, @RequestBody NewArticleCommentDTO dto) {
        requestService.onRequest(packet);
        User user = authenticationFacade.getUser();
        rbacUtil.preAuthorize2("comment_on_articles");

        articleService.save(dto, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }
}
