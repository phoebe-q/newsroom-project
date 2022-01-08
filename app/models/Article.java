package models;

public class Article {

    public String category;
    public String title;
    public String content;

    public Article() {
    }

    Article(String category, String title, String content) {
        super();
        this.category = category;
        this.title = title;
        this.content = content;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
