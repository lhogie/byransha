package byransha.util;

import java.util.function.Supplier;

public abstract class ValueSupplier implements Supplier {
	final Object o;

	public ValueSupplier(Object o) {
		this.o = o;
	}
}