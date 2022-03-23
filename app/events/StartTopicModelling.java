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
import structures.AppState;
import structures.Subtitle;
import structures.TopicArticle;
import structures.TopicSubtitle;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import java.util.regex.Pattern;

public class StartTopicModelling implements EventProcessor{
    @Override
    public void processEvent(ActorRef out, AppState siteState, JsonNode message) throws IOException {
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
        // Begin by importing documents from text to feature sequences
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

        return model;
    }

    public List<TopicArticle> topicSortedText(List<WPArticle> results, AppState siteState) throws IOException {
        List<String> textList = new ArrayList<>();
        for(WPArticle result: results){
            textList.add(Jsoup.parse(result.contents).text());
            System.out.println("Results contents : " + result.title + "\n");
        }
        //InstanceList instances = returnInstances(textList);
        //ParallelTopicModel model = getModel(instances);
        // The data alphabet maps word IDs to strings

        // its not getting the correct dist because it is only doing it from topic articles I think
        List<TopicArticle> topicSortedText = new ArrayList<>();
        TopicInferencer inferencer = siteState.createdModel.getInferencer();
        for(int i = 0; i < textList.size(); i++) {
            //double[] topicDistribution = siteState.createdModel.getTopicProbabilities(i);

            //TopicInferencer inferencer = siteState.createdModel.getInferencer();
            double[] topicDistribution = inferencer.getSampledDistribution(siteState.instances.get(i), 10, 1, 5);

            double topDistribution = 0;
            int listNumber = 0;
            for (int i2 = 0; i2 < topicDistribution.length; i2++) {
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

    public List<TopicSubtitle> topicSortedSubtitles(List<Subtitle> captionsList, AppState siteState, int resultsLen) throws IOException {
        List<String> textList = new ArrayList<>();
        for(Subtitle sub: captionsList) {
            textList.add(sub.getSubtitleText());
        }

        InstanceList instances = returnInstances(textList);
        TopicInferencer inferencer = siteState.createdModel.getInferencer();

        List<TopicSubtitle> topicSortedSubtitles = new ArrayList<>();
        for(int i = 0; i < textList.size(); i++) {
            //double[] topicDistribution = model.getTopicProbabilities(i);
            double[] topicDistribution = inferencer.getSampledDistribution(siteState.instances.get(resultsLen+i), 10, 1, 5);

            double topDistribution = 0;
            int listNumber = 0;
            for (int i2 = 0; i2 < topicDistribution.length; i2++) {
                if (topicDistribution[i2] > topDistribution) {
                    topDistribution = topicDistribution[i2];
                    listNumber = i2 + 1;
                }
            }
            //Subtitle subtitle = new Subtitle((captionsList.get(i)).getVideoId(), (captionsList.get(i)).getVideoTitle(), textList.get(i));
            //TopicSubtitle topicStruct = new TopicSubtitle(listNumber, subtitle);
            TopicSubtitle topicStruct = new TopicSubtitle(listNumber, captionsList.get(i));
            topicSortedSubtitles.add(topicStruct);
        }
        return topicSortedSubtitles;
    }

    public List<List<String>> topicWordsList(List<String> textList, AppState siteState) throws IOException {
        //InstanceList instances = returnInstances(textList);
        siteState.instances = returnInstances(textList);
        siteState.createdModel = getModel(siteState.instances);

        Alphabet dataAlphabet = siteState.instances.getDataAlphabet();
        ArrayList<TreeSet<IDSorter>> topicSortedWords = siteState.createdModel.getSortedWords();
        List<List<String>> topicWordsList = new ArrayList<>();
        int numTopics = 5;
        // Show top 5 words in topics with proportions for the first document
        for (int topic = 0; topic < numTopics; topic++) {
            Iterator<IDSorter> iterator = topicSortedWords.get(topic).iterator();
            ArrayList<String> topicWords = new ArrayList<>();

            int rank = 0;
            while (iterator.hasNext() && rank < 5) {
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
