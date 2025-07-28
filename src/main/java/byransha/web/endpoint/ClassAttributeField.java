package byransha.web.endpoint;

import byransha.*;
import byransha.annotations.*;
import byransha.labmodel.model.gitMind.gestionnaire.Gestionnaire;
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
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ClassAttributeField extends NodeEndpoint<BNode> implements View {

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
        final ListOptions listOptions;
        final List<String> choices;

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

            // Extract ListOptions and choices
            var annotation = field.getAnnotation(ListOptions.class);
            if (annotation == null) {
                this.listOptions = new ListOptions() {
                    @Override
                    public Class<
                        ? extends java.lang.annotation.Annotation
                    > annotationType() {
                        return ListOptions.class;
                    }

                    @Override
                    public ListType type() {
                        return ListType.LIST;
                    }

                    @Override
                    public OptionsSource source() {
                        return OptionsSource.STATIC;
                    }

                    @Override
                    public String[] staticOptions() {
                        return new String[0];
                    }

                    @Override
                    public boolean allowCreation() {
                        return false;
                    }

                    @Override
                    public boolean allowMultiple() {
                        return false;
                    }

                    @Override
                    public int maxItems() {
                        return Integer.MAX_VALUE;
                    }

                    @Override
                    public int minItems() {
                        return 0;
                    }

                    @Override
                    public ElementType elementType() {
                        return ElementType.STRING;
                    }
                };
            } else {
                this.listOptions = annotation;
            }
            this.choices = new ArrayList<>();
            if (
                listOptions != null &&
                listOptions.source() == ListOptions.OptionsSource.STATIC
            ) {
                this.choices.addAll(Arrays.asList(listOptions.staticOptions()));
            }

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
            } catch (NoSuchFieldException e) {}
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
        if (node == null) {
            return new EndpointJsonResponse(
                new TextNode("Node not found"),
                "Node not found"
            );
        }

        int offset = in.has("offset") ? in.get("offset").asInt() : 0;
        int limit = in.has("limit") ? in.get("limit").asInt() : 100;
        boolean skipValidation =
            in.has("skipValidation") && in.get("skipValidation").asBoolean();

        if (user instanceof Gestionnaire gest) System.out.println(
            gest.getFiltres()
        );

        var a = new ArrayNode(null);
        var currentNodeInformation = new ObjectNode(null);

        var currentNodeInfo = buildCurrentNodeInfo(node);
        currentNodeInformation.set("currentNode", currentNodeInfo);

        var processedAttributes = processNodeAttributes(
            node,
            offset,
            limit,
            skipValidation,
            user
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
        boolean skipValidation,
        User user
    ) {
        var attributes = new ArrayList<ObjectNode>();
        var count = new java.util.concurrent.atomic.AtomicInteger(0);
        var processed = new java.util.concurrent.atomic.AtomicInteger(0);

        node.forEachOut((name, out) -> {
            if (
                out.deleted ||
                !out.isVisible ||
                out.prettyName().equals("graph")
            ) return;

            if (processed.get() < offset) {
                processed.incrementAndGet();
                return;
            }

            if (count.get() >= limit) return;

            var attributeNode = buildAttributeNode(
                node,
                name,
                out,
                skipValidation,
                user
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
        boolean skipValidation,
        User user
    ) {
        var b = new ObjectNode(null);
        b.set("id", new IntNode(out.id()));
        b.set("name", new TextNode(name));
        b.set("type", new TextNode(out.getClass().getSimpleName()));

        if (out instanceof BusinessNode bn) {
            b.set("isValid", BooleanNode.valueOf(bn.isValid()));
        }

        if (out instanceof ValuedNode<?> vn) {
            addValuedNodeInfo(b, vn);
        }

        addCollectionNodeInfo(b, out);

        if (isCollectionNode(out)) {
            addGenericTypeInfo(b, node, name, user);
        }

        if (!skipValidation) {
            addValidationInfo(b, node, name);
        }

        return b;
    }

    private void addCollectionNodeInfo(ObjectNode b, BNode out) {
        if (out instanceof ListNode<?> lc) {
            b.set("canAddNewNode", BooleanNode.valueOf(lc.canAddNewNode()));
            b.set("allowMultiple", BooleanNode.valueOf(lc.allowMultiple()));
            b.set("source", new TextNode(lc.getOptionsSource().name()));
        }
    }

    private boolean isCollectionNode(BNode out) {
        return out instanceof ListNode<?>;
    }

    private void addGenericTypeInfo(
        ObjectNode b,
        BNode node,
        String name,
        User user
    ) {
        try {
            FieldMetadata metadata = getFieldMetadata(node.getClass(), name);
            if (metadata != null) {
                if (metadata.genericType != null) {
                    b.set("listNodeType", new TextNode(metadata.genericType));
                }

                // Add ListOptions information directly to the attribute
                if (metadata.listOptions != null) {
                    b.set(
                        "listType",
                        new TextNode(metadata.listOptions.type().name())
                    );
                    b.set(
                        "source",
                        new TextNode(metadata.listOptions.source().name())
                    );
                    b.set(
                        "allowCreation",
                        BooleanNode.valueOf(
                            metadata.listOptions.allowCreation()
                        )
                    );
                    b.set(
                        "allowMultiple",
                        BooleanNode.valueOf(
                            metadata.listOptions.allowMultiple()
                        )
                    );
                    b.set(
                        "maxItems",
                        new IntNode(metadata.listOptions.maxItems())
                    );
                    b.set(
                        "minItems",
                        new IntNode(metadata.listOptions.minItems())
                    );
                    b.set(
                        "elementType",
                        new TextNode(metadata.listOptions.elementType().name())
                    );

                    // If it's a radio we give value which is the index
                    // of the selected item

                    if (
                        metadata.listOptions.type() ==
                        ListOptions.ListType.RADIO
                    ) {
                        var out = metadata.field.get(node);

                        if (out instanceof ListNode<?> lc) {
                            b.set("value", new IntNode(lc.getSelectedIndex()));
                        }
                    }

                    // Add choices/options with user-based filtering
                    if (!metadata.choices.isEmpty()) {
                        System.out.println("1");
                        ArrayNode choicesArray =
                            JsonNodeFactory.instance.arrayNode();
                        List<String> filteredChoices = applyUserFilter(
                            metadata.choices,
                            user
                        );
                        for (String choice : filteredChoices) {
                            choicesArray.add(choice);
                        }
                        b.set("choices", choicesArray);
                    } else if (
                        metadata.listOptions.source() ==
                        ListOptions.OptionsSource.PROGRAMMATIC
                    ) {
                        var out = metadata.field.get(node);

                        if (out instanceof ListNode<?> lc) {
                            ArrayNode optionsArray =
                                JsonNodeFactory.instance.arrayNode();
                            List<String> originalOptions = lc
                                .getOptionsList()
                                .stream()
                                .map(option ->
                                    option == null ? null : option.toString()
                                )
                                .toList();
                            List<String> filteredOptions = applyUserFilter(
                                originalOptions,
                                user
                            );
                            for (String option : filteredOptions) {
                                if (option == null) {
                                    optionsArray.add(NullNode.getInstance());
                                } else {
                                    optionsArray.add(option);
                                }
                            }

                            b.set("choices", optionsArray);

                            if (
                                metadata.listOptions.elementType() ==
                                ListOptions.ElementType.STRING
                            ) {
                                if (lc.getSelected() != null) {
                                    b.set(
                                        "value",
                                        new TextNode(lc.getSelected())
                                    );
                                } else {
                                    b.set("value", NullNode.getInstance());
                                }
                            } else if (
                                metadata.listOptions.elementType() ==
                                ListOptions.ElementType.INTEGER
                            ) {
                                b.set(
                                    "value",
                                    new IntNode(lc.getSelectedIndex())
                                );
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println(
                "Error getting generic type for field " +
                name +
                ": " +
                e.getMessage()
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

    /**
     * Apply user-based filtering to options list
     * Override this method to implement custom filtering logic
     */
    protected List<String> applyUserFilter(List<String> options, User user) {
        if (user instanceof Gestionnaire gestionnaire) {
            var filtres = gestionnaire.getFiltres();
            if (filtres != null && !filtres.isEmpty()) {
                return options
                    .stream()
                    .filter(option -> filtres.contains(option))
                    .toList();
            }
        }

        return options;
    }
}
