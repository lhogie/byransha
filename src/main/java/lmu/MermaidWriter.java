package lmu;

import java.util.Set;

/*
 * Created for Mermaid class diagram generation
 */

/**
 * @author assistant
 */
public class MermaidWriter extends AbstractWriter {

    @Override
    public byte[] writeModel(Model model) throws WriterException {
        if (model == null) throw new NullPointerException();

        StringBuilder mermaid = new StringBuilder();
        mermaid.append("classDiagram\n");

        // Write entities (classes)
        for (Entity entity : model.getEntities()) {
            if (!entity.isPrimitive() && entity.isVisible()) {
                writeEntity(mermaid, entity);
            }
        }

        // Write relationships
        for (Relation relation : model.getRelations()) {
            if (relation.isVisible()) {
                writeRelation(mermaid, relation);
            }
        }

        return mermaid.toString().getBytes();
    }

    private void writeEntity(StringBuilder mermaid, Entity entity) {
        String className = sanitizeClassName(entity.getName());

        // Start class definition
        mermaid.append("    class ").append(className);

        // Check if entity has attributes or operations
        boolean hasContent =
            !entity.getAttributes().isEmpty() ||
            !entity.getOperations().isEmpty();

        if (hasContent) {
            mermaid.append(" {\n");

            // Write attributes
            for (Attribute attribute : entity.getAttributes()) {
                if (attribute.isVisible()) {
                    writeAttribute(mermaid, attribute);
                }
            }

            // Write operations/methods
            for (Operation operation : entity.getOperations()) {
                if (operation.isVisible()) {
                    writeOperation(mermaid, operation);
                }
            }

            mermaid.append("    }\n");
        } else {
            mermaid.append("\n");
        }

        // Add class modifiers
        if (entity.isAbsract()) {
            mermaid
                .append("    ")
                .append(className)
                .append(" : <<abstract>>\n");
        }

        if (entity.isInterface()) {
            mermaid
                .append("    ")
                .append(className)
                .append(" : <<interface>>\n");
        }
    }

    private void writeAttribute(StringBuilder mermaid, Attribute attribute) {
        mermaid.append("        ");

        // Add visibility
        if (attribute.getVisibility() == Visibility.PRIVATE) {
            mermaid.append("-");
        } else if (attribute.getVisibility() == Visibility.PROTECTED) {
            mermaid.append("#");
        } else if (attribute.getVisibility() == Visibility.PUBLIC) {
            mermaid.append("+");
        } else {
            mermaid.append("~"); // package visibility
        }

        // Add static modifier
        if (attribute.isClassStatic()) {
            mermaid.append("static ");
        }

        // Add attribute name and type
        mermaid.append(sanitizeName(attribute.getName()));

        if (attribute.getType() != null) {
            mermaid
                .append(" : ")
                .append(sanitizeType(attribute.getType().getName()));
        }

        mermaid.append("\n");
    }

    private void writeOperation(StringBuilder mermaid, Operation operation) {
        mermaid.append("        ");

        // Add visibility
        if (operation.getVisibility() == Visibility.PRIVATE) {
            mermaid.append("-");
        } else if (operation.getVisibility() == Visibility.PROTECTED) {
            mermaid.append("#");
        } else if (operation.getVisibility() == Visibility.PUBLIC) {
            mermaid.append("+");
        } else {
            mermaid.append("~"); // package visibility
        }

        // Add static modifier
        if (operation.isClassStatic()) {
            mermaid.append("static ");
        }

        // Add operation name
        mermaid.append(sanitizeName(operation.getName())).append("()");

        // Add return type
        if (operation.getType() != null) {
            mermaid
                .append(" : ")
                .append(sanitizeType(operation.getType().getName()));
        }

        mermaid.append("\n");
    }

    private void writeRelation(StringBuilder mermaid, Relation relation) {
        String tailEntity = sanitizeClassName(
            relation.getTailEntity().getName()
        );
        String headEntity = sanitizeClassName(
            relation.getHeadEntity().getName()
        );

        if (relation instanceof InheritanceRelation) {
            // Inheritance: Parent <|-- Child
            mermaid
                .append("    ")
                .append(headEntity)
                .append(" <|-- ")
                .append(tailEntity)
                .append("\n");
        } else if (relation instanceof AssociationRelation) {
            AssociationRelation assoc = (AssociationRelation) relation;

            // Association: ClassA --> ClassB
            mermaid
                .append("    ")
                .append(tailEntity)
                .append(" --> ")
                .append(headEntity);

            // Add cardinality if available
            if (
                assoc.getCardinality() != null &&
                !assoc.getCardinality().trim().isEmpty()
            ) {
                mermaid.append(" : ").append(assoc.getCardinality());
            }

            mermaid.append("\n");
        }
    }

    private String sanitizeClassName(String name) {
        if (name == null) return "UnknownClass";
        // Remove invalid characters for Mermaid class names
        return name.replaceAll("[^a-zA-Z0-9_]", "_");
    }

    private String sanitizeName(String name) {
        if (name == null) return "unknown";
        // Escape special characters for Mermaid
        return name.replaceAll("\\s+", "_").replaceAll("[<>\\[\\]{}()\"']", "");
    }

    private String sanitizeType(String type) {
        if (type == null) return "";
        // Clean up type names for Mermaid
        return type.replaceAll("\\s+", " ").replaceAll("[<>]", "").trim();
    }
}
