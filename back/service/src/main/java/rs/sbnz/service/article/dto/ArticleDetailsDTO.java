package rs.sbnz.service.article.dto;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.util.Base64;
import java.util.List;

import rs.sbnz.model.article.Article;
import rs.sbnz.service.user.dto.UserDTO;

public class ArticleDetailsDTO {
    private Long id;
    private Instant timestamp;
    private UserDTO owner;
    private List<ArticleCommentDTO> comments;
    private String name;
    private Double price;
    private String imgBase64;

    public ArticleDetailsDTO() {
    }

    public ArticleDetailsDTO(Long id, Instant timestamp, UserDTO owner, List<ArticleCommentDTO> comments, String name, Double price) {
        this.id = id;
        this.timestamp = timestamp;
        this.owner = owner;
        this.comments = comments;
        this.name = name;
        this.price = price;
    }

    public ArticleDetailsDTO(Article article) {
        this.id = article.getId();
        this.timestamp = article.getTimestamp();
        this.owner = new UserDTO(article.getOwner());
        this.comments = article.getComments().stream().map(c -> new ArticleCommentDTO(c)).toList();
        this.name = article.getName();
        this.price = article.getPrice();
        
        File file = new File("./images/article/" + id + ".jpg");
        byte[] bytes;
        try {
            bytes = Files.readAllBytes(file.toPath());
            imgBase64 = Base64.getEncoder().encodeToString(bytes);
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

    public UserDTO getOwner() {
        return this.owner;
    }

    public void setOwner(UserDTO owner) {
        this.owner = owner;
    }

    public List<ArticleCommentDTO> getComments() {
        return this.comments;
    }

    public void setComments(List<ArticleCommentDTO> comments) {
        this.comments = comments;
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
