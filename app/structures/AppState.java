package structures;

import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.types.Alphabet;
import cc.mallet.types.IDSorter;
import cc.mallet.types.InstanceList;
import models.WPArticle;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class AppState {

    public List<WPArticle> newsResults;
    public List<Subtitle> youtubeResults;
    public List<List<String>> modelledTopics;
    public List<TopicArticle> sortedNewsResults;
    public List<TopicSubtitle> sortedYoutubeResults;
    public ParallelTopicModel createdModel;
    public InstanceList instances;

}
