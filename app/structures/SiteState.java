package structures;

import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.types.InstanceList;
import models.Subtitle;
import models.TopicArticle;
import models.TopicSubtitle;
import models.WPArticle;

import java.util.List;

// holds the state of the variables needed in the system
public class SiteState {

    public List<WPArticle> newsResults;
    public List<Subtitle> youtubeResults;
    public List<List<String>> modelledTopics;
    public List<TopicArticle> sortedNewsResults;
    public List<TopicSubtitle> sortedYoutubeResults;
    public ParallelTopicModel createdModel;
    public InstanceList instances;

}
