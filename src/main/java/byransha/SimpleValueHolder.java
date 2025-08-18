package byransha;

public class SimpleValueHolder<N> implements ValueHolder<N> {
    N v;

    @Override
    public N getValue() {
        return v;
    }

    @Override
    public void setValue(N n, User user) {
        this.v = n;
    }
}
