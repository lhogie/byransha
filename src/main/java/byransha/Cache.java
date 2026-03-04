package byransha;

import java.util.function.Supplier;

public class Cache<T> {

	private T value;
	final Supplier<T> supplier;

	public Cache(Supplier<T> supplier) {
		this.supplier = supplier;
	}

	public T get() {
		if (value == null) {
			value = supplier.get();
		}
		return value;
	}

	public void invalidate() {
		value = null;
	}

}
