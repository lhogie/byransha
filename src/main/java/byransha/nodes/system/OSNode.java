package byransha.nodes.system;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;

import com.fasterxml.jackson.databind.node.ObjectNode;

import byransha.graph.BGraph;
import byransha.graph.ShowInKishanView;

public class OSNode extends SystemNode {

	public OSNode(BGraph g) {
		super(g);
	}

	@ShowInKishanView
	public String name() {
		return System.getProperty("os.name");
	}

	@ShowInKishanView
	public String version() {
		return System.getProperty("os.version");
	}

	@ShowInKishanView
	public String arch() {
		return System.getProperty("os.arch");
	}

	@ShowInKishanView
	public double loadAvg() {
		return ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage();
	}

	@ShowInKishanView
	public int nbCores() {
		return Runtime.getRuntime().availableProcessors();
	}

	@ShowInKishanView
	public double busy() {
		return loadAvg() / (double) nbCores();
	}

	@ShowInKishanView
	public String hostAddress() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			return null;
		}
	}

	@ShowInKishanView
	public String hostName() {
		try {
			return InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			return null;
		}
	}

	@Override
	public ObjectNode describeAsJSON() {
		var r = new ObjectNode(factory);
		r.put("IP address", hostAddress());
		r.put("hostname", hostName());
		r.put("arch", arch());
		r.put("OS name", name());
		r.put("load average", loadAvg());
		r.put("#cores", nbCores());
		r.put("%busy", busy());
		return r;
	}

	@Override
	public String whatIsThis() {
		return "information on the operating system";
	}

	@Override
	public String toString() {
		return ManagementFactory.getOperatingSystemMXBean().getName();
	}
}
