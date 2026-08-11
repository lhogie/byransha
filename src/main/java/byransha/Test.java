package byransha;

import java.util.UUID;

public class Test {
	public static void main(String[] args) throws Exception {
		int a=1;
		int b =2;
		long c = ((long) a << 32) | Integer.toUnsignedLong(b);
		System.out.println(c);
	}
}
