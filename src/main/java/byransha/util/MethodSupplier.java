package byransha.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MethodSupplier extends ValueSupplier {
	final Method m;

	public MethodSupplier(Object o, Method m) {
		super(o);
		this.m = m;

	}

	@Override
	public Object get() {
		try {
			return m.invoke(o);
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new Error(e);
		}
	}

}