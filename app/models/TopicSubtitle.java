package models;

public class TopicSubtitle {
    int topicId; // index corresponding to the topic the subtitles are sorted into
    Subtitle topicSubtitle; // subtitle instance

    public TopicSubtitle(int topicId, Subtitle topicSubtitle) {
        this.topicId = topicId;
        this.topicSubtitle = topicSubtitle;
    }

    public int getTopicId() {
        return topicId;
    }
    public void setTopicId(int topicId) {
        this.topicId = topicId;
    }

    public Subtitle getTopicSubtitle() { return topicSubtitle; }
    public void setTopicSubtitle(Subtitle topicSubtitle) {
        this.topicSubtitle = topicSubtitle;
    }
}
