package rs.sbnz.service.article;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import rs.sbnz.model.article.ArticleComment;

public interface IArticleCommentRepo extends JpaRepository<ArticleComment, Long> {
    List<ArticleComment> findByArticle(Long articleId);
    List<ArticleComment> findByUser(Long userId);
}
