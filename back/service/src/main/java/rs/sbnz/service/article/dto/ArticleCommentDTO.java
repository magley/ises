package rs.sbnz.service.article.dto;

import java.time.Instant;

import rs.sbnz.model.article.ArticleComment;

public class ArticleCommentDTO {
    private Long id;
    private Long userId;
    private String userEmail;
    private Instant timestamp;
    private String comment;

    public ArticleCommentDTO() {
    }

    public ArticleCommentDTO(Long id, Long userId, String userEmail, Instant timestamp, String comment) {
        this.id = id;
        this.userId = userId;
        this.userEmail = userEmail;
        this.timestamp = timestamp;
        this.comment = comment;
    }

    public ArticleCommentDTO(ArticleComment comment) {
        this.id = comment.getId();
        this.userId = comment.getUser().getId();
        this.userEmail = comment.getUser().getEmail();
        this.timestamp = comment.getTimestamp();
        this.comment = comment.getComment();
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserEmail() {
        return this.userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public Instant getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getComment() {
        return this.comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
