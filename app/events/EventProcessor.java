package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import com.sapher.youtubedl.YoutubeDLException;
import structures.SiteState;

import java.io.IOException;
import java.security.GeneralSecurityException;

public interface EventProcessor {

    public void processEvent(ActorRef out, SiteState siteState, JsonNode message) throws IOException, YoutubeDLException, GeneralSecurityException;

}
