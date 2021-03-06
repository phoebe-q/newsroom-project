package events;

import akka.actor.ActorRef;
import com.fasterxml.jackson.databind.JsonNode;
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
import play.libs.Json;
import structures.AppState;
import structures.Subtitle;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.*;

public class SearchYoutube implements EventProcessor{
    @Override
    public void processEvent(ActorRef out, AppState siteState, JsonNode message) throws IOException, YoutubeDLException, GeneralSecurityException {
        {
            ObjectNode alert = Json.newObject();
            alert.put("messagetype", "alert");
            alert.put("text", "Searching Youtube...");
            out.tell(alert, out);}

        String queryTerms = message.get("searchTerm").asText();

        List<Subtitle> youtubeResults = searchYT(queryTerms);
        siteState.youtubeResults = youtubeResults;

        {
            ObjectNode alert = Json.newObject();
            alert.put("messagetype", "alert");
            alert.put("text", "Youtube Search Complete");
            out.tell(alert, out);}
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

    /**
     * Build and return an authorized API client service.
     *
     * @return an authorized API client service
     * @throws GeneralSecurityException, IOException
     */
    public static YouTube getService() throws GeneralSecurityException, IOException {
        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        System.out.println("httpTransport: " + httpTransport);
        Credential credential = authorize(httpTransport);
        return new YouTube.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    /**
     * Call function to create API service object. Define and
     * execute API request. Print API response.
     *
     * @throws GeneralSecurityException, IOException, GoogleJsonResponseException
     */
    public List<Subtitle> searchYT(String searchTerm) throws GeneralSecurityException, IOException, GoogleJsonResponseException, YoutubeDLException {
        YouTube youtubeService = getService();
        List<String> snippet = Arrays.asList("id,snippet");
        List<String> video = Arrays.asList("video");

        // Define and execute the API request
        YouTube.Search.List request = youtubeService.search()
                .list(snippet);
        SearchListResponse response = request.setChannelId("UC16niRr50-MSBwiO3YDb3RA") //channel id for BBCNews
                .setMaxResults(25L)
                .setQ(searchTerm)
                .setType(video)
                .setVideoCaption("closedCaption")
                .setVideoDimension("2d")
                .setVideoEmbeddable("true")
                .execute();
        List<SearchResult> results = response.getItems();

        int i = 1;
        List<Subtitle> captionsList = new ArrayList<>();
        for(SearchResult result: results){
            ResourceId resourceId = result.getId();
            String videoId = resourceId.getVideoId();
            String videoTitle = result.getSnippet().getTitle();

            Subtitle sub = downloadCaptions(videoId, videoTitle, i);
            captionsList.add(sub);
            i++;
        }
        //List<String> captionsList = parseVtt();
        return captionsList;
    }

    public Subtitle downloadCaptions(String id, String videoTitle, int i) throws GeneralSecurityException, IOException, YoutubeDLException {
        // Video url to download
        String videoUrl = "https://www.youtube.com/watch?v=" + id;
        // Destination directory
        String directory = "/Users/phoebe/Desktop/Fourth Year/Honours Project/newsroom/app/assets/youtube/";
        YoutubeDL.setExecutablePath("/usr/local/Cellar/youtube-dl/2021.12.17/libexec/bin/youtube-dl");

        // Build request
        YoutubeDLRequest request = new YoutubeDLRequest(videoUrl, directory);
        request.setOption("all-subs");		// --write-sub
        request.setOption("skip-download");	// --skip-download
        request.setOption("sub-lang", "en"); // --sub-lang en
        request.setOption("output", ""+i); // --output specifies file to output subs to

        // Make request and return response
        YoutubeDLResponse response = YoutubeDL.execute(request);

        Subtitle sub = new Subtitle(id, videoTitle, parseVtt(i));
        return sub;
        // Response
        //String stdOut = response.getOut(); // Executable output
        //System.out.println("stdOut" + stdOut);

    }

    public String parseVtt(int i) throws IOException {
        String vtt_dir = "/Users/phoebe/Desktop/Fourth Year/Honours Project/newsroom/app/assets/youtube";
        String captions = "";

        StringBuilder stringBuilder = new StringBuilder();
        File subFile = new File(vtt_dir + "/" + i + ".en.vtt");
        if (!subFile.exists()) {
            subFile = new File(vtt_dir + "/" + i + ".en-GB.vtt");
        }
        if (subFile.exists()) {
            Scanner r = new Scanner(subFile);
            for (int i2 = 0; i2 < 3; i2++) {
                r.nextLine();
            }
            while (r.hasNextLine()) {
                String data = r.nextLine() + " ";
                if (!data.matches("^([0-9]+\n|)([0-9:,->\s]+)")) {
                    stringBuilder.append(data);
                }
            }
            r.close();
            captions = stringBuilder.toString();
        }
        return captions;
    }
}
