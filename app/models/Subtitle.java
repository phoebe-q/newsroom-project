package models;

public class Subtitle {
    String videoId; // id of the video
    String videoTitle; // title of the video
    String subtitleText; // subtitles from the video

    public Subtitle(String videoId, String videoTitle, String subtitleText) {
        this.videoId = videoId;
        this.videoTitle = videoTitle;
        this.subtitleText = subtitleText;
    }

    public String getVideoId() {
        return videoId;
    }
    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getVideoTitle() {
        return videoTitle;
    }
    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }

    public String getSubtitleText() {
        return subtitleText;
    }
    public void setSubtitleText(String subtitleText) {
        this.subtitleText = subtitleText;
    }
}
