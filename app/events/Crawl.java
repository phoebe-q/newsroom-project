package events;

import akka.actor.ActorRef;
import com.fasterxml.jackson.databind.JsonNode;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import play.mvc.Result;
import structures.AppState;

import java.io.IOException;

import static play.mvc.Results.ok;

public class Crawl implements EventProcessor{
    @Override
    public void processEvent(ActorRef out, AppState gameState, JsonNode message) {
        try {
            var url = message.get("url").asText();
            Connection connection = Jsoup.connect(url);
            Document websiteJson = connection.get();

            if(websiteJson != null && connection.response().statusCode() == 200) {
                System.out.println("WE ARE IN EVENT");
                System.out.println(websiteJson.title());
            } else {
                System.out.println("Request returned null");
            }
        }
        catch(IOException e) {
            System.out.println("Error: " + e);
        }
    }
}
