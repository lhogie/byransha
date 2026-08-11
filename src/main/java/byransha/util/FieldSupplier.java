package byransha.util;

import java.lang.reflect.Field;

public class FieldSupplier extends ValueSupplier {
	final Field f;

	public FieldSupplier(Object o, Field f) {
		super(o);
		this.f = f;
	}

	@Override
	public Object get() {
		try {
			return f.get(o);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new Error(e);
		}
	}

}