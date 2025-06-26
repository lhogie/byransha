package byransha.web.endpoint;

import byransha.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.sun.net.httpserver.HttpsExchange;

import byransha.web.EndpointJsonResponse;
import byransha.web.NodeEndpoint;
import byransha.web.WebServer;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Iterator;
import java.util.Map;

public class SetValue extends NodeEndpoint<BNode> {
    @Override
    public String whatItDoes() {
        return "modify the value of valued nodes";
    }

    public SetValue(BBGraph g) {
        super(g);
    }

    public SetValue(BBGraph g, int id) {
        super(g, id);
    }

    @Override
    public EndpointJsonResponse exec(ObjectNode in, User user, WebServer webServer, HttpsExchange exchange,
                                     BNode target) throws Throwable {

        var a = new ObjectNode(null);

        if (!in.isEmpty()) {
            int id = in.get("id").asInt();
            var node = graph.findByID(id);
            a.set("id", new IntNode(node.id()));
            a.set("name", new TextNode(node.prettyName()));
            a.set("type", new TextNode(node.getClass().getSimpleName()));

            var value = in.get("value");

            if (node instanceof StringNode sn) {
                sn.set(value.asText());
                a.set("value", new TextNode(value.asText()));
            } else if (node instanceof byransha.IntNode i) {
                i.set(value.asInt());
                a.set("value", new IntNode(value.asInt()));
            } else if (node instanceof byransha.BooleanNode b) {
                b.set(value.asBoolean());
                a.set("value", value.booleanValue() ? BooleanNode.TRUE : BooleanNode.FALSE);
            } else if (node instanceof ImageNode im) {
                String base64Image = value.asText();
                byte[] data = Base64.getDecoder().decode(base64Image);
                String mimeType = "image/png";
                if (base64Image.startsWith("data:image/jpeg;base64,")) {
                    mimeType = "image/jpeg";
                } else if (base64Image.startsWith("data:image/gif;base64,")) {
                    mimeType = "image/gif";
                } else if (base64Image.startsWith("data:image/svg+xml;base64,")) {
                    mimeType = "image/svg+xml";
                }

                im.set(data);
                im.setMimeType(mimeType);
                a.set("value", new TextNode(im.get().toString()));
            } else if(node instanceof FileNode fn){
                System.out.println("value" + value + " : " + value.asText());
                String base64File = value.asText();
                byte[] data = Base64.getDecoder().decode(base64File);
                String mimeType = "application/octet-stream";
                if (base64File.startsWith("data:application/pdf;base64,")) {
                    mimeType = "application/pdf";
                } else if (base64File.startsWith("data:text/plain;base64,")) {
                    mimeType = "text/plain";
                }
                System.out.println("data" + data);
                fn.set(data);
                fn.setMimeType(mimeType);
                a.set("value", new TextNode(fn.get().toString()));
            }

            in.removeAll();
        }

        return new EndpointJsonResponse(a, "Setting the value");
    }
}
