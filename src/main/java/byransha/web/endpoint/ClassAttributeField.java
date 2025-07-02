package byransha.web.endpoint;

import byransha.*;
import byransha.annotations.*;
import byransha.web.EndpointJsonResponse;
import byransha.web.NodeEndpoint;
import byransha.web.View;
import byransha.web.WebServer;
import com.fasterxml.jackson.databind.node.*;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.sun.net.httpserver.HttpsExchange;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.stream.Collectors;

public class ClassAttributeField extends NodeEndpoint<BNode> implements View {

    @Override
    public String whatItDoes() {
        return "List the out of the current node.";
    }

    public ClassAttributeField(BBGraph g) {
        super(g);
    }

    public ClassAttributeField(BBGraph g, int id) {
        super(g, id);
    }

    private Field findField(Class<?> clazz, String name) {
        Class<?> current = clazz;
        while (current != null && current != Object.class) {
            try {
                Field field = current.getDeclaredField(name);
                field.setAccessible(true);
                return field;
            } catch (NoSuchFieldException e) {
                // Not in current class, try superclass
            }
            current = current.getSuperclass();
        }
        return null;
    }


    @Override
    public EndpointJsonResponse exec(ObjectNode in, User user, WebServer webServer, HttpsExchange exchange, BNode node)
            throws Throwable {
        var a = new ArrayNode(null);
        node.forEachOut((name, out) -> {
            if(!out.deleted) {
                var b = new ObjectNode(null);
                b.set("id", new IntNode(out.id()));
                b.set("name", new TextNode(name));
                b.set("type", new TextNode(out.getClass().getSimpleName()));
                if (out instanceof ValuedNode<?> vn) {
                    if (vn.get() == null) {
                        b.set("value", NullNode.getInstance());
                    } else if (vn instanceof byransha.BooleanNode) {
                        b.set("value", BooleanNode.valueOf((Boolean) vn.get()));
                    } else if (vn instanceof byransha.IntNode) {
                        b.set("value", new IntNode((Integer) vn.get()));
                    } else {
                        b.set("value", new TextNode(vn.getAsString()));
                    }

                    b.set("mimeType", new TextNode(vn.getMimeType()));
                }
                if (out instanceof ListNode<?> ln) {
                    b.set("canAddNewNode", BooleanNode.valueOf(ln.canAddNewNode()));
                    b.set("isDropdown", BooleanNode.valueOf(ln.isDropdown()));
                } else if (out instanceof SetNode<?> sn) {
                    b.set("canAddNewNode", BooleanNode.valueOf(sn.canAddNewNode()));
                    b.set("isDropdown", BooleanNode.valueOf(sn.isDropdown()));
                } else if (out instanceof DropdownNode<?> dn) {
                    b.set("canAddNewNode", BooleanNode.valueOf(dn.canAddNewNode()));
                    b.set("isDropdown", BooleanNode.valueOf(dn.isDropdown()));
                }

                if (out instanceof ListNode<?> || out instanceof SetNode<?> || out instanceof DropdownNode<?>) {
                    try {
                        Field field = findField(node.getClass(), name);
                        if (field != null) {
                            var genericType = field.getGenericType();
                            if (genericType instanceof ParameterizedType parameterizedType) {
                                var actualType = parameterizedType.getActualTypeArguments()[0];
                                b.set("listNodeType", new TextNode(actualType.getTypeName()));
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (out instanceof RadioNode<?> radioNode) {
                    try {
                        b.set("options", JsonNodeFactory.instance.arrayNode().addAll(
                                radioNode.getOptions().stream()
                                        .map(option -> option == null ? NullNode.getInstance() : new TextNode(option.toString()))
                                        .collect(Collectors.toList())));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                Field field = findField(node.getClass(), name);
                if (field != null) {
                    var validations = new ObjectNode(null);
                    if (field.isAnnotationPresent(Required.class)) {
                        validations.set("required", BooleanNode.valueOf(true));
                    }
                    if (field.isAnnotationPresent(Min.class)) {
                        validations.set("min", new DoubleNode(field.getAnnotation(Min.class).value()));
                    }
                    if (field.isAnnotationPresent(Max.class)) {
                        validations.set("max", new DoubleNode(field.getAnnotation(Max.class).value()));
                    }
                    if (field.isAnnotationPresent(Size.class)) {
                        var size = field.getAnnotation(Size.class);
                        var sizeInfo = new ObjectNode(null);
                        sizeInfo.set("min", new IntNode(size.min()));
                        sizeInfo.set("max", new IntNode(size.max()));
                        validations.set("size", sizeInfo);
                    }
                    if (field.isAnnotationPresent(Pattern.class)) {
                        validations.set("pattern", new TextNode(field.getAnnotation(Pattern.class).regex()));
                    }
                    if (validations.size() > 0) {
                        b.set("validations", validations);
                    }
                }

                a.add(b);
            }
        });

        return new EndpointJsonResponse(a, node.prettyName()+'@'+node.id());
    }

    @Override
    public boolean sendContentByDefault() {
        return false;
    }
}