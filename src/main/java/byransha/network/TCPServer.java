package byransha.network;

import java.io.IOException;
import java.net.ServerSocket;

import byransha.graph.LoopingThreadNode;
import byransha.graph.ShowInKishanView;
import byransha.system.SystemNode;

public class TCPServer extends SystemNode {

	public static final int DEFAULT_PORT = 50170;

	@ShowInKishanView
	public final int port;

	public TCPServer(TCPNode net, int port) {
		super(net);
		this.port = port;
	}

	public void start() {
		new LoopingThreadNode(this, () -> 1.0, "TCP server thread", () -> {
			try (var socket = new ServerSocket(port)) {
				System.out.println("TCP Server is listening on port " + port);

				while (true) {
					hub().network.tcp.newSocket(socket.accept(), false);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}
}
