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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public class ClassAttributeField extends NodeEndpoint<BNode> implements View {

    // Cache for field metadata to avoid repeated reflection
    private static final ConcurrentMap<
        String,
        FieldMetadata
    > fieldMetadataCache = new ConcurrentHashMap<>();

    private static class FieldMetadata {

        final Field field;
        final boolean hasRequired;
        final double minValue;
        final double maxValue;
        final boolean hasMin;
        final boolean hasMax;
        final int sizeMin;
        final int sizeMax;
        final boolean hasSize;
        final String pattern;
        final boolean hasPattern;
        final String genericType;

        FieldMetadata(Field field) {
            this.field = field;
            this.hasRequired = field.isAnnotationPresent(Required.class);
            this.hasMin = field.isAnnotationPresent(Min.class);
            this.minValue = hasMin ? field.getAnnotation(Min.class).value() : 0;
            this.hasMax = field.isAnnotationPresent(Max.class);
            this.maxValue = hasMax ? field.getAnnotation(Max.class).value() : 0;
            this.hasSize = field.isAnnotationPresent(Size.class);
            if (hasSize) {
                Size size = field.getAnnotation(Size.class);
                this.sizeMin = size.min();
                this.sizeMax = size.max();
            } else {
                this.sizeMin = 0;
                this.sizeMax = 0;
            }
            this.hasPattern = field.isAnnotationPresent(Pattern.class);
            this.pattern = hasPattern
                ? field.getAnnotation(Pattern.class).regex()
                : null;

            // Cache generic type info
            var genericFieldType = field.getGenericType();
            if (
                genericFieldType instanceof ParameterizedType parameterizedType
            ) {
                var actualType = parameterizedType.getActualTypeArguments()[0];
                this.genericType = actualType.getTypeName();
            } else {
                this.genericType = null;
            }
        }
    }

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

    private FieldMetadata getFieldMetadata(Class<?> clazz, String fieldName) {
        String cacheKey = clazz.getName() + "." + fieldName;
        return fieldMetadataCache.computeIfAbsent(cacheKey, k -> {
            Field field = findField(clazz, fieldName);
            return field != null ? new FieldMetadata(field) : null;
        });
    }

    @Override
    public EndpointJsonResponse exec(
        ObjectNode in,
        User user,
        WebServer webServer,
        HttpsExchange exchange,
        BNode node
    ) throws Throwable {
        // Performance optimization: add pagination and limits
        int offset = in.has("offset") ? in.get("offset").asInt() : 0;
        int limit = in.has("limit") ? in.get("limit").asInt() : 100;
        boolean skipValidation =
            in.has("skipValidation") && in.get("skipValidation").asBoolean();

        var a = new ArrayNode(null);
        var currentNodeInformation = new ObjectNode(null);

        // Build current node info (optimized)
        var currentNodeInfo = buildCurrentNodeInfo(node);
        currentNodeInformation.set("currentNode", currentNodeInfo);

        // Performance optimization: bulk process attributes
        var processedAttributes = processNodeAttributes(
            node,
            offset,
            limit,
            skipValidation
        );
        a.addAll(processedAttributes);

        currentNodeInformation.set("attributes", a);
        currentNodeInformation.set(
            "pagination",
            createPaginationInfo(offset, limit, node.outDegree())
        );

        in.removeAll();
        return new EndpointJsonResponse(
            currentNodeInformation,
            node.prettyName() + '@' + node.id()
        );
    }

    private ObjectNode buildCurrentNodeInfo(BNode node) {
        var currentNodeInfo = new ObjectNode(null);
        currentNodeInfo.set("id", new IntNode(node.id()));
        currentNodeInfo.set("name", new TextNode(node.prettyName()));
        currentNodeInfo.set(
            "type",
            new TextNode(node.getClass().getSimpleName())
        );

        if (node instanceof BusinessNode businessNode) {
            currentNodeInfo.set(
                "isValid",
                BooleanNode.valueOf(businessNode.isValid())
            );
        }

        if (node instanceof ValuedNode<?> valuedNode) {
            addValuedNodeInfo(currentNodeInfo, valuedNode);
        }

        return currentNodeInfo;
    }

    private void addValuedNodeInfo(
        ObjectNode nodeInfo,
        ValuedNode<?> valuedNode
    ) {
        if (valuedNode.get() == null) {
            nodeInfo.set("value", NullNode.getInstance());
        } else if (valuedNode instanceof byransha.BooleanNode) {
            nodeInfo.set(
                "value",
                BooleanNode.valueOf((Boolean) valuedNode.get())
            );
        } else if (valuedNode instanceof byransha.IntNode) {
            nodeInfo.set("value", new IntNode((Integer) valuedNode.get()));
        } else {
            nodeInfo.set("value", new TextNode(valuedNode.getAsString()));
        }
        nodeInfo.set("mimeType", new TextNode(valuedNode.getMimeType()));
    }

    private List<ObjectNode> processNodeAttributes(
        BNode node,
        int offset,
        int limit,
        boolean skipValidation
    ) {
        var attributes = new ArrayList<ObjectNode>();
        var count = new java.util.concurrent.atomic.AtomicInteger(0);
        var processed = new java.util.concurrent.atomic.AtomicInteger(0);

        node.forEachOut((name, out) -> {
            if (out.deleted) return; // Skip deleted nodes early

            if (processed.get() < offset) {
                processed.incrementAndGet();
                return;
            }

            if (count.get() >= limit) return; // Stop processing after limit

            var attributeNode = buildAttributeNode(
                node,
                name,
                out,
                skipValidation
            );
            if (attributeNode != null) {
                attributes.add(attributeNode);
                count.incrementAndGet();
            }
            processed.incrementAndGet();
        });

        return attributes;
    }

    private ObjectNode buildAttributeNode(
        BNode node,
        String name,
        BNode out,
        boolean skipValidation
    ) {
        var b = new ObjectNode(null);
        b.set("id", new IntNode(out.id()));
        b.set("name", new TextNode(name));
        b.set("type", new TextNode(out.getClass().getSimpleName()));

        // Add business node info
        if (out instanceof BusinessNode bn) {
            b.set("isValid", BooleanNode.valueOf(bn.isValid()));
        }

        // Add valued node info
        if (out instanceof ValuedNode<?> vn) {
            addValuedNodeInfo(b, vn);
        }

        // Add collection node info (optimized)
        addCollectionNodeInfo(b, out);

        // Add generic type info (cached)
        if (isCollectionNode(out)) {
            addGenericTypeInfo(b, node, name);
        }

        // Add radio node options (optimized)
        if (out instanceof RadioNode<?> radioNode) {
            addRadioNodeOptions(b, radioNode);
        }

        // Add validation info (skip if requested for performance)
        if (!skipValidation) {
            addValidationInfo(b, node, name);
        }

        return b;
    }

    private void addCollectionNodeInfo(ObjectNode b, BNode out) {
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
    }

    private boolean isCollectionNode(BNode out) {
        return (
            out instanceof ListNode<?> ||
            out instanceof SetNode<?> ||
            out instanceof DropdownNode<?>
        );
    }

    private void addGenericTypeInfo(ObjectNode b, BNode node, String name) {
        try {
            FieldMetadata metadata = getFieldMetadata(node.getClass(), name);
            if (metadata != null && metadata.genericType != null) {
                b.set("listNodeType", new TextNode(metadata.genericType));
            }
        } catch (Exception e) {
            // Log error but don't fail the entire operation
            System.err.println(
                "Error getting generic type for field " +
                name +
                ": " +
                e.getMessage()
            );
        }
    }

    private void addRadioNodeOptions(ObjectNode b, RadioNode<?> radioNode) {
        try {
            b.set(
                "options",
                JsonNodeFactory.instance
                    .arrayNode()
                    .addAll(
                        radioNode
                            .getOptions()
                            .stream()
                            .map(option ->
                                option == null
                                    ? NullNode.getInstance()
                                    : new TextNode(option.toString())
                            )
                            .collect(Collectors.toList())
                    )
            );
        } catch (Exception e) {
            System.err.println(
                "Error getting radio node options: " + e.getMessage()
            );
        }
    }

    private void addValidationInfo(ObjectNode b, BNode node, String name) {
        FieldMetadata metadata = getFieldMetadata(node.getClass(), name);
        if (metadata != null) {
            var validations = new ObjectNode(null);
            if (metadata.hasRequired) {
                validations.set("required", BooleanNode.valueOf(true));
            }
            if (metadata.hasMin) {
                validations.set("min", new DoubleNode(metadata.minValue));
            }
            if (metadata.hasMax) {
                validations.set("max", new DoubleNode(metadata.maxValue));
            }
            if (metadata.hasSize) {
                var sizeInfo = new ObjectNode(null);
                sizeInfo.set("min", new IntNode(metadata.sizeMin));
                sizeInfo.set("max", new IntNode(metadata.sizeMax));
                validations.set("size", sizeInfo);
            }
            if (metadata.hasPattern) {
                validations.set("pattern", new TextNode(metadata.pattern));
            }
            if (validations.size() > 0) {
                b.set("validations", validations);
            }
        }
    }

    private ObjectNode createPaginationInfo(
        int offset,
        int limit,
        int totalCount
    ) {
        var pagination = new ObjectNode(null);
        pagination.set("offset", new IntNode(offset));
        pagination.set("limit", new IntNode(limit));
        pagination.set("totalCount", new IntNode(totalCount));
        pagination.set(
            "hasMore",
            BooleanNode.valueOf(offset + limit < totalCount)
        );
        return pagination;
    }

    @Override
    public boolean sendContentByDefault() {
        return false;
    }
}
