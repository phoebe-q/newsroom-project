package actors;

import akka.actor.*;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import events.EventProcessor;
import events.SearchYoutube;
import events.StartSearch;
import events.StartTopicModelling;
import play.libs.Json;
import structures.SiteState;

public class WebSocketActor extends AbstractActor {

    public static Props props(ActorRef out) {
        return Props.create(WebSocketActor.class, out);
    }
    private Map<String,EventProcessor> eventProcessors;
    private SiteState siteState;

    private final ActorRef out;

    public WebSocketActor(ActorRef out) {

        this.out = out;
        eventProcessors = new HashMap<String,EventProcessor>();
        eventProcessors.put("startSearch", new StartSearch());
        eventProcessors.put("searchYoutube", new SearchYoutube());
        eventProcessors.put("topicModelStart", new StartTopicModelling());

        siteState = new SiteState();

        try {
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
                    try {
                        processMessage(message.get("messagetype").asText(), message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).build();
    }

    public void processMessage(String messageType, JsonNode message) throws Exception{

        EventProcessor processor = eventProcessors.get(messageType);
        if (processor==null) {
            // if event type is unknown
            System.err.println("WebSocketActor: Received unknown event type "+messageType);
        } else {
            System.out.println("message type is " + messageType);
            processor.processEvent(out, siteState, message); // process the event
        }
    }
}
