package byransha;

import java.io.Serializable;

import byransha.event.NewNodeEvent;

public abstract class InstantiationParameters implements Serializable {
	public abstract ID getID();

	public static class InitByID extends InstantiationParameters {
		ID id;

		@Override
		public ID getID() {
			return id;
		}
	}

	public static class InitByEvent extends InstantiationParameters {
		NewNodeEvent<?> e;

		@Override
		public ID getID() {
			return e.nodeId;
		}
	}
}
