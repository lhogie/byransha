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

    @Override
    public String prettyName() {
        if (get() == null) {
            return "une date";
        }

        try {
            // Parse the string into a Date object
            Date date = new Date(get());

            // Format the date in dd/MM/yyyy format
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");
            return outputFormat.format(date);
        } catch (Exception e) {
            // If any exception occurs during parsing, return the original string
            return get();
        }
    }


}
