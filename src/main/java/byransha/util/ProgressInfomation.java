package byransha.util;

public record ProgressInfomation(double progress, double target) {
	public double ratio() {
		return progress / target;
	}

	public boolean completed() {
		return progress == target;
	}
}
