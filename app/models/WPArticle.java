package models;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class WPArticle {

    public String id;
    public String article_url;
    public String title;
    public String author;
    public Date published_date;
    public List<Map<String, String>> contents;
    public String type;
    public String source;

    public WPArticle() {

    }

    WPArticle(String id, String article_url, String title, String author, Date published_date, List<Map<String, String>> contents, String type, String source){
        super();
        this.id = id;
        this.article_url = article_url;
        this.title = title;
        this.author = author;
        this.published_date = published_date;
        this.contents = contents;
        this.type = type;
        this.source = source;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getArticleURL() {
        return article_url;
    }
    public void setArticleURL(String article_url) {
        this.article_url = article_url;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }

    public Date getPublishedDate() {
        return published_date;
    }
    public void setPublishedDate(Date published_date) {
        this.published_date = published_date;
    }

    public List<Map<String, String>> getContents() {
        return contents;
    }
    public void setContents(List<Map<String, String>> contents) {
        this.contents = contents;
    }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

}
