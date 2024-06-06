package rs.sbnz.service.article;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import rs.sbnz.model.User;
import rs.sbnz.model.article.Article;
import rs.sbnz.model.article.ArticleComment;
import rs.sbnz.model.article.ArticlePurchase;
import rs.sbnz.service.article.dto.NewArticleCommentDTO;
import rs.sbnz.service.article.dto.NewArticleDTO;
import rs.sbnz.service.exceptions.NotFoundException;
import rs.sbnz.service.util.FileUtil;

@Component
public class ArticleService {
    @Autowired private IArticleRepo articleRepo;
    @Autowired private IArticlePurchaseRepo articlePurchaseRepo;
    @Autowired private IArticleCommentRepo articleCommentRepo;
    @Autowired private FileUtil fileUtil;

    //-------------------------------------------------------------------------
    // Article
    //-------------------------------------------------------------------------

    public List<Article> findAllArticles() {
        return articleRepo.findAll();
    }

    public Article save(NewArticleDTO dto, User ownerOfTheArticle) {
        Article a = new Article(null, Instant.now(), ownerOfTheArticle, dto.getName(), dto.getPrice());
        a = save(a);

        try {
            fileUtil.saveImage(dto.getImgBase64(), "" + a.getId());
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return a;
    }

    public Article save(Article a) {
        a = articleRepo.save(a);
        return a;
    }

    public Optional<Article> findArticleById(Long id) {
        return articleRepo.findById(id);
    }

    public List<Article> findArticlesByOwner(Long ownerId) {
        return articleRepo.findByOwner(ownerId);
    }

    //-------------------------------------------------------------------------
    // Article Comment
    //-------------------------------------------------------------------------

    public ArticleComment save(NewArticleCommentDTO dto, User user) {
        Article article = findArticleById(dto.getArticleId()).orElseThrow(() -> new NotFoundException());
        ArticleComment comment = new ArticleComment(null, Instant.now(), dto.getComment(), user, article);
        comment = save(comment);
        return comment;
    }

    public ArticleComment save(ArticleComment ac) {
        ac = articleCommentRepo.save(ac);
        return ac;
    }

    public Optional<ArticleComment> findArticleCommentById(Long id) {
        return articleCommentRepo.findById(id);
    }

    public List<ArticleComment> findArticleCommentsByArticle(Long articleId) {
        return articleCommentRepo.findByArticle(articleId);
    }

    public List<ArticleComment> findArticleCommentsByUser(Long userId) {
        return articleCommentRepo.findByUser(userId);
    }

    //-------------------------------------------------------------------------
    // Article Purchase
    //-------------------------------------------------------------------------

    public ArticlePurchase save(Long articleId, User user) {
        Article article = findArticleById(articleId).orElseThrow(() -> new NotFoundException());
        ArticlePurchase ap = new ArticlePurchase(null, user, article, Instant.now());
        ap = save(ap);
        return ap;
    }

    public ArticlePurchase save(ArticlePurchase ap) {
        ap = articlePurchaseRepo.save(ap);
        return ap;
    }

    public Optional<ArticlePurchase> findArticlePurchaseById(Long id) {
        return articlePurchaseRepo.findById(id);
    }

    public List<ArticlePurchase> findArticlePurcasesByArticle(Long articleId) {
        return articlePurchaseRepo.findByArticle(articleId);
    }

    public List<ArticlePurchase> findArticlePurcasesByUser(User user) {
        return articlePurchaseRepo.findByUser(user);
    }
}
