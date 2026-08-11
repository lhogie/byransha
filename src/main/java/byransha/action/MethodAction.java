package byransha.action;

import java.lang.reflect.Method;

import byransha.Element;

public class MethodAction extends Action {
	final Method m;

	public MethodAction(Element parent, Method m) {
		super(parent, m.getDeclaringClass());
		this.m = m;
		this.hasButtonOnKishanView = m.isAnnotationPresent(AddButtonOnKishanView.class);
	}

	@Override
	public String whatItDoes() {
		return m.getName();
	}

	@Override
	protected void impl() throws Throwable {
		m.invoke(parent);
	}

	@Override
	public boolean applies() {
		return true;
	}
}
