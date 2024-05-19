package rs.sbnz.service.article;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import rs.sbnz.model.article.Article;

public interface IArticleRepo extends JpaRepository<Article, Long> {
    List<Article> findByOwner(Long ownerId);
}
