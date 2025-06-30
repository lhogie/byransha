package byransha.web.endpoint;

import byransha.BBGraph;
import byransha.BNode;
import byransha.User;
import byransha.web.EndpointJsonResponse;
import byransha.web.ErrorResponse;
import byransha.web.NodeEndpoint;
import byransha.web.WebServer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.sun.net.httpserver.HttpsExchange;
import java.util.Base64;
import java.io.InputStream;
import java.net.URL;

public class LoadImage extends NodeEndpoint<BNode> {

    @Override
    public String whatItDoes() {
        return "Load image endpoint for navigating to a target node.";
    }

    public LoadImage(BBGraph g) {
        super(g);
    }

    public LoadImage(BBGraph g, int id) {
        super(g, id);
    }

    @Override
    public EndpointJsonResponse exec(ObjectNode in, User user, WebServer webServer, HttpsExchange exchange, BNode node)
            throws Throwable {
        System.out.println("loadImageEndpoints");
        // load an image from internet url to base 64

        String imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/3/3a/Cat03.jpg/1200px-Cat03.jpg";
        System.out.println("Image URL: " + imageUrl);

        var l =  new ObjectNode(null);
        try (InputStream inputStream = new URL(imageUrl).openStream()) {
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
