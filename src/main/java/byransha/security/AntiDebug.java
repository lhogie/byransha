package byransha.security;

import java.lang.management.ManagementFactory;
import java.net.NetworkInterface;
import java.util.Base64;

public class AntiDebug {
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

	public static void timingCheck() {
		long start = System.nanoTime();
		// some trivial operation
		int x = 0;
		for (int i = 0; i < 1000; i++)
			x += i;
		long elapsed = System.nanoTime() - start;

		if (elapsed > 5_000_000) { // 5ms is suspicious
			System.exit(1);
		}
	}

	public static boolean isSuspiciousEnvironment() {
		// Check for common reverse engineering tools in system properties
		String[] suspicious = { "frida", "xposed", "substrate", "jeb", "jadx" };
		String props = System.getProperties().toString().toLowerCase();
		for (String s : suspicious) {
			if (props.contains(s))
				return true;
		}

		// Check for suspicious agents
		for (var arg : ManagementFactory.getRuntimeMXBean().getInputArguments()) {
			if (arg.contains("javaagent") || arg.contains("jdwp") || arg.contains("jvmti"))
				return true;
		}
		return false;
	}

	public static String getMachineId() throws Exception {
		// Use MAC address as hardware fingerprint
		NetworkInterface ni = NetworkInterface.getByName("eth0");
		byte[] mac = ni.getHardwareAddress();
		return Base64.getEncoder().encodeToString(mac);
	}
}
