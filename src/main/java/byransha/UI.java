package byransha;

import java.awt.Color;

public class UI extends BNode {
	ColorNode backgroundColor;
	ColorNode textColor;

	public UI(BBGraph db) {
		super(db);
		backgroundColor = new ColorNode(db);
		backgroundColor.set(Color.darkGray);
		textColor = new ColorNode(db);
		textColor.set(Color.black);
	}

	@Override
	public String prettyName() {
		return "UI preferences";
	}

	@Override
	public String whatIsThis() {
		return "UI preferences";
	}
}
