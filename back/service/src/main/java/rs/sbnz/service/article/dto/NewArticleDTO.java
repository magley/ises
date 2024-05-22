package rs.sbnz.service.article.dto;

public class NewArticleDTO {
    private String name;
    private Double price;

    public NewArticleDTO() {
    }

    public NewArticleDTO(String name, Double price) {
        this.name = name;
        this.price = price;
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
