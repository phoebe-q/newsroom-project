package controllers;

import com.alibaba.fastjson.JSON;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.xcontent.XContentBuilder;
import org.elasticsearch.xcontent.XContentFactory;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import models.Article;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
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
            System.out.println(dir_size);
            String file_num = "";

            for(int i = 1; i < dir_size+1; i++) {
                if (i < 10) {
                    file_num = "00" + i;
                } else if (i >= 10 && i < 100) {
                    file_num = "0" + i;
                } else {
                    file_num = "" + i;
                }

                try {
                    StringBuilder stringBuilder = new StringBuilder();
                    File myFile = new File(dir + "/" + file_num + ".txt");
                    Scanner myReader = new Scanner(myFile);
                    String title = myReader.nextLine();
                    System.out.println(title);
                    while (myReader.hasNextLine()) {
                        String data = myReader.nextLine() + " ";
                        stringBuilder.append(data);
                    }
                    myReader.close();
                    String content = stringBuilder.toString();

                    XContentBuilder builder = XContentFactory.jsonBuilder()
                            .startObject()
                            .field("category", category)
                            .field("title", title)
                            .field("content", content)
                    .endObject();

                    IndexRequest indexRequest = new IndexRequest("articles-db");
                    indexRequest.source(builder);

                    IndexResponse response = client.index(indexRequest, RequestOptions.DEFAULT);
                    System.out.println(response.getResult());
                } catch (FileNotFoundException e) {
                    System.out.println("An error occurred.");
                    e.printStackTrace();
                }
            }
        }
        return ok(views.html.articles.populated.render());
    }

    public Result search(Http.Request request, String searchTerm) throws IOException {
        ClientConfiguration clientConfiguration =
                ClientConfiguration.builder().connectedTo("localhost:9200").build();
        RestHighLevelClient client = RestClients.create(clientConfiguration).rest();

        QueryBuilder query = QueryBuilders.matchQuery("content", searchTerm);
        SearchRequest searchRequest = new SearchRequest("articles-db");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(query);
        searchRequest.searchType(SearchType.DFS_QUERY_THEN_FETCH);
        searchRequest.source(searchSourceBuilder);
        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
        SearchHit[] searchHits = response.getHits().getHits();
        List<Article> results =
                Arrays.stream(searchHits)
                        .map(hit -> JSON.parseObject(hit.getSourceAsString(), Article.class))
                        .collect(Collectors.toList());

        return ok(views.html.results.render(request, results));
    }

    public Result resultView(String category, String title, String content) {
        return ok(views.html.result.render(category, title, content));
    }
}
