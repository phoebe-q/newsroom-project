package structures;

public class TopicSubtitle {
    int topicId;
    Subtitle topicSubtitle;

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
