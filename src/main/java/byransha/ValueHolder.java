package byransha;

public interface ValueHolder<N> {
    N getValue();
    void setValue(N n, User user);
}
