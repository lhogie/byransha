package byransha.ui.javafx;

import javafx.scene.control.TextArea;
import javafx.scene.text.Text;

public class ByText extends TextArea {
	public ByText(String s) {
		super(s);
		setEditable(false);
		setWrapText(true);
//		setPrefSize(100, 30);
	}
}
