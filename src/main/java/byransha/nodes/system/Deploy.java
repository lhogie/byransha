package byransha.nodes.system;

import java.io.File;
import java.io.IOException;

import com.google.common.io.Files;

import byransha.graph.Action;
import byransha.graph.ShowInKishanView;
import byransha.nodes.primitive.StringNode;
import byransha.nodes.system.Restart.byransha;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.xfer.FileSystemFile;

public class Deploy extends Action<Byransha> {
	@ShowInKishanView
	public final StringNode scpHost = new StringNode(this, "bastion.i3s.unice.fr", ".+");
	@ShowInKishanView
	public final StringNode scpRemoteDir = new StringNode(this, "public_html/software/byransha/downloads/", ".+");
	@ShowInKishanView
	public final StringNode username = new StringNode(this, System.getProperty("user.name"), ".+");

	public Deploy(Byransha b) {
		super(b, byransha.class);
	}

	@Override
	public String whatItDoes() {
		return "deploy the local version";
	}

	@Override
	protected void impl() throws Throwable {
		var version = ((Byransha) parent).versionNode.get();
		Files.write(version.getBytes(), new File(Byransha.jarDirectory, "last-version.txt"));
		String to = scpRemoteDir.get() + "/" + version;
		scp(Byransha.jarDirectory, scpHost.get(), to, username.get(), null);
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
