package actors;

import akka.actor.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import events.EventProcessor;
import events.Crawl;
import play.libs.Json;
import structures.AppState;

public class WebSocketActor extends AbstractActor {

    public static Props props(ActorRef out) {
        return Props.create(WebSocketActor.class, out);
    }
    private Map<String,EventProcessor> eventProcessors;
    private ObjectMapper mapper = new ObjectMapper();
    private AppState appState; // A class that can be used to hold game state information

    private final ActorRef out;

    /**
     * Constructor for the WebSocketActor. This is called by the HomeController when the websocket
     * connection to the front-end is established.
     * @param out
     */
    public WebSocketActor(ActorRef out) {

        this.out = out;
        eventProcessors = new HashMap<String,EventProcessor>();
        eventProcessors.put("url", new Crawl());

        // Initalize a new game state object
        appState = new AppState();

        try {
            System.out.println("try statement in WSA constructor");
            ObjectNode readyMessage = Json.newObject();
            readyMessage.put("messagetype", "default");
            out.tell(readyMessage, out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(JsonNode.class, message -> {
                    System.out.println(message);
                    try {
                        ObjectNode readyMessage = Json.newObject();
                        readyMessage.put("messagetype", message.get("messagetype").asText());
                        if(message.get("messagetype").asText().equals("searchTerm") || message.get("messagetype").asText().equals("yt-searchTerm")) {
                            readyMessage.put("searchTerm", message.get("searchTerm").asText());
                        } else if (message.get("messagetype").asText().equals("viewResult")){
                            readyMessage.put("result", message.get("result"));
                        }
                        processMessage(message.get("messagetype").asText(), message);
                        out.tell(readyMessage, out);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).build();
    }

    public void processMessage(String messageType, JsonNode message) throws Exception{

        EventProcessor processor = eventProcessors.get(messageType);
        if (processor==null) {
            // Unknown event type received
            System.err.println("WebSocketActor: Recieved unknown event type "+messageType);
        } else {
            System.out.println("message type is " + messageType);
            processor.processEvent(out, appState, message); // process the event
        }
    }
}
