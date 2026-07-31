package byransha.nodes.lab;

import byransha.graph.Root;
import byransha.graph.BNode;

public abstract class Genre extends BNode {

	protected Genre(Root g) {
		super(g);
	}

	@Override
	public String whatIsThis() {
		return "a genre";
	}

	public static class Male extends Genre {

		public Male(Root g) {
			super(g);
		}

		@Override
		public String toString() {
			return "male";
		}
	}

	public static class Female extends Genre {

		public Female(Root g) {
			super(g);
		}

		@Override
		public String toString() {
			return "male";
		}
	}

	public static class NotGenred extends Genre {

		public NotGenred(Root g) {
			super(g);
		}

		@Override
		public String toString() {
			return "ungenred";
		}
	}

}
