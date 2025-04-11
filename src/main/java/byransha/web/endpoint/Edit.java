package byransha.web.endpoint;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpsExchange;

import byransha.BBGraph;
import byransha.BNode;
import byransha.User;
import byransha.ValuedNode;
import byransha.web.EndpointJsonResponse;
import byransha.web.NodeEndpoint;
import byransha.web.WebServer;


public class Edit extends NodeEndpoint<BNode> {

    @Override
    public String whatIsThis() {
        return "Endpoint to edit every value accessible via the current node";
    }

    public Edit(BBGraph g) {
        super(g);
    }

    @Override
    public EndpointJsonResponse exec(ObjectNode in, User user, WebServer webServer, HttpsExchange exchange, BNode currentNode) throws Throwable {

        if (currentNode instanceof ValuedNode<?>) {
            System.out.println("current Node (id="+ currentNode.id()+") is editable, type="+ currentNode.getClass());
        }else{
            //System.out.println("valeur non editable");
            for (BNode node : currentNode.outs().values()) {
                if(node instanceof ValuedNode<?>){

                    System.out.println("Node with id:"+node.id()+" is Editable,type="+node.getClass()+" from current node(id="+ currentNode.id()+")");
                }
                //System.out.println("Node descrip : " + node.getDescription());

            }
        }


        //System.out.println("id de currentNode:"+ currentNode.id());

        var a = new ArrayNode(null);
        var b = new ObjectNode(null);
        b.set("Test",new IntNode(currentNode.id()));
        a.add(b);
        return new EndpointJsonResponse(a,"response for edit");
    }
}
