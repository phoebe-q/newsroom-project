package models;

import com.alibaba.fastjson.JSON;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import play.mvc.PathBindable;
import play.mvc.QueryStringBindable;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
