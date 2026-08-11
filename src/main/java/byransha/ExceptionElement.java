package byransha;

import java.time.LocalDateTime;

public class ExceptionElement extends Element {
	public Throwable err;
	public LocalDateTime date;

	public ExceptionElement(Element g, ID id) {
		super(g, id);
	}

	@Override
	public String whatIsThis() {
		return "an error";
	}

	@Override
	public String toString() {
		return err.getMessage();
	}

}
