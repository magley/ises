package rs.sbnz.service.article.dto;

import java.time.Instant;

import rs.sbnz.model.article.ArticlePurchase;
import rs.sbnz.service.user.dto.UserDTO;

public class ArticlePurchaseDTO {
    private Long id;
    private UserDTO user;
    private ArticleDTO article;
    private Instant timestamp;

    public ArticlePurchaseDTO() {
    }

    public ArticlePurchaseDTO(Long id, UserDTO user, ArticleDTO article, Instant timestamp) {
        this.id = id;
        this.user = user;
        this.article = article;
        this.timestamp = timestamp;
    }

    public ArticlePurchaseDTO(ArticlePurchase purchase) {
        this.id = purchase.getId();
        this.user = new UserDTO(purchase.getUser());
        this.article = new ArticleDTO(purchase.getArticle());
        this.timestamp = purchase.getTimestamp();
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserDTO getUser() {
        return this.user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public ArticleDTO getArticle() {
        return this.article;
    }

    public void setArticle(ArticleDTO article) {
        this.article = article;
    }

    public Instant getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}