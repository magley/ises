package rs.sbnz.service.article.dto;

public class NewArticleCommentDTO {
    private Long articleId;
    private String comment;

    public NewArticleCommentDTO() {
    }

    public NewArticleCommentDTO(Long articleId, String comment) {
        this.articleId = articleId;
        this.comment = comment;
    }

    public Long getArticleId() {
        return this.articleId;
    }

    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }

    public String getComment() {
        return this.comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
