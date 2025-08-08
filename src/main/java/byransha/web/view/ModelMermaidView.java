package byransha.web.view;

import byransha.BBGraph;
import byransha.BNode;
import byransha.User;
import byransha.web.Endpoint;
import byransha.web.EndpointTextResponse;
import byransha.web.NodeEndpoint;
import byransha.web.TechnicalView;
import byransha.web.WebServer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpsExchange;
import java.lang.reflect.Modifier;
import java.util.function.Predicate;
import lmu.AssociationRelation;
import lmu.Attribute;
import lmu.Entity;
import lmu.InheritanceRelation;
import lmu.MermaidWriter;
import lmu.Model;
import lmu.Visibility;
import lmu.WriterException;

public class ModelMermaidView
    extends NodeEndpoint<BBGraph>
    implements TechnicalView {

    public ModelMermaidView(BBGraph db) {
        super(db);
    }


    @Override
    public boolean sendContentByDefault() {
        return false;
    }

    @Override
    public String whatItDoes() {
        return "mermaid-based representations of the model in the graph";
    }

    @Override
    public EndpointTextResponse exec(
        ObjectNode in,
        User u,
        WebServer webServer,
        HttpsExchange exchange,
        BBGraph db
    ) throws Throwable {
        var mermaid = toMermaid(
            db,
            c ->
                BNode.class.isAssignableFrom(c) &&
                !Endpoint.class.isAssignableFrom(c)
        );
        return new EndpointTextResponse("text/mermaid", pw ->
            pw.print(mermaid)
        );
    }

    public static String toMermaid(BBGraph g, Predicate<Class> nodeFilter) {
        var model = new Model();

        // Build entities from graph nodes
        g.forEachNode(n -> {
            Entity childEntity = null;

            for (
                Class c = n.getClass();
                c != null && nodeFilter.test(c);
                c = c.getSuperclass()
            ) {
                var entityName = c.getSimpleName();
                var e = model.findEntity(et -> et.getName().equals(entityName));

                if (e == null) {
                    e = new Entity();
                    e.setName(entityName);
                    e.target = c;
                    model.addEntity(e);

                    // Add attributes and methods
                    addClassMembers(e, c, model);
                }

                var ee = e;
                var childEntitye = childEntity;

                // Add inheritance relationships
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

        // Add field-based associations
        for (var e : model.entities) {
            if (
                e.target == null ||
                !BNode.class.isAssignableFrom((Class) e.target)
            ) {
                continue; // Skip entities that are not BNode subclasses
            }
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

                if (childEntity != null) {
                    var r = new AssociationRelation(e, childEntity);
                    model.addRelation(r);
                }
            }
        }

        try {
            return new String(new MermaidWriter().writeModel(model));
        } catch (WriterException e1) {
            throw new IllegalStateException(e1);
        }
    }

    private static void addClassMembers(
        Entity entity,
        Class<?> clazz,
        Model model
    ) {
        // Add fields as attributes
        for (var field : clazz.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            var attribute = new Attribute();
            attribute.setName(field.getName());

            // Find or create type entity
            String typeName = getSimpleTypeName(field.getType());
            Entity typeEntity = findOrCreateTypeEntity(model, typeName);
            attribute.setType(typeEntity);

            attribute.setVisibility(getVisibility(field.getModifiers()));
            attribute.setClassStatic(Modifier.isStatic(field.getModifiers()));

            entity.getAttributes().add(attribute);
        }
    }

    private static Entity findOrCreateTypeEntity(Model model, String typeName) {
        // First try to find existing entity
        Entity typeEntity = model.findEntity(e -> e.getName().equals(typeName));

        if (typeEntity == null) {
            // Create new type entity
            typeEntity = new Entity();
            typeEntity.setName(typeName);
            typeEntity.setPrimitive(isPrimitiveType(typeName));
            typeEntity.setVisible(false); // Hide all type entities that aren't actual BNode classes
            model.addEntity(typeEntity);
        }

        return typeEntity;
    }

    private static boolean isPrimitiveType(String typeName) {
        return (
            typeName.equals("int") ||
            typeName.equals("long") ||
            typeName.equals("double") ||
            typeName.equals("float") ||
            typeName.equals("boolean") ||
            typeName.equals("char") ||
            typeName.equals("byte") ||
            typeName.equals("short") ||
            typeName.equals("void") ||
            typeName.equals("String") ||
            typeName.equals("Object")
        );
    }

    private static String getSimpleTypeName(Class<?> type) {
        if (type.isArray()) {
            return getSimpleTypeName(type.getComponentType()) + "[]";
        }
        return type.getSimpleName();
    }

    private static Visibility getVisibility(int modifiers) {
        if (Modifier.isPublic(modifiers)) {
            return Visibility.PUBLIC;
        } else if (Modifier.isProtected(modifiers)) {
            return Visibility.PROTECTED;
        } else if (Modifier.isPrivate(modifiers)) {
            return Visibility.PRIVATE;
        } else {
            return Visibility.DEFAULT; // package-private
        }
    }
}
