package models;

public class Article {

    public String category;
    public String content;

    public Article() {
    }

    Article(String category, String title, String content) {
        super();
        this.category = category;
        this.content = content;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
