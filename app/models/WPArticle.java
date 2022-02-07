package models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.*;
import play.mvc.QueryStringBindable;

import java.io.Serializable;
import java.util.*;

public class WPArticle implements QueryStringBindable<WPArticle>, Serializable {
    private static final long serialVersionUID = 7860293794078412243L;

    public String id;
    public String article_url;
    public String title;
    public String author;
    public long published_date;
    public List<Content> contents;
    public String type;
    public String source;

    public WPArticle() {

    }

    public WPArticle(String id, String article_url, String title, String author, long published_date, List<Content> contents, String type, String source){
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

    @Override
    public Optional<WPArticle> bind(String key, java.util.Map<String, String[]> params) {
        List<Content> cL = new Gson().fromJson(params.get("contents")[0], List.class);
        try {
            id = String.valueOf(params.get("category")[0]);
            article_url = String.valueOf(params.get("article_url")[0]);
            title = String.valueOf(params.get("title")[0]);
            author = String.valueOf(params.get("author")[0]);
            published_date = Long.parseLong(params.get("published_date")[0]);
            contents = cL;
            type = String.valueOf(params.get("type")[0]);
            source = String.valueOf(params.get("source")[0]);

            return Optional.of(this);

        } catch (Exception e) { // no parameter match return None
            return Optional.empty();
        }
    }

    @Override
    public String unbind(String key) {
        return new StringBuilder().append("id=").append(id).append("article_url=").append(article_url).append("&title=").append(title).append("author=").append(author).append("published_date=").append(published_date).append("&contents=").append(contents).append("type=").append(type).append("source=").append(source).toString();
    }

    @Override
    public String javascriptUnbind() {
        return null;
    }

}
