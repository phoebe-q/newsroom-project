package events;

import akka.actor.ActorRef;
import cc.mallet.pipe.*;
import cc.mallet.pipe.iterator.ArrayIterator;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.topics.TopicInferencer;
import cc.mallet.types.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import models.WPArticle;
import org.jsoup.Jsoup;
import play.libs.Json;
import structures.SiteState;
import models.Subtitle;
import models.TopicArticle;
import models.TopicSubtitle;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import java.util.regex.Pattern;

public class StartTopicModelling implements EventProcessor{
    @Override
    public void processEvent(ActorRef out, SiteState siteState, JsonNode message) throws IOException {
        {ObjectNode alert = Json.newObject();
            alert.put("messagetype", "alert");
            alert.put("text", "Modelling Topics...");
            out.tell(alert, out);}


        List<String> topicModellingText = new ArrayList<>();
        List<WPArticle> results = siteState.newsResults;
        List<Subtitle> subtitlesList = siteState.youtubeResults;
        int resultsLen = results.size();
        for(WPArticle result: results){
            topicModellingText.add(Jsoup.parse(result.contents).text());
        }
        for(Subtitle sub: subtitlesList) {
            topicModellingText.add(sub.getSubtitleText());
        }


        siteState.modelledTopics = topicWordsList(topicModellingText, siteState);
        siteState.sortedNewsResults = topicSortedText(results, siteState);
        siteState.sortedYoutubeResults = topicSortedSubtitles(subtitlesList, siteState, resultsLen);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode topics = mapper.valueToTree(siteState.modelledTopics);
        JsonNode news = mapper.valueToTree(siteState.sortedNewsResults);
        JsonNode subtitles = mapper.valueToTree(siteState.sortedYoutubeResults);
        {ObjectNode alert = Json.newObject();
            alert.put("messagetype", "alert");
            alert.put("text", "Modelling Topics Finished...");
            out.tell(alert, out);}
        {ObjectNode m = Json.newObject();
            m.put("messagetype", "modellingComplete");
            m.put("topics", topics);
            m.put("newsData", news);
            m.put("subtitlesData", subtitles);
            out.tell(m, out);}
    }

    public InstanceList returnInstances(List<String> textList) {
        ArrayList<Pipe> pipeList = new ArrayList<>();

        // Pipes: lowercase, tokenize, remove stopwords, map to features
        pipeList.add( new CharSequenceLowercase() );
        pipeList.add( new CharSequence2TokenSequence(Pattern.compile("\\p{L}[\\p{L}\\p{P}]+\\p{L}")) );
        pipeList.add( new TokenSequenceRemoveStopwords(new File("/Users/phoebe/Desktop/Fourth Year/Honours Project/newsroom/app/stoplists/en.txt"), "UTF-8", false, false, false) );
        pipeList.add( new TokenSequence2FeatureSequence() );

        InstanceList instances = new InstanceList (new SerialPipes(pipeList));
        instances.addThruPipe(new ArrayIterator(textList));
        return instances;
    }

    public ParallelTopicModel getModel(InstanceList instances) throws IOException {
        // Create a model with 5 topics, alpha_t = 0.01, beta_w = 0.01
        int numTopics = 5;
        ParallelTopicModel model = new ParallelTopicModel(numTopics, 1.0, 0.01);

        // adding all text
        model.addInstances(instances);

        // using two parallel samplers to divide the corpus in two
        model.setNumThreads(2);

        // iterate 1000 times
        model.setNumIterations(1000);

        model.estimate();

        return model;
    }

    public List<TopicArticle> topicSortedText(List<WPArticle> results, SiteState siteState) throws IOException {
        List<String> textList = new ArrayList<>();
        for(WPArticle result: results){
            textList.add(Jsoup.parse(result.contents).text());
            System.out.println("Results contents : " + result.title + "\n");
        }

        List<TopicArticle> topicSortedText = new ArrayList<>();
        TopicInferencer inferencer = siteState.createdModel.getInferencer();
        for(int i = 0; i < textList.size(); i++) {
            // sampled distribution - used to infer which topic text is most similar to
            double[] topicDistribution = inferencer.getSampledDistribution(siteState.instances.get(i), 10, 1, 5);

            double topDistribution = 0;
            int listNumber = 0;
            for (int i2 = 0; i2 < topicDistribution.length; i2++) { // finding highest value and assigning index of topic based on that
                if (topicDistribution[i2] > topDistribution) {
                    topDistribution = topicDistribution[i2];
                    listNumber = i2 + 1;
                }
            }
            TopicArticle topicStruct = new TopicArticle(listNumber, results.get(i));

            topicSortedText.add(topicStruct);
        }
        return topicSortedText;
    }

    public List<TopicSubtitle> topicSortedSubtitles(List<Subtitle> captionsList, SiteState siteState, int resultsLen) throws IOException {
        List<String> textList = new ArrayList<>();
        for(Subtitle sub: captionsList) {
            textList.add(sub.getSubtitleText());
        }

        InstanceList instances = returnInstances(textList);
        TopicInferencer inferencer = siteState.createdModel.getInferencer();

        List<TopicSubtitle> topicSortedSubtitles = new ArrayList<>();
        for(int i = 0; i < textList.size(); i++) {
            double[] topicDistribution = inferencer.getSampledDistribution(siteState.instances.get(resultsLen+i), 10, 1, 5);

            double topDistribution = 0;
            int listNumber = 0;
            for (int i2 = 0; i2 < topicDistribution.length; i2++) { // finding highest value and assigning index of topic based on that
                if (topicDistribution[i2] > topDistribution) {
                    topDistribution = topicDistribution[i2];
                    listNumber = i2 + 1;
                }
            }
            TopicSubtitle topicStruct = new TopicSubtitle(listNumber, captionsList.get(i));
            topicSortedSubtitles.add(topicStruct);
        }
        return topicSortedSubtitles;
    }

    public List<List<String>> topicWordsList(List<String> textList, SiteState siteState) throws IOException {
        siteState.instances = returnInstances(textList);
        siteState.createdModel = getModel(siteState.instances);

        Alphabet dataAlphabet = siteState.instances.getDataAlphabet();
        ArrayList<TreeSet<IDSorter>> topicSortedWords = siteState.createdModel.getSortedWords();
        List<List<String>> topicWordsList = new ArrayList<>();
        int numTopics = 5;
        for (int topic = 0; topic < numTopics; topic++) {
            Iterator<IDSorter> iterator = topicSortedWords.get(topic).iterator();
            ArrayList<String> topicWords = new ArrayList<>();

            int rank = 0;
            while (iterator.hasNext() && rank < 5) { // retrieves top 5 words in topic
                IDSorter idCountPair = iterator.next();
                topicWords.add((dataAlphabet.lookupObject(idCountPair.getID())).toString());
                //out.format("%s (%.0f) ", dataAlphabet.lookupObject(idCountPair.getID()), idCountPair.getWeight());
                rank++;
            }
            topicWordsList.add(topicWords);
        }
        return topicWordsList;
    }
}
