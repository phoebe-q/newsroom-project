package controllers;

import com.fasterxml.jackson.databind.ObjectMapper;

import models.*;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.xcontent.XContentType;

import org.jsoup.Jsoup;

import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;

import play.mvc.Controller;
import play.mvc.Result;

import java.io.*;
import java.util.*;

public class ArticleController extends Controller {

    public Result index() {
        return null;
    }

    public Result indexDB() throws IOException {
        ClientConfiguration clientConfiguration =
                ClientConfiguration.builder().connectedTo("localhost:9200").withSocketTimeout(6000000).build();
        RestHighLevelClient client = RestClients.create(clientConfiguration).rest();
        BufferedReader reader = new BufferedReader(new FileReader("/Users/phoebe/Desktop/Fourth Year/Honours Project/TREC_Washington_Post_collection.v3.jl"));
        ObjectMapper objectMapper = new ObjectMapper();

        IndexRequest indexRequest = new IndexRequest("post-washington");
        for (int i = 0; i < 5000; i++) {
            try {
                String line = reader.readLine();
                WPArticleIn article = objectMapper.readValue(line, WPArticleIn.class);

                String category = "";
                Image image = new Image();
                StringBuilder contentBuilder = new StringBuilder();
                for (Content c : article.getContents()) {
                    if (Objects.equals(c.getType(), "kicker")) {
                        category = c.getContent();
                    } else if (Objects.equals(c.getType(), "image")) {
                        image = new Image(c.getFullcaption(), c.getImageURL(), c.getMime(), c.getImageHeight(), c.getImageWidth(), c.getType(), c.getBlurb());
                    } else if (Objects.equals(c.getSubtype(), "paragraph")) {
                        contentBuilder.append(" <br/> ").append(c.getContent());
                    }
                }
                String contents = Jsoup.parse(contentBuilder.toString()).text();
                WPArticle formatted_article = new WPArticle(article.getId(), article.getArticleURL(), article.getTitle(), article.getAuthor(), article.getPublishedDate(), category, contents, image, article.getType(), article.getSource());
                String upload_string = objectMapper.writeValueAsString(formatted_article);

                indexRequest.source(upload_string, XContentType.JSON);
                IndexResponse response = client.index(indexRequest, RequestOptions.DEFAULT);
                System.out.println(response.getResult());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ok(views.html.articles.populated.render());
    }


}