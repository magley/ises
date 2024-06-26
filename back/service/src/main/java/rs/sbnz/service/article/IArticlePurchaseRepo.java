package rs.sbnz.service.article;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import rs.sbnz.model.User;
import rs.sbnz.model.article.ArticlePurchase;

public interface IArticlePurchaseRepo extends JpaRepository<ArticlePurchase, Long> {
    List<ArticlePurchase> findByArticle(Long articleId);
    List<ArticlePurchase> findByUser(User user);
}
