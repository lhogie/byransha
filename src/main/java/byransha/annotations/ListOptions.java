package byransha.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ListOptions {
    enum ListType {
        LIST, // Ordered list allowing duplicates
        CHECKBOX, // Multiple selection with checkboxes
        DROPDOWN, // Single selection dropdown
        MULTIDROPDOWN, // Multiple selection dropdown
        RADIO, // Single selection radio buttons
    }

    enum OptionsSource {
        DYNAMIC, // Default behavior - let frontend choose/create nodes
        STATIC, // Fixed list of options provided via annotation
        PROGRAMMATIC, // Options set programmatically via setOptions()
    }

    enum ElementType {
        BNODE, // BNode instances
        STRING, // String values
        INTEGER, // Integer values
        BOOLEAN, // Boolean values
        DOUBLE, // Double values
    }

    ListType type() default ListType.LIST;

    OptionsSource source() default OptionsSource.DYNAMIC;

    ElementType elementType() default ElementType.BNODE;

    // For STATIC source - provide option values
    String[] staticOptions() default {};

    boolean allowAdd() default true;

    // Allow creation of new nodes (only applies to DYNAMIC source)
    boolean allowCreation() default true;

    // Allow multiple selection (only applies to DROPDOWN type)
    boolean allowMultiple() default false;

    // Maximum number of items (0 = unlimited)
    int maxItems() default 0;

    // Minimum number of items
    int minItems() default 0;
}
