package byransha.web.endpoint;

import byransha.BBGraph;
import byransha.nodes.BNode;
import byransha.nodes.system.User;
import byransha.web.EndpointJsonResponse;
import byransha.web.ErrorResponse;
import byransha.web.NodeEndpoint;
import byransha.web.WebServer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.sun.net.httpserver.HttpsExchange;

import java.net.URI;
import java.util.Base64;

public class LoadImage extends NodeEndpoint<BNode> {



    public LoadImage(BBGraph g) {
        super(g);
        endOfConstructor();
    }

    @Override
    public String whatItDoes() {
        return "Load image endpoint for navigating to a target node.";
    }
    @Override
    public EndpointJsonResponse exec(ObjectNode in, User user, WebServer webServer, HttpsExchange exchange, BNode node)
            throws Throwable {
        System.out.println("loadImageEndpoints");
        // load an image from internet url to base 64

        String imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/3/3a/Cat03.jpg/1200px-Cat03.jpg";
        System.out.println("Image URL: " + imageUrl);

        var l =  new ObjectNode(null);
        try (var inputStream = new URI(imageUrl).toURL().openStream()) {
            byte[] imageBytes = inputStream.readAllBytes();
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);
            l.set("base64Image", new TextNode(base64Image));
            return new EndpointJsonResponse(l, this);
        } catch (Exception e) {
            e.printStackTrace();
            return ErrorResponse.serverError("Failed to load or encode the image: " + e.getMessage());
        }
    }
}
