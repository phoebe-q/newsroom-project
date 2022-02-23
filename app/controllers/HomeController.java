package controllers;

import actors.WebSocketActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.stream.Materializer;
import models.Article;
import models.WPArticle;
import models.WPArticleIn;
import play.libs.streams.ActorFlow;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.WebSocket;
import structures.TopicArticle;
import structures.TopicSubtitle;

import javax.inject.Inject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static play.libs.Scala.asScala;


/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {
    private final ActorSystem actorSystem;
    private final Materializer materializer;


    @Inject
    public HomeController(ActorSystem actorSystem, Materializer materializer) {
        this.actorSystem = actorSystem;
        this.materializer = materializer;
    }

    public Result index(Http.Request request) throws IOException {
        return ok(views.html.index.render(request));
    }

    public Result resultsView(Http.Request request, List<List<String>> topics, List<TopicArticle> topicSortedText, List<TopicSubtitle> topicSortedSubtitles) {
        return ok(views.html.results.render(request, topics, topicSortedText, topicSortedSubtitles));
    }

    public WebSocket socket() {

        return WebSocket.Json.accept(
                request -> ActorFlow.actorRef(this::createWebSocketActor, actorSystem, materializer));
    }

    public Props createWebSocketActor(ActorRef out) {
        return Props.create(WebSocketActor.class, out); // calls the constructor for Game Actor
    }

}
