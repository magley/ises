package rs.sbnz.service.article.dto;

public class NewArticlePurchaseDTO {
    private Long articleId;

    public NewArticlePurchaseDTO() {
    }

    public NewArticlePurchaseDTO(Long articleId) {
        this.articleId = articleId;
    }
    
    public Long getArticleId() {
        return this.articleId;
    }

    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }
}
