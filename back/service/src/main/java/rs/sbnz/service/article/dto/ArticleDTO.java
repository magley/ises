package rs.sbnz.service.article.dto;

import java.time.Instant;

import rs.sbnz.model.article.Article;

public class ArticleDTO {
    private Long id;
    private Instant timestamp;
    private String name;
    private Double price;

    public ArticleDTO() {
    }

    public ArticleDTO(Long id, Instant timestamp, String name, Double price) {
        this.id = id;
        this.timestamp = timestamp;
        this.name = name;
        this.price = price;
    }

    public ArticleDTO(Article article) {
        this.id = article.getId();
        this.timestamp = article.getTimestamp();
        this.name = article.getName();
        this.price = article.getPrice();
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return this.price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
