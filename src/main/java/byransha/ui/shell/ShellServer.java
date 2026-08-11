package byransha.ui.shell;

import java.io.IOException;
import java.net.ServerSocket;

import byransha.Service;
import byransha.graph.Hub;
import byransha.graph.LoopingThreadNode;
import byransha.network.Message;

public class ShellServer extends Service {

	public static final int DEFAULT_PORT = 42424;

	public ShellServer(Hub g, int port) throws Throwable {
		super(g);

		new LoopingThreadNode(this, () -> 1d, "server", () -> {
			try {
				startServer(port);
			} catch (Throwable err) {
				hub().errorLog.add(err);
			}
		});
	}

	private void startServer(int port) throws Throwable {
		try (var serverSocket = new ServerSocket(port)) {
			System.out.println("Telnet server listening on port " + port);

			while (true) {
				try {
					var clientSocket = serverSocket.accept();
					new TelnetSession(clientSocket, this);
				} catch (IOException e) {
					System.err.println("Client error: " + e.getMessage());
				}
			}
		}
	}

	@Override
	public String whatIsThis() {
		return "the TCP server for command line interaction with the graph";
	}

	@Override
	public String toString() {
		return "shell server";
	}

	@Override
	protected void incomingMessage(Message msg) {
		// TODO Auto-generated method stub
		
	}
}