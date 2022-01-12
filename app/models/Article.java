package models;

import play.mvc.QueryStringBindable;
import java.util.Optional;

public class Article implements QueryStringBindable<Article> {

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

    public String getTitle() {return title;}

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public Optional<Article> bind(String key, java.util.Map<String, String[]> params) {
        try {
            category = String.valueOf(params.get("category")[0]);
            title = String.valueOf(params.get("title")[0]);
            content = String.valueOf(params.get("content")[0]);
            return Optional.of(this);

        } catch (Exception e) { // no parameter match return None
            return Optional.empty();
        }
    }

    @Override
    public String unbind(String key) {
        return new StringBuilder().append("category=").append(category).append("&title=").append(title).append("&content=").append(content).toString();
    }

    @Override
    public String javascriptUnbind() {
        return null;
    }

}
