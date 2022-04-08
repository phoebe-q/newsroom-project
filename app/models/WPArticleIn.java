package models;

import java.util.List;

public class WPArticleIn {

    public String id; // unique id
    public String article_url; // article url linking to Washington Post website
    public String title; // title of the article
    public String author; // author of the article
    public long published_date; // date the article was published
    public List<Content> contents; // contents of the article - text of the article, additional links, date, kicker etc
    public String type; // type of the article
    public String source; // source of the article

    public WPArticleIn() {

    }

    public WPArticleIn(String id, String article_url, String title, String author, long published_date, List<Content> contents, String type, String source){
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

    public long getPublishedDate() {
        return published_date;
    }
    public void setPublishedDate(long published_date) {
        this.published_date = published_date;
    }

    public List<Content> getContents() {
        return contents;
    }
    public void setContents(List<Content> contents) {
        this.contents = contents;
    }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
}
