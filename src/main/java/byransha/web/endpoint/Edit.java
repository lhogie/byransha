package byransha.web.endpoint;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
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

    public Edit(BBGraph g, int id) {
        super(g, id);
    }

    @Override
    public EndpointJsonResponse exec(ObjectNode in, User user, WebServer webServer, HttpsExchange exchange, BNode currentNode) throws Throwable {

        var a = new ArrayNode(null);


        if (currentNode instanceof ValuedNode<?>) {
            System.out.println("current Node (id="+ currentNode.id()+") is editable, type="+ currentNode.getClass());
            var b = new ObjectNode(null);
            b.set("Id_editable", new IntNode( currentNode.id()));

            b.set("type",new TextNode(currentNode.getClass().getSimpleName()));
            a.add(b);
        }else{
            //System.out.println("valeur non editable");
            //System.out.println("Classe de curent node ");
            //currentNode.getFields(currentNode.id());
            System.out.println("entery set "+currentNode.outs().keySet());

            for(String key : currentNode.outs().keySet()) {
                var b = new ObjectNode(null);
                BNode node = currentNode.outs().get(key);
                System.out.println("Noeud de nom "+key+" Valeur "+currentNode.outs().get(key));

                //A rajouter apres implementation des permisions
                //if(currentNode.outs().get(key).canEdit(user)){}
                if(node instanceof ValuedNode<?>){
                    //System.out.println("Node with id:"+node.id()+" is Editable,type="+node.getClass()+" from current node(id="+ currentNode.id()+")");
                    //System.out.println(node.getClass());
                    //node.getFields(node.id());
                    b.set("linked_Node_Editable_id", new IntNode(node.id()));
                    b.set("linked_Node_Editable_name",new TextNode(key));
                    b.set("linked_Node_Editable_type", new TextNode(node.getClass().getSimpleName()));
                    b.set("val:", new TextNode( ((ValuedNode) node).toString())) ;
                    a.add(b);
                }
            }



//            for (BNode node : currentNode.outs().values()) {
//                System.out.println("Outs: "+ node);
//
//                //System.out.println("Class of node: "+node.getClass() + node.getClass().getName());
//                if(node instanceof ValuedNode<?>){
//                    var b = new ObjectNode(null);
//                    //System.out.println("Node with id:"+node.id()+" is Editable,type="+node.getClass()+" from current node(id="+ currentNode.id()+")");
//                    //System.out.println(node.getClass());
//                    //node.getFields(node.id());
//                    b.set("Id_linked_Node_Editable_id", new IntNode(node.id()));
//                    b.set("list_linked_Node_Editable_type", new TextNode(node.getClass().getSimpleName()));
//                    b.set("val:", (node.toJSONNode()));
//                    a.add(b);
//                }
                //System.out.println("Node descrip : " + node.getDescription());

            //}
        }


        //System.out.println("id de currentNode:"+ currentNode.id());
        return new EndpointJsonResponse(a,"response for edit");
    }
}
