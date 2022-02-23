package structures;

import models.WPArticle;

import java.util.List;

/**
 * This class can be used to hold information about the on-going game.
 * Its created with the GameActor.
 *
 * @author Dr. Richard McCreadie
 *
 */
public class AppState {

    public List<WPArticle> newsResults;
    public List<Subtitle> youtubeResults;
    public List<List<String>> modelledTopics;
    public List<TopicArticle> sortedNewsResults;
    public List<TopicSubtitle> sortedYoutubeResults;

}
