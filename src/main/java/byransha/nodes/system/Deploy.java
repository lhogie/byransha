package byransha.nodes.system;

import java.io.File;
import java.io.IOException;

import byransha.graph.Action;
import byransha.graph.action.FreezingAction.misc;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.xfer.FileSystemFile;

public class Deploy extends Action<Byransha> {

	public Deploy(Byransha b) {
		super(b, misc.class);
	}

	@Override
	public String whatItDoes() {
		return "deploy the local version";
	}

	@Override
	protected void impl() throws Throwable {
		var b = (Byransha) parent;
		String to = b.scpRemoteDir.get() + "/" + b.versionNode.get();
		scp(Byransha.jarDirectory, b.scpHost.get(), to, "lhogie", null);
	}

	@Override
	public boolean applies() {
		return true;
	}

	public static void scp(File srcDir, String host, String remoteDir, String username, String password)
			throws IOException {
		SSHClient ssh = new SSHClient();

		try {
			ssh.connect(host);

			if (password == null) {
				File knownHostsFile = new File(System.getProperty("user.home"), ".ssh/known_hosts");
				ssh.loadKnownHosts(knownHostsFile);
				ssh.authPublickey(username);
			} else {
				ssh.authPassword(username, password);
			}

			ssh.newSCPFileTransfer().upload(new FileSystemFile(srcDir), remoteDir);
		} finally {
			ssh.disconnect();
		}
	}

}
