package rs.sbnz.service.article.dto;

public class NewArticleDTO {
    private String name;
    private Double price;
    private String imgBase64;

    public NewArticleDTO() {
    }

    public NewArticleDTO(String name, Double price, String imgBase64) {
        this.name = name;
        this.price = price;
        this.imgBase64 = imgBase64;
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
