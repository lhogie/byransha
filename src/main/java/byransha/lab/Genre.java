package byransha.lab;

import byransha.graph.Hub;
import byransha.graph.BNode;

public abstract class Genre extends BNode {

	protected Genre(Hub g) {
		super(g);
	}

	@Override
	public String whatIsThis() {
		return "a genre";
	}

	public static class Male extends Genre {

		public Male(Hub g) {
			super(g);
		}

		@Override
		public String toString() {
			return "male";
		}
	}

	public static class Female extends Genre {

		public Female(Hub g) {
			super(g);
		}

		@Override
		public String toString() {
			return "male";
		}
	}

	public static class NotGenred extends Genre {

		public NotGenred(Hub g) {
			super(g);
		}

		@Override
		public String toString() {
			return "ungenred";
		}
	}

}
