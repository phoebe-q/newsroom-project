package events;

import akka.actor.ActorRef;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.sapher.youtubedl.YoutubeDL;
import com.sapher.youtubedl.YoutubeDLException;
import com.sapher.youtubedl.YoutubeDLRequest;
import com.sapher.youtubedl.YoutubeDLResponse;
import org.apache.commons.io.FileUtils;
import play.libs.Json;
import structures.SiteState;
import models.Subtitle;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.*;

public class SearchYoutube implements EventProcessor{
    @Override
    public void processEvent(ActorRef out, SiteState siteState, JsonNode message) throws IOException, YoutubeDLException, GeneralSecurityException {
        {
            ObjectNode alert = Json.newObject();
            alert.put("messagetype", "alert");
            alert.put("text", "Searching Youtube...");
            out.tell(alert, out);
        }

        ObjectMapper mapper = new ObjectMapper();

        String queryTerms = message.get("searchTerm").asText();

        List<Subtitle> youtubeResults = searchYT(queryTerms);
        siteState.youtubeResults = youtubeResults;
        JsonNode subtitles = mapper.valueToTree(siteState.youtubeResults);
        {
            ObjectNode alert = Json.newObject();
            alert.put("messagetype", "alert");
            alert.put("text", "Youtube Search Complete");
            out.tell(alert, out);
        }
        if (siteState.newsResults != null && siteState.youtubeResults != null) {
            {ObjectNode m = Json.newObject();
                m.put("messagetype", "searchComplete");
                out.tell(m, out);}
        }
    }

    private static final String CLIENT_SECRETS= "/Users/phoebe/Desktop/Fourth Year/Honours Project/newsroom/client_secret.json";
    private static final Collection<String> SCOPES = Arrays.asList("https://www.googleapis.com/auth/youtube.force-ssl");
    private static final String APPLICATION_NAME = "NewsRoom";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    /**
     * Create an authorized Credential object.
     *
     * @return an authorized Credential object.
     * @throws IOException
     */
    public static Credential authorize(final NetHttpTransport httpTransport) throws IOException {
        // Load client secrets.
        FileInputStream in = new FileInputStream(new File(CLIENT_SECRETS));
        //InputStream in = YoutubeController.class.getResourceAsStream(CLIENT_SECRETS);
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
                        .build();
        Credential credential =
                new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("phoebe.quinn04@gmail.com");
        return credential;
    }

    public static YouTube getService() throws GeneralSecurityException, IOException {
        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

        Credential credential = authorize(httpTransport);
        return new YouTube.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public List<Subtitle> searchYT(String searchTerm) throws GeneralSecurityException, IOException, GoogleJsonResponseException, YoutubeDLException {
        // Clear directory of previous subtitles
        FileUtils.cleanDirectory(new File("/Users/phoebe/Desktop/Fourth Year/Honours Project/newsroom/app/assets/youtube/"));

        YouTube youtubeService = getService();
        List<String> snippet = Arrays.asList("id,snippet");
        List<String> video = Arrays.asList("video");


        // Define and execute the API request
        YouTube.Search.List request = youtubeService.search()
                .list(snippet);
        SearchListResponse response = request.setChannelId("UCHd62-u_v4DvJ8TCFtpi4GA") //channel id for Washington Post
                .setMaxResults(5L) // five videos
                .setQ(searchTerm)
                .setType(video)
                .setVideoCaption("closedCaption") // only videos with closed captions
                .setVideoDimension("2d")
                .setVideoEmbeddable("true")
                .execute();
        List<SearchResult> results = new ArrayList<>(response.getItems());

        List<Subtitle> captionsList = new ArrayList<>();
        for (SearchResult result : results) {
            ResourceId resourceId = result.getId();
            String videoId = resourceId.getVideoId();
            String videoTitle = result.getSnippet().getTitle();

            Subtitle sub = downloadCaptions(videoId, videoTitle);
            captionsList.add(sub);
        }

        return captionsList;
    }

    public Subtitle downloadCaptions(String id, String videoTitle) throws GeneralSecurityException, IOException, YoutubeDLException {
        // YouTube url to download
        String videoUrl = "https://www.youtube.com/watch?v=" + id;

        // Destination directory
        String directory = "/Users/phoebe/Desktop/Fourth Year/Honours Project/newsroom/app/assets/youtube/";
        YoutubeDL.setExecutablePath("/usr/local/Cellar/youtube-dl/2021.12.17/libexec/bin/youtube-dl");

        // Request building
        YoutubeDLRequest request = new YoutubeDLRequest(videoUrl, directory);
        request.setOption("all-subs");		// download all subtitles
        request.setOption("skip-download");	// don't download video
        request.setOption("sub-lang", "en"); // subtitle language is english
        request.setOption("output", ""+id); // --output specifies file to output subtitles to

        // Make request and return response
        YoutubeDLResponse response = YoutubeDL.execute(request);

        Subtitle sub = new Subtitle(id, videoTitle, parseVtt(id));
        return sub;
    }

    public String parseVtt(String id) throws IOException {
        String vtt_dir = "/Users/phoebe/Desktop/Fourth Year/Honours Project/newsroom/app/assets/youtube";
        String captions = "";

        StringBuilder stringBuilder = new StringBuilder();
        File subFile = new File(vtt_dir + "/" + id + ".en.vtt"); // these if check if various formats of data have been saved
        if (!subFile.exists()) {
            subFile = new File(vtt_dir + "/" + id + ".und.vtt");
        }
        if (!subFile.exists()) {
            subFile = new File(vtt_dir + "/" + id + ".en-GB.vtt");
        }
        if (!subFile.exists()) {
            subFile = new File(vtt_dir + "/" + id + ".en-US.vtt");
        }
        if (subFile.exists()) {
            Scanner r = new Scanner(subFile);
            for (int count = 0; count < 3; count++) { // remove header and language
                r.nextLine();
            }
            while (r.hasNextLine()) { // parses text to remove unneeded items like tags and timings
                String data = r.nextLine() + " ";
                if (!data.matches("^([0-9]+\n|)([0-9:,->\s]+)") && !data.matches("^([0-9]+|)([0-9:,->s]+) --> ([0-9]+|)([0-9:,->s]+) ([a-zA-Z]{4}:[0-9]+%)")) {
                    data = data.replaceAll("&[a-zA-Z]+;[a-zA-Z]+;.*", "");
                    data = data.replaceAll("&[a-zA-Z]+;.*", "");
                    data = data.replaceAll("^-", "");
                    stringBuilder.append(data);
                }
            }
            r.close();
            captions = stringBuilder.toString();
        }
        return captions;
    }
}
