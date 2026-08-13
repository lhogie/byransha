package byransha;

import java.io.Serializable;

import byransha.event.NewNodeEvent;

public abstract class InstantiationParameters<P extends Element> implements Serializable {
	final P parent;

	public abstract ID getID();

	protected abstract boolean generateEvents();

	public P getParent() {
		return parent;
	}

	public InstantiationParameters(P parent) {
		this.parent = parent;
	}

	public static class InitByCreator extends InstantiationParameters {
		final ID id;

		public InitByCreator(Element parent, ID id) {
			super(parent);
			this.id = id;
		}


		@Override
		public ID getID() {
			return id;
		}

		@Override
		protected boolean generateEvents() {
			return true;
		}
	}

	public static class InitByEvent extends InstantiationParameters {
		NewNodeEvent<?> e;

		public InitByEvent(Element parent, NewNodeEvent<?> e) {
			super(parent);
			this.e = e;
		}

		@Override
		public ID getID() {
			return e.nodeId;
		}

		@Override
		protected boolean generateEvents() {
			return false;
		}
	}
}
