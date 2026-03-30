package byransha.util;

public interface PossiblyFailingConsumer<E> {
	void accept(E e) throws Throwable;
}