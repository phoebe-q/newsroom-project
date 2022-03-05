package events;

import akka.actor.ActorRef;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.WPArticle;
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
import play.libs.Json;
import structures.AppState;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StartSearch implements EventProcessor {
    @Override
    public void processEvent(ActorRef out, AppState siteState, JsonNode message) throws IOException {

        {ObjectNode alert = Json.newObject();
        alert.put("messagetype", "alert");
        alert.put("text", "Searching Articles...");
        out.tell(alert, out);}

        String[] queryTerms = message.get("searchTerm").asText().split( " ");
        ClientConfiguration clientConfiguration =
                ClientConfiguration.builder().connectedTo("localhost:9200").withSocketTimeout(600000).build();
        RestHighLevelClient client = RestClients.create(clientConfiguration).rest();
        QueryBuilder query = QueryBuilders.termsQuery("contents", queryTerms);
        SearchRequest searchRequest = new SearchRequest("washington-post");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(query);
        searchRequest.searchType(SearchType.DFS_QUERY_THEN_FETCH);
        searchRequest.source(searchSourceBuilder);
        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
        SearchHit[] searchHits = response.getHits().getHits();

        siteState.newsResults =
                Arrays.stream(searchHits)
                        .map(hit -> JSON.parseObject(hit.getSourceAsString(), WPArticle.class))
                        .collect(Collectors.toList());

        {ObjectNode alert = Json.newObject();
            alert.put("messagetype", "alert");
            alert.put("text", "Articles Search Complete");
            out.tell(alert, out);}

        {ObjectNode m = Json.newObject();
            m.put("messagetype", "searchComplete");
            out.tell(m, out);}

    }
}
