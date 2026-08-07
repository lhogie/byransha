package byransha;

import byransha.util.ByUtils;

public class UUIDGenerator {
	public static void main(String[] args) throws Exception {
		System.out.println(ByUtils.random.nextLong() + "L, " + ByUtils.random.nextLong() + "L");
	}
}
