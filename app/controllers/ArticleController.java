package controllers;

import com.alibaba.fastjson.JSON;
import com.google.api.services.youtube.model.*;
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

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import com.google.api.services.youtube.YouTube;
import play.mvc.Controller;
import play.mvc.Result;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.*;

import models.Article;

import java.io.*;
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

    public Result search(Http.Request request, String searchTerm) throws IOException, GeneralSecurityException {
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
        searchYT(searchTerm);
        return ok(views.html.results.render(request, results));
    }

    public Result resultView(String category, String title, String content) {
        return ok(views.html.result.render(category, title, content));
    }

    private static final String CLIENT_SECRETS= "/Users/phoebe/Desktop/Fourth Year/Honours Project/newsroom/client_secret.json";
    private static final Collection<String> SCOPES = Arrays.asList("https://www.googleapis.com/auth/youtube.force-ssl");
    private static final String APPLICATION_NAME = "NewsRoom";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    /**
     * Create an authorized Credential object.
     *
     * @return an authorized Credential object.
     * @throws IOException
     */
    public static Credential authorize(final NetHttpTransport httpTransport) throws IOException {
        // Load client secrets.
        FileInputStream in = new FileInputStream(new File(CLIENT_SECRETS));
        //InputStream in = YoutubeController.class.getResourceAsStream(CLIENT_SECRETS);
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
                        .build();
        Credential credential =
                new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("phoebe.quinn04@gmail.com");
        return credential;
    }

    /**
     * Build and return an authorized API client service.
     *
     * @return an authorized API client service
     * @throws GeneralSecurityException, IOException
     */
    public static YouTube getService() throws GeneralSecurityException, IOException {
        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        System.out.println("httpTransport: " + httpTransport);
        Credential credential = authorize(httpTransport);
        return new YouTube.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    /**
     * Call function to create API service object. Define and
     * execute API request. Print API response.
     *
     * @throws GeneralSecurityException, IOException, GoogleJsonResponseException
     */
    public void searchYT(String searchTerm) throws GeneralSecurityException, IOException, GoogleJsonResponseException {
        YouTube youtubeService = getService();
        List<String> snippet = Arrays.asList("id,snippet");
        List<String> video = Arrays.asList("video");
        // Define and execute the API request
        YouTube.Search.List request = youtubeService.search()
                .list(snippet);
        SearchListResponse response = request.setChannelId("UC16niRr50-MSBwiO3YDb3RA") //channel id for BBCNews
                .setMaxResults(25L)
                .setQ(searchTerm)
                .setType(video)
                .setVideoCaption("closedCaption")
                .setVideoDimension("2d")
                .setVideoEmbeddable("true")
                .execute();
        List<SearchResult> results = response.getItems();
        int i = 1;
        for(SearchResult result: results){
            ResourceId resourceId = result.getId();
            String videoId = resourceId.getVideoId();
            System.out.println("video ID: " + videoId);
            String captionId = getCaptionID(videoId);
            downloadCaptions(captionId, i);
            i++;
        }

    }
    public String getCaptionID(String id) throws GeneralSecurityException, IOException {
        YouTube youtubeService = getService();
        // Define and execute the API request
        YouTube.Captions.List request = youtubeService.captions()
                .list(Collections.singletonList("id"), id);
        CaptionListResponse response = request.execute();

        List<Caption> results = response.getItems();
        String captionId = results.get(0).getId();
        System.out.println(captionId);
        return captionId;
    }
    public void downloadCaptions(String id, int i) throws GeneralSecurityException, IOException {
        YouTube youtubeService = getService();

        OutputStream output = new FileOutputStream("/Users/phoebe/Desktop/Fourth Year/Honours Project/newsroom/app/assets/youtube/captions-"+i+".txt" );

        // Define and execute the API request
        YouTube.Captions.Download request = youtubeService.captions()
                .download(id);
        request.getMediaHttpDownloader();
        request.executeMediaAndDownloadTo(output);
    }
}

