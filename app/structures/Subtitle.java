package structures;

public class Subtitle {
    String videoId;
    String subtitleText;

    public Subtitle(String videoId, String subtitleText) {
        this.videoId = videoId;
        this.subtitleText = subtitleText;
    }

    public String getVideoId() {
        return videoId;
    }
    public void setVideoId(String topicId) {
        this.videoId = videoId;
    }

    public String getSubtitleText() {
        return subtitleText;
    }
    public void setSubtitleText(String subtitleText) {
        this.subtitleText = subtitleText;
    }
}
