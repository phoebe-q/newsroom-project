package models;

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

    public WPArticle getArticle() {
        return article;
    }
    public void setArticle(WPArticle article) {
        this.article = article;
    }
}
