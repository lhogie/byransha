package byransha.security;

public class Utils {
	public static boolean isDebuggerAttached() {
		return java.lang.management.ManagementFactory.getRuntimeMXBean().getInputArguments().toString()
				.contains("-agentlib:jdwp");
	}

	// Also check via ThreadMXBean
	public static boolean isDebuggerAttached2() {
		for (Thread t : Thread.getAllStackTraces().keySet()) {
			if (t.getName().contains("JDWP") || t.getName().contains("debugger"))
				return true;
		}
		return false;
	}
}
