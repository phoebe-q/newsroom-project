package controllers;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.WPArticle;
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

import cc.mallet.util.*;
import cc.mallet.types.*;
import cc.mallet.pipe.*;
import cc.mallet.pipe.iterator.*;
import cc.mallet.topics.*;

import java.util.*;
import java.util.regex.*;
import java.io.*;

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
                ClientConfiguration.builder().connectedTo("localhost:9200").withSocketTimeout(6000000).build();
        RestHighLevelClient client = RestClients.create(clientConfiguration).rest();
        BufferedReader reader = new BufferedReader(new FileReader("/Users/phoebe/Desktop/Fourth Year/Honours Project/TREC_Washington_Post_collection.v3.jl"));
        ObjectMapper objectMapper = new ObjectMapper();

        IndexRequest indexRequest = new IndexRequest("wj-articles");
        while ((reader.readLine()) != null) {
            try {
                String line = reader.readLine();
                //JsonNode inputJSON = objectMapper.readTree(line);
                WPArticle article = objectMapper.readValue(line, WPArticle.class);

                XContentBuilder builder = XContentFactory.jsonBuilder()
                        .startObject()
                        .field("id", article.getId())
                        .field("article_url", article.getArticleURL())
                        .field("title", article.getTitle())
                        .field("author", article.getAuthor())
                        .field("published_date", article.getPublishedDate())
                        .field("contents", article.getContents())
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
        // List<String> categories = Arrays.asList("business", "entertainment", "politics", "tech");

        /* for(String category: categories) {
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
                    //while (myReader.hasNextLine()) {
                      //  String data = myReader.nextLine() + " ";
                      //  stringBuilder.append(data);
                    //}
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
         */
        return ok(views.html.articles.populated.render());
    }

    public Result search(String searchTerm) throws Exception {
        ClientConfiguration clientConfiguration =
                ClientConfiguration.builder().connectedTo("localhost:9200").withSocketTimeout(600000).build();
        RestHighLevelClient client = RestClients.create(clientConfiguration).rest();

        QueryBuilder query = QueryBuilders.matchQuery("title", searchTerm);
        SearchRequest searchRequest = new SearchRequest("wj-articles");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(query);
        searchRequest.searchType(SearchType.DFS_QUERY_THEN_FETCH);
        searchRequest.source(searchSourceBuilder);
        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
        SearchHit[] searchHits = response.getHits().getHits();
        List<WPArticle> results =
                Arrays.stream(searchHits)
                        .map(hit -> JSON.parseObject(hit.getSourceAsString(), WPArticle.class))
                        .collect(Collectors.toList());

        String pathToFile = "/Users/phoebe/Desktop/Fourth Year/Honours Project/newsroom/searchResults.txt";

        for (WPArticle result: results) {
            System.out.println("result is " + result);
           /* try {
                FileWriter myWriter = new FileWriter(pathToFile);
                myWriter.write(result.title + "\t" + result.category + "\t" + result.content + "\n");
                myWriter.close();
                System.out.println("Successfully wrote to the file.");
            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }

            */
        }
        return null;

        //topicModel(pathToFile);

       /* File searchResultsFile = new File(pathToFile);
        if (searchResultsFile.delete()) {
            System.out.println("Deleted the file: " + searchResultsFile.getName());
        } else {
            System.out.println("Failed to delete the file.");
        }

        */

       // return ok(views.html.results.render(request, results));
    }

    public Result resultView(String category, String title, String content) {
        return ok(views.html.result.render(category, title, content));
    }

    public void topicModel(String pathToFile) throws Exception {

        // Begin by importing documents from text to feature sequences
        ArrayList<Pipe> pipeList = new ArrayList<Pipe>();

        // Pipes: lowercase, tokenize, remove stopwords, map to features
        pipeList.add( new CharSequenceLowercase() );
        pipeList.add( new CharSequence2TokenSequence(Pattern.compile("\\p{L}[\\p{L}\\p{P}]+\\p{L}")) );
        pipeList.add( new TokenSequenceRemoveStopwords(new File("/Users/phoebe/Desktop/Fourth Year/Honours Project/newsroom/app/stoplists/en.txt"), "UTF-8", false, false, false) );
        pipeList.add( new TokenSequence2FeatureSequence() );

        InstanceList instances = new InstanceList (new SerialPipes(pipeList));

        Reader fileReader = new InputStreamReader(new FileInputStream(new File(pathToFile)), "UTF-8");
        instances.addThruPipe(new CsvIterator (fileReader, Pattern.compile("^(\\S*)[\\s,]*(\\S*)[\\s,]*(.*)$"),
                3, 2, 1)); // data, label, name fields

        // Create a model with 100 topics, alpha_t = 0.01, beta_w = 0.01
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
        System.out.println("first out print:" + out);

        // Estimate the topic distribution of the first instance,
        //  given the current Gibbs state.
        double[] topicDistribution = model.getTopicProbabilities(0);

        // Get an array of sorted sets of word ID/count pairs
        ArrayList<TreeSet<IDSorter>> topicSortedWords = model.getSortedWords();

        // Show top 5 words in topics with proportions for the first document
        for (int topic = 0; topic < numTopics; topic++) {
            Iterator<IDSorter> iterator = topicSortedWords.get(topic).iterator();

            out = new Formatter(new StringBuilder(), Locale.US);
            out.format("%d\t%.3f\t", topic, topicDistribution[topic]);
            int rank = 0;
            while (iterator.hasNext() && rank < 5) {
                IDSorter idCountPair = iterator.next();
                out.format("%s (%.0f) ", dataAlphabet.lookupObject(idCountPair.getID()), idCountPair.getWeight());
                rank++;
            }
            System.out.println("second out print:" + out);
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
        System.out.println("0\t" + testProbabilities[0]);
    }
}
