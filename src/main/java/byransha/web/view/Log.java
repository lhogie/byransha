package byransha.web.view;

import java.util.Date;

public class Log {
	final public Date date;
	final public String msg;

	public Log(Date d, String s) {
		this.date = d;
		this.msg = s;
	}
}