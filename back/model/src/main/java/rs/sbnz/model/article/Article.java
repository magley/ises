package rs.sbnz.model.article;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import rs.sbnz.model.User;

@Entity
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Instant timestamp;

    @ManyToOne
    private User owner;

    @OneToMany(cascade = CascadeType.ALL , fetch = FetchType.EAGER, mappedBy = "article")
    private Set<ArticleComment> comments;

    @Column
    private String name;

    @Column
    private Double price;

    public Article() {
    }

    public Article(Long id, Instant timestamp, User owner, String name, Double price) {
        this.id = id;
        this.timestamp = timestamp;
        this.owner = owner;
        this.comments = new HashSet<>();
        this.name = name;
        this.price = price;
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

    public User getOwner() {
        return this.owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Set<ArticleComment> getComments() {
        return this.comments;
    }

    public void setComments(Set<ArticleComment> comments) {
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
}
