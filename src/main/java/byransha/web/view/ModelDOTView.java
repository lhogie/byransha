package byransha.web.view;

import byransha.BBGraph;
import byransha.BNode;
import byransha.ListNode;
import byransha.User;
import byransha.ValuedNode;
import byransha.web.DevelopmentView;
import byransha.web.EndpointTextResponse;
import byransha.web.NodeEndpoint;
import byransha.web.WebServer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpsExchange;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.function.Predicate;
import lmu.AssociationRelation;
import lmu.DotWriter;
import lmu.Entity;
import lmu.InheritanceRelation;
import lmu.Model;
import lmu.WriterException;

public class ModelDOTView
    extends NodeEndpoint<BBGraph>
    implements DevelopmentView {

    @Override
    public String whatItDoes() {
        return "ModelDOTView provides a DOT representation of the graph.";
    }

    public ModelDOTView(BBGraph db) {
        super(db);
        endOfConstructor();
    }



    @Override
    public boolean sendContentByDefault() {
        return false;
    }

    public static int id(Class n) {
        return Math.abs(n.getName().hashCode());
    }

    @Override
    public EndpointTextResponse exec(
        ObjectNode input,
        User user,
        WebServer webServer,
        HttpsExchange exchange,
        BBGraph g
    ) throws Throwable {
        return new EndpointTextResponse("text/dot", pw ->
            pw.print(toDot(g, c -> true))
        );
    }

    public static String toDot(BBGraph g, Predicate<Class> nodeFilter) {
        var model = new Model();

        g.forEachNode(n -> {
            Entity childEntity = null;

            for (
                Class c = n.getClass();
                c != null && nodeFilter.test(c);
                c = c.getSuperclass()
            ) {
                var entityName = c.getSimpleName();
                var e = model.findEntity(et -> et.getName().equals(entityName));

                if (
                    e == null // entity NOT yet in the model
                ) {
                    e = new Entity();
                    e.setName(entityName);
                    e.target = c;
                    model.addEntity(e);
                }

                var ee = e;
                var childEntitye = childEntity;

                if (
                    childEntity != null &&
                    model
                        .getRelations()
                        .stream()
                        .filter(
                            r ->
                                r instanceof InheritanceRelation ir &&
                                ir.getSuperEntity() == ee &&
                                ir.getSubEntity() == childEntitye
                        )
                        .findFirst()
                        .isEmpty()
                ) {
                    model.addRelation(new InheritanceRelation(childEntity, e));
                }

                childEntity = e;
            }
        });

        if (false) for (var e : model.entities) {
            for (var f : ((Class) e.target).getDeclaredFields()) {
                if (
                    Modifier.isStatic(f.getModifiers()) ||
                    !BNode.class.isAssignableFrom(f.getType())
                ) {
                    continue;
                }

                f.setAccessible(true);

                var childEntity = model.findEntity(
                    et -> et.target == f.getType()
                );

                var r = new AssociationRelation(e, childEntity);
                model.addRelation(r);

                boolean isList = ListNode.class.isAssignableFrom(f.getType());

                Class<? extends BNode> targetType = null;

                if (isList) {
                    if (f.getGenericType() instanceof ParameterizedType pt) {
                        if (pt.getActualTypeArguments().length == 1) {
                            targetType = ((Class<
                                    ?
                                >) pt.getActualTypeArguments()[0]).asSubclass(
                                BNode.class
                            );
                        } else {
                            throw new IllegalStateException();
                        }
                    } else {
                        throw new IllegalStateException();
                    }
                } else {
                    targetType = f.getType().asSubclass(BNode.class);
                }

                r.setCardinality(isList ? "*" : "");
                if (ValuedNode.class.isAssignableFrom(targetType)) {
                    var a = new lmu.Attribute();
                    a.setName(f.getName());
                    //a.setType(f.getType());
                }
            }
        }
        try {
            return new String(new DotWriter().writeModel(model));
        } catch (WriterException e1) {
            throw new IllegalStateException(e1);
        }
    }
}
