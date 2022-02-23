package structures;

import models.WPArticle;

public class TopicArticle {
    int topicId;
    WPArticle article;

    public TopicArticle(int topicId, WPArticle article) {
        this.topicId = topicId;
        this.article = article;
    }

    public int getTopicId() {
        return topicId;
    }
    public void setTopicId(int topicId) {
        this.topicId = topicId;
    }

    public WPArticle getTopicText() {
        return article;
    }
    public void setTopicText(WPArticle article) {
        this.article = article;
    }
}
