package models;

import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.types.InstanceList;

import java.util.List;

public class AppState {

    public List<WPArticle> newsResults;
    public List<Subtitle> youtubeResults;
    public List<List<String>> modelledTopics;
    public List<TopicArticle> sortedNewsResults;
    public List<TopicSubtitle> sortedYoutubeResults;
    public ParallelTopicModel createdModel;
    public InstanceList instances;

}
