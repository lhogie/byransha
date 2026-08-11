package byransha.lab;

import byransha.ID;
import byransha.graph.Element;
import byransha.graph.Hub;

public abstract class Genre extends Element {

	protected Genre(Element g, ID id) {
		super(g, id);
	}

	@Override
	public String whatIsThis() {
		return "a genre";
	}

	public static class Male extends Genre {
		public Male(Element g, ID id) {
			super(g, id);
		}

		@Override
		public String toString() {
			return "male";
		}
	}

	public static class Female extends Genre {
		public Female(Element g, ID id) {
			super(g, id);
		}

		@Override
		public String toString() {
			return "male";
		}
	}

	public static class NotGenred extends Genre {
		public NotGenred(Element g, ID id) {
			super(g, id);
		}

		@Override
		public String toString() {
			return "ungenred";
		}
	}

}
