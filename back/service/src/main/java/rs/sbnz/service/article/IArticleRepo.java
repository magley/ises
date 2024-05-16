package rs.sbnz.service.article;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import rs.sbnz.model.article.Article;

@Repository
public interface IArticleRepo extends JpaRepository<Article, Long> {
    List<Article> findByOwner(Long ownerId);
}
