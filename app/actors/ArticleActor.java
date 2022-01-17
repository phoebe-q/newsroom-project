package actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import events.EventProcessor;
import play.libs.Json;
import structures.AppState;

import java.util.HashMap;
import java.util.Map;

public class ArticleActor extends AbstractActor {

    public static Props props(ActorRef out) {
        return Props.create(ArticleActor.class, out);
    }
    private Map<String, EventProcessor> eventProcessors;
    private ObjectMapper mapper = new ObjectMapper();
    private AppState appState; // A class that can be used to hold game state information

    private final ActorRef out;

    /**
     * Constructor for the WebSocketActor. This is called by the HomeController when the websocket
     * connection to the front-end is established.
     * @param out
     */
    public ArticleActor(ActorRef out) {

        this.out = out;
        eventProcessors = new HashMap<String,EventProcessor>();

        // Initalize a new game state object
        appState = new AppState();
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(JsonNode.class, message -> {
                    System.out.println(message);
                    try {
                        ObjectNode readyMessage = Json.newObject();
                        readyMessage.put("messagetype", message.get("messagetype").asText());
                        if(message.get("messagetype").asText().equals("searchTerm")) {
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
            System.err.println("WebSocketActor: Received unknown event type "+messageType);
        } else {
            System.out.println("message type is " + messageType);
            processor.processEvent(out, appState, message); // process the event
        }
    }
}
