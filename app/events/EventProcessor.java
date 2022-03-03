package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import com.sapher.youtubedl.YoutubeDLException;
import structures.AppState;

import java.io.IOException;
import java.security.GeneralSecurityException;

public interface EventProcessor {

    /**
     * The processEvent method takes as input the contents of the event in the form of a
     * Jackson JsonNode object, which contains a set of key-value pairs (the information
     * about the event). It also takes in a copy of an ActorRef object, which can be used
     * to send commands back to the front-end, and a reference to the GameState class,
     * which as the name suggests can be used to hold game state information.
     * @param message
     * @return
     */
    public void processEvent(ActorRef out, AppState siteState, JsonNode message) throws IOException, YoutubeDLException, GeneralSecurityException;

}
