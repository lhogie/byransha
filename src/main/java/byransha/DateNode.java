package byransha;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateNode extends StringNode {

	public DateNode(BBGraph g, String v) {
		super(g, v);
	}

	public DateNode(BBGraph g) {super(g);}

	public DateNode(BBGraph g, int id) {
		super(g, id);
	}
}
