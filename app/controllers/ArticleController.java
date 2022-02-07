package controllers;

import com.alibaba.fastjson.JSON;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Content;
import models.WPArticle;
import models.WPArticleString;

import com.google.api.services.youtube.model.*;
import com.sapher.youtubedl.YoutubeDL;
import com.sapher.youtubedl.YoutubeDLException;
import com.sapher.youtubedl.YoutubeDLRequest;
import com.sapher.youtubedl.YoutubeDLResponse;
import org.apache.commons.io.FileUtils;

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
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.*;
import java.util.regex.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import cc.mallet.util.*;
import cc.mallet.types.*;
import cc.mallet.pipe.*;
import cc.mallet.pipe.iterator.*;
import cc.mallet.topics.*;

import models.Article;

public class ArticleController extends Controller {

    public Result index() {return null;}

    public Result indexDB() throws IOException {
        ClientConfiguration clientConfiguration =
                ClientConfiguration.builder().connectedTo("localhost:9200").withSocketTimeout(6000000).build();
        RestHighLevelClient client = RestClients.create(clientConfiguration).rest();
        BufferedReader reader = new BufferedReader(new FileReader("/Users/phoebe/Desktop/Fourth Year/Honours Project/TREC_Washington_Post_collection.v3.jl"));
        ObjectMapper objectMapper = new ObjectMapper();

        IndexRequest indexRequest = new IndexRequest("wpost-articles");
        for (int i =0; i < 2000; i++) {
            try {
                String line = reader.readLine();
                WPArticle article = objectMapper.readValue(line, WPArticle.class);

                XContentBuilder builder = XContentFactory.jsonBuilder()
                        .startObject()
                        .field("id", article.getId())
                        .field("article_url", article.getArticleURL())
                        .field("title", article.getTitle())
                        .field("author", article.getAuthor())
                        .field("published_date", article.getPublishedDate())
                        .field("contents", objectMapper.writeValueAsString(article.getContents()))
                        .field("type", article.getType())
                        .field("source", article.getSource())
                        .endObject();


                indexRequest.source(builder);


                IndexResponse response = client.index(indexRequest, RequestOptions.DEFAULT);
                System.out.println(response.getResult());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ok(views.html.articles.populated.render());
    }


    public Result search(Http.Request request, String searchTerm) throws Exception, IOException, GeneralSecurityException, YoutubeDLException {

        ClientConfiguration clientConfiguration =
                ClientConfiguration.builder().connectedTo("localhost:9200").withSocketTimeout(600000).build();
        RestHighLevelClient client = RestClients.create(clientConfiguration).rest();

        QueryBuilder query = QueryBuilders.matchQuery("title", searchTerm);
        SearchRequest searchRequest = new SearchRequest("wpost-articles");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(query);
        searchRequest.searchType(SearchType.DFS_QUERY_THEN_FETCH);
        searchRequest.source(searchSourceBuilder);
        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
        SearchHit[] searchHits = response.getHits().getHits();

        List<WPArticleString> resultsWithString =
                Arrays.stream(searchHits)
                        .map(hit -> JSON.parseObject(hit.getSourceAsString(), WPArticleString.class))
                        .collect(Collectors.toList());

        List<WPArticle> results = new ArrayList<>();
          ObjectMapper objectMapper = new ObjectMapper();
          TypeReference<List<Content>> contentType = new TypeReference<>() {};
          for (WPArticleString result: resultsWithString) {
              List<Content> contentsToArray = objectMapper.readValue(result.contents,  contentType);
              WPArticle article = new WPArticle(result.id, result.article_url, result.title, result.author, result.published_date, contentsToArray, result.type, result.source);
              results.add(article);
          }

        List<String> captions = searchYT(searchTerm);

        List<String> topicModellingText = new ArrayList<>();
        for (Article result: results) {
            topicModellingText.add(result.content);
        }
        topicModellingText.addAll(captions);
        ArrayList<ArrayList<String>> topicsListAll = topicModel(topicModellingText);

        ArrayList<ArrayList<String>> topicsList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            topicsList.add(topicsListAll.get(i));
        }
        ArrayList<String> topic1 = topicsListAll.get(3);
        ArrayList<String> topic2 = topicsListAll.get(4);
        ArrayList<String> topic3 = topicsListAll.get(5);
        ArrayList<String> topic4 = topicsListAll.get(6);
        ArrayList<String> topic5 = topicsListAll.get(7);

        return ok(views.html.results.render(results, topicsList, topic1, topic2, topic3, topic4, topic5));
    }

    // public Result resultView(String category, String title, String content) {
       // return ok(views.html.result.render(category, title, content));
    // }

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
    public List<String> searchYT(String searchTerm) throws GeneralSecurityException, IOException, GoogleJsonResponseException, YoutubeDLException {
        YouTube youtubeService = getService();
        List<String> snippet = Arrays.asList("id,snippet");
        List<String> video = Arrays.asList("video");

        // Define and execute the API request
        YouTube.Search.List request = youtubeService.search()
                .list(snippet);
        SearchListResponse response = request.setChannelId("UC16niRr50-MSBwiO3YDb3RA") //channel id for BBCNews
                .setMaxResults(5L)
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

            downloadCaptions(videoId, i);
            i++;
        }
        List<String> captionsList = parseVtt();
        return captionsList;
    }

    public void downloadCaptions(String id, int i) throws GeneralSecurityException, IOException, YoutubeDLException {
        // Video url to download
        String videoUrl = "https://www.youtube.com/watch?v=" + id;
        // Destination directory
        String directory = "/Users/phoebe/Desktop/Fourth Year/Honours Project/newsroom/app/assets/youtube/";
        YoutubeDL.setExecutablePath("/usr/local/Cellar/youtube-dl/2021.12.17/libexec/bin/youtube-dl");

        // Build request
        YoutubeDLRequest request = new YoutubeDLRequest(videoUrl, directory);
        request.setOption("all-subs");		// --write-sub
        request.setOption("skip-download");	// --skip-download
        request.setOption("sub-lang", "en"); // --sub-lang en
        request.setOption("output", ""+i); // --output specifies file to output subs to

        // Make request and return response
        YoutubeDLResponse response = YoutubeDL.execute(request);

        // Response
        String stdOut = response.getOut(); // Executable output
        System.out.println("stdOut" + stdOut);

    }

    public List<String> parseVtt() throws IOException {
        String vtt_dir = "/Users/phoebe/Desktop/Fourth Year/Honours Project/newsroom/app/assets/youtube";
        int dir_size = new File(vtt_dir).list().length;
        List<String> captionsList = new ArrayList<>();

        if (dir_size > 0) {
            for (int i = 1; i < dir_size + 1; i++) {
                StringBuilder stringBuilder = new StringBuilder();
                File subFile = new File(vtt_dir + "/" + i + ".en.vtt");
                if (!subFile.exists()) {
                    subFile = new File(vtt_dir + "/" + i + ".en-GB.vtt");
                }
                if (subFile.exists()) {
                    Scanner r = new Scanner(subFile);
                    for (int i2 = 0; i2 < 3; i2++) {
                        r.nextLine();
                    }
                    while (r.hasNextLine()) {
                        String data = r.nextLine() + " ";
                        if (!data.matches("^([0-9]+\n|)([0-9:,->\s]+)")) {
                            stringBuilder.append(data);
                        }
                    }
                    r.close();
                    String subtitlesText = stringBuilder.toString();

                    captionsList.add(subtitlesText);
                }
            }
        }
        FileUtils.cleanDirectory(new File(vtt_dir));
        return captionsList;
    }

   public ArrayList<ArrayList<String>> topicModel(List<String> textList) throws Exception {

        // Begin by importing documents from text to feature sequences
        ArrayList<Pipe> pipeList = new ArrayList<>();

        // Pipes: lowercase, tokenize, remove stopwords, map to features
        pipeList.add( new CharSequenceLowercase() );
        pipeList.add( new CharSequence2TokenSequence(Pattern.compile("\\p{L}[\\p{L}\\p{P}]+\\p{L}")) );
        pipeList.add( new TokenSequenceRemoveStopwords(new File("/Users/phoebe/Desktop/Fourth Year/Honours Project/newsroom/app/stoplists/en.txt"), "UTF-8", false, false, false) );
        pipeList.add( new TokenSequence2FeatureSequence() );

        InstanceList instances = new InstanceList (new SerialPipes(pipeList));
        instances.addThruPipe(new ArrayIterator (textList));

        // Create a model with 5 topics, alpha_t = 0.01, beta_w = 0.01
        //  Note that the first parameter is passed as the sum over topics, while
        //  the second is the parameter for a single dimension of the Dirichlet prior.
        int numTopics = 5;
        ParallelTopicModel model = new ParallelTopicModel(numTopics, 1.0, 0.01);

        model.addInstances(instances);

        // Use two parallel samplers, which each look at one half the corpus and combine
        //  statistics after every iteration.
        model.setNumThreads(2);

        // Run the model for 50 iterations and stop (this is for testing only,
        //  for real applications, use 1000 to 2000 iterations)
        model.setNumIterations(1000);
        model.estimate();

        // Show the words and topics in the first instance

        // The data alphabet maps word IDs to strings
        Alphabet dataAlphabet = instances.getDataAlphabet();

        FeatureSequence tokens = (FeatureSequence) model.getData().get(0).instance.getData();
        LabelSequence topics = model.getData().get(0).topicSequence;

        Formatter out = new Formatter(new StringBuilder(), Locale.US);
        for (int position = 0; position < tokens.getLength(); position++) {
            out.format("%s-%d ", dataAlphabet.lookupObject(tokens.getIndexAtPosition(position)), topics.getIndexAtPosition(position));
        }
        //System.out.println("first out print:" + out);

        ArrayList<String> topic1 = new ArrayList<>();
        ArrayList<String> topic2 = new ArrayList<>();
        ArrayList<String> topic3 = new ArrayList<>();
        ArrayList<String> topic4 = new ArrayList<>();
        ArrayList<String> topic5 = new ArrayList<>();
        // Estimate the topic distribution of the first instance,
        //  given the current Gibbs state.
        for(int i = 0; i < textList.size(); i++) {
            double[] topicDistribution = model.getTopicProbabilities(i);

            double topDistribution = 0;
            int listNumber = 0;
            for (int i2 = 0; i2 < topicDistribution.length; i2++) {
                if (topicDistribution[i2] > topDistribution) {
                    topDistribution = topicDistribution[i2];
                    listNumber = i2 + 1;
                }
            }

            switch(listNumber) {
                case 1:
                    topic1.add(textList.get(i));
                case 2:
                    topic2.add(textList.get(i));
                case 3:
                    topic3.add(textList.get(i));
                case 4:
                    topic4.add(textList.get(i));
                case 5:
                    topic5.add(textList.get(i));
            }
        }

        // Get an array of sorted sets of word ID/count pairs
        ArrayList<TreeSet<IDSorter>> topicSortedWords = model.getSortedWords();

        ArrayList<ArrayList<String>> topicsList = new ArrayList<>();
        // Show top 5 words in topics with proportions for the first document
        for (int topic = 0; topic < numTopics; topic++) {
            Iterator<IDSorter> iterator = topicSortedWords.get(topic).iterator();
            ArrayList<String> topicWords = new ArrayList<>();
            //out = new Formatter(new StringBuilder(), Locale.US);
            //out.format("%d\t%.3f\t", topic, topicDistribution[topic]);
            int rank = 0;
            while (iterator.hasNext() && rank < 5) {
                IDSorter idCountPair = iterator.next();
                topicWords.add((dataAlphabet.lookupObject(idCountPair.getID())).toString());
                //out.format("%s (%.0f) ", dataAlphabet.lookupObject(idCountPair.getID()), idCountPair.getWeight());
                rank++;
            }
            topicsList.add(topicWords);
            //System.out.println(topicsList);
        }

        // Create a new instance with high probability of topic 0
        StringBuilder topicZeroText = new StringBuilder();
        Iterator<IDSorter> iterator = topicSortedWords.get(0).iterator();

        int rank = 0;
        while (iterator.hasNext() && rank < 5) {
            IDSorter idCountPair = iterator.next();
            topicZeroText.append(dataAlphabet.lookupObject(idCountPair.getID()) + " ");
            rank++;
        }

        // Create a new instance named "test instance" with empty target and source fields.
        InstanceList testing = new InstanceList(instances.getPipe());
        testing.addThruPipe(new Instance(topicZeroText.toString(), null, "test instance", null));

        TopicInferencer inferencer = model.getInferencer();
        double[] testProbabilities = inferencer.getSampledDistribution(testing.get(0), 10, 1, 5);
        //System.out.println("0\t" + testProbabilities[0]);
        topicsList.add(topic1);
        topicsList.add(topic2);
        topicsList.add(topic3);
        topicsList.add(topic4);
        topicsList.add(topic5);

        return topicsList;
    }
}

