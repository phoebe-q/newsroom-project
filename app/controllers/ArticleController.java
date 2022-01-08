package controllers;

import com.alibaba.fastjson.JSON;
import org.apache.commons.io.IOUtils;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.xcontent.XContentBuilder;
import org.elasticsearch.xcontent.XContentFactory;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import play.mvc.Controller;
import play.mvc.Result;

import models.Article;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ArticleController extends Controller {

    public Result index() {return null;}

    public Result indexDB() throws IOException {
        ClientConfiguration clientConfiguration =
                ClientConfiguration.builder().connectedTo("localhost:9200").build();
        RestHighLevelClient client = RestClients.create(clientConfiguration).rest();


        List<String> categories = Arrays.asList("business", "entertainment", "politics", "tech");

        for(String category: categories) {
            String dir = "/Users/phoebe/Desktop/Fourth Year/Honours Project/extract bbc data/" + category;
            int dir_size = new File(dir).list().length;
            String file_num = "";

            for(int i = 1; i < dir_size+1; i++) {
                if (i < 10) {
                    file_num = "00" + i;
                } else if (i >= 10 && i < 100) {
                    file_num = "0" + i;
                } else {
                    file_num = "" + i;
                }

                InputStream stream = new FileInputStream(dir + "/" + file_num + ".txt");
                String raw_text = IOUtils.readLines(stream, "UTF-8").stream().toString();
                String content = raw_text.replace("\n", " ").replace("\r", " ");

                XContentBuilder builder = XContentFactory.jsonBuilder()
                        .startObject()
                        .field("category", category)
                        .field("content", content)
                        .endObject();

                IndexRequest indexRequest = new IndexRequest("people");
                indexRequest.source(builder);

                IndexResponse response = client.index(indexRequest, RequestOptions.DEFAULT);
                System.out.println(response.getResult());
            }
        }
        return ok(views.html.articles.populated.render());
    }

    public Result search(String searchTerm) throws IOException {
        ClientConfiguration clientConfiguration =
                ClientConfiguration.builder().connectedTo("localhost:9200").build();
        RestHighLevelClient client = RestClients.create(clientConfiguration).rest();

        MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("content", searchTerm);
        SearchRequest searchRequest = new SearchRequest();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(matchQueryBuilder);
        searchRequest.source(searchSourceBuilder);
        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
        SearchHit[] searchHits = response.getHits().getHits();
        List<Article> results =
                Arrays.stream(searchHits)
                        .map(hit -> JSON.parseObject(hit.getSourceAsString(), Article.class))
                        .collect(Collectors.toList());
        Article article = results.get(0);
        return ok(views.html.articles.show.render(article));
    }
}
