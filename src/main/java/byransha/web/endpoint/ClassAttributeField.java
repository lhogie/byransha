package byransha.web.endpoint;

import byransha.*;
import byransha.annotations.*;
import byransha.labmodel.model.v0.BusinessNode;
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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ClassAttributeField extends NodeEndpoint<BNode> implements View {
    private static final Map<String, Field> fieldCache = new ConcurrentHashMap<>();
    
    private static final JsonNodeFactory nodeFactory = JsonNodeFactory.instance;
    
    private static final NullNode NULL_NODE = NullNode.getInstance();
    private static final BooleanNode TRUE_NODE = BooleanNode.TRUE;
    private static final BooleanNode FALSE_NODE = BooleanNode.FALSE;

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
        String cacheKey = clazz.getName() + "#" + name;
        
        Field cachedField = fieldCache.get(cacheKey);
        if (cachedField != null) {
            return cachedField;
        }
        
        Class<?> current = clazz;
        while (current != null && current != Object.class) {
            try {
                Field field = current.getDeclaredField(name);
                field.setAccessible(true);
                fieldCache.put(cacheKey, field);
                return field;
            } catch (NoSuchFieldException e) {
                // Not in current class, try superclass
            }
            current = current.getSuperclass();
        }
        
        fieldCache.put(cacheKey, null);
        return null;
    }


    @Override
    public EndpointJsonResponse exec(ObjectNode in, User user, WebServer webServer, HttpsExchange exchange, BNode node)
            throws Throwable {
        var a = nodeFactory.arrayNode();

        var currentNodeInformation = nodeFactory.objectNode();
        var currentNodeInfo = nodeFactory.objectNode();
        currentNodeInfo.set("id", nodeFactory.numberNode(node.id()));
        currentNodeInfo.set("name", nodeFactory.textNode(node.prettyName()));
        currentNodeInfo.set("type", nodeFactory.textNode(node.getClass().getSimpleName()));
        if (node instanceof BusinessNode businessNode) {
            currentNodeInfo.set("isValid", businessNode.isValid() ? TRUE_NODE : FALSE_NODE);
        }
        if (node instanceof ValuedNode<?> valuedNode) {
            if (valuedNode.get() == null) {
                currentNodeInfo.set("value", NULL_NODE);
            } else if (valuedNode instanceof byransha.BooleanNode) {
                Boolean value = (Boolean) valuedNode.get();
                currentNodeInfo.set("value", value ? TRUE_NODE : FALSE_NODE);
            } else if (valuedNode instanceof byransha.IntNode) {
                currentNodeInfo.set("value", nodeFactory.numberNode((Integer) valuedNode.get()));
            } else {
                currentNodeInfo.set("value", nodeFactory.textNode(valuedNode.getAsString()));
            }
            currentNodeInfo.set("mimeType", nodeFactory.textNode(valuedNode.getMimeType()));
        }

        currentNodeInformation.set("currentNode", currentNodeInfo);

        node.forEachOut((name, out) -> {
            if(!out.deleted) {
                var b = nodeFactory.objectNode();
                b.set("id", nodeFactory.numberNode(out.id()));
                b.set("name", nodeFactory.textNode(name));
                b.set("type", nodeFactory.textNode(out.getClass().getSimpleName()));

                if (out instanceof BusinessNode bn) {
                    b.set("isValid", bn.isValid() ? TRUE_NODE : FALSE_NODE);
                }

                if (out instanceof ValuedNode<?> vn) {
                    if (vn.get() == null) {
                        b.set("value", NULL_NODE);
                    } else if (vn instanceof byransha.BooleanNode) {
                        Boolean value = (Boolean) vn.get();
                        b.set("value", value ? TRUE_NODE : FALSE_NODE);
                    } else if (vn instanceof byransha.IntNode) {
                        b.set("value", nodeFactory.numberNode((Integer) vn.get()));
                    } else {
                        b.set("value", nodeFactory.textNode(vn.getAsString()));
                    }

                    b.set("mimeType", nodeFactory.textNode(vn.getMimeType()));
                }

                if (out instanceof ListNode<?> ln) {
                    b.set("canAddNewNode", ln.canAddNewNode() ? TRUE_NODE : FALSE_NODE);
                    b.set("isDropdown", ln.isDropdown() ? TRUE_NODE : FALSE_NODE);
                } else if (out instanceof SetNode<?> sn) {
                    b.set("canAddNewNode", sn.canAddNewNode() ? TRUE_NODE : FALSE_NODE);
                    b.set("isDropdown", sn.isDropdown() ? TRUE_NODE : FALSE_NODE);
                } else if (out instanceof DropdownNode<?> dn) {
                    b.set("canAddNewNode", dn.canAddNewNode() ? TRUE_NODE : FALSE_NODE);
                    b.set("isDropdown", dn.isDropdown() ? TRUE_NODE : FALSE_NODE);
                }

                Field field = findField(node.getClass(), name);
                
                if ((out instanceof ListNode<?> || out instanceof SetNode<?> || out instanceof DropdownNode<?>) && field != null) {
                    try {
                        var genericType = field.getGenericType();
                        if (genericType instanceof ParameterizedType parameterizedType) {
                            var actualType = parameterizedType.getActualTypeArguments()[0];
                            b.set("listNodeType", nodeFactory.textNode(actualType.getTypeName()));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (out instanceof RadioNode<?> radioNode) {
                    try {
                        ArrayNode optionsArray = nodeFactory.arrayNode();
                        for (Object option : radioNode.getOptions()) {
                            optionsArray.add(option == null ? NULL_NODE : nodeFactory.textNode(option.toString()));
                        }
                        b.set("options", optionsArray);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (field != null) {
                    ObjectNode validations = nodeFactory.objectNode();
                    if (field.isAnnotationPresent(Required.class)) {
                        validations.set("required", TRUE_NODE);
                    }
                    if (field.isAnnotationPresent(Min.class)) {
                        validations.set("min", nodeFactory.numberNode(field.getAnnotation(Min.class).value()));
                    }
                    if (field.isAnnotationPresent(Max.class)) {
                        validations.set("max", nodeFactory.numberNode(field.getAnnotation(Max.class).value()));
                    }
                    if (field.isAnnotationPresent(Size.class)) {
                        var size = field.getAnnotation(Size.class);
                        var sizeInfo = nodeFactory.objectNode();
                        sizeInfo.set("min", nodeFactory.numberNode(size.min()));
                        sizeInfo.set("max", nodeFactory.numberNode(size.max()));
                        validations.set("size", sizeInfo);
                    }
                    if (field.isAnnotationPresent(Pattern.class)) {
                        validations.set("pattern", nodeFactory.textNode(field.getAnnotation(Pattern.class).regex()));
                    }
                    if (validations.size() > 0) {
                        b.set("validations", validations);
                    }
                }

                a.add(b);
            }
        });

        currentNodeInformation.set("attributes", a);

        StringBuilder idBuilder = new StringBuilder(node.prettyName());
        idBuilder.append('@').append(node.id());
        
        return new EndpointJsonResponse(currentNodeInformation, idBuilder.toString());
    }

    @Override
    public boolean sendContentByDefault() {
        return false;
    }
}