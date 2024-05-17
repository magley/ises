package rs.sbnz.service.article;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import rs.sbnz.model.article.Article;
import rs.sbnz.model.article.ArticleComment;
import rs.sbnz.model.article.ArticlePurchase;

@Component
public class ArticleService {
    @Autowired private IArticleRepo articleRepo;
    @Autowired private IArticlePurchaseRepo articlePurchaseRepo;
    @Autowired private IArticleCommentRepo articleCommentRepo;

    //-------------------------------------------------------------------------
    // Article
    //-------------------------------------------------------------------------

    public List<Article> findAllArticles() {
        return articleRepo.findAll();
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

    public List<ArticlePurchase> findArticlePurcasesByUser(Long userId) {
        return articlePurchaseRepo.findByUser(userId);
    }
}
