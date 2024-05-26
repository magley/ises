package rs.sbnz.service.article.dto;

import java.io.IOException;
import java.time.Instant;
import java.util.Base64;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;

import rs.sbnz.model.article.Article;

public class ArticleDTO {
    private Long id;
    private Instant timestamp;
    private String name;
    private Double price;
    private String imgBase64;

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

        ClassPathResource resource = new ClassPathResource("static/Untitled.png");
        byte[] bytes;
        try {
            bytes = IOUtils.toByteArray(resource.getInputStream());
            this.imgBase64 = Base64.getEncoder().encodeToString(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }      
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

    public String getImgBase64() {
        return this.imgBase64;
    }

    public void setImgBase64(String imgBase64) {
        this.imgBase64 = imgBase64;
    }
}
