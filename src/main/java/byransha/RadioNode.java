package byransha;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.node.JsonNodeType;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class RadioNode<N> extends IntNode {
    private final Set<N> options = new LinkedHashSet<>();

    public RadioNode(BBGraph db) {
        super(db);
        set(null);
    }

    public RadioNode(BBGraph db, int id) {
        super(db, id);
        set(null);
    }



    @Override
    public void fromString(String s) {
        try {
            int value = Integer.parseInt(s);
            set(value);
        } catch (NumberFormatException e) {
            System.err.println("Invalid value for RadioNode: " + s);
            throw new IllegalArgumentException("Invalid value for RadioNode: " + s, e);
        }
    }

    @Override
    public String whatIsThis() {
        return "";
    }

    @Override
    public String prettyName() {
        return "";
    }

    public void addOption(N option) {
        options.add(option);
    }

    public void addOptions(N... newOptions) {
        Collections.addAll(options, newOptions);
    }

    public N getSelectedOption() {
        if (get() == null) {
            return null;
        }
        return options.stream()
                .skip(get())
                .findFirst()
                .orElse(null);
    }

    public Set<N> getOptions() {
        return Collections.unmodifiableSet(options);
    }
}
