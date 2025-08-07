package byransha;

import java.lang.reflect.Field;

public class FieldNode extends ValuedNode<Field> {
    Field field;
    StringNode name;

    public FieldNode(BBGraph g, Field f) {
        super(g);
        this.field = f;
        name = g.accept(new StringNode(g, f.getName()));
    }


    @Override
    public String whatIsThis() {
        return "a node that represents a field";
    }

    @Override
    public String prettyName() {
        return field.getDeclaringClass().getName() + '.' + field.getName();
    }

    @Override
    public void fromString(String s) {
        throw new IllegalStateException();
    }
}
