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

    public Subtitle getTopicText() {
        return topicSubtitle;
    }
    public void setTopicText(Subtitle topicSubtitle) {
        this.topicSubtitle = topicSubtitle;
    }
}
