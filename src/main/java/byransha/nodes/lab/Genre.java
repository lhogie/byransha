package byransha.nodes.lab;

import byransha.graph.BGraph;
import byransha.graph.BNode;

public abstract class Genre extends BNode {

	protected Genre(BGraph g) {
		super(g);
	}

	@Override
	public String whatIsThis() {
		return "a genre";
	}

	public static class Male extends Genre {

		public Male(BGraph g) {
			super(g);
		}

		@Override
		public String toString() {
			return "male";
		}
	}

	public static class Female extends Genre {

		public Female(BGraph g) {
			super(g);
		}

		@Override
		public String toString() {
			return "male";
		}
	}

	public static class NotGenred extends Genre {

		public NotGenred(BGraph g) {
			super(g);
		}

		@Override
		public String toString() {
			return "ungenred";
		}
	}

}
