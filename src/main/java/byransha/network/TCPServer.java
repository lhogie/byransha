package byransha.network;

import java.io.IOException;
import java.net.ServerSocket;

import byransha.graph.ServiceNode;
import byransha.graph.ShowInKishanView;
import byransha.util.ByUtils;

public class TCPServer extends ServiceNode {

	public static final int DEFAULT_PORT = 50170;

	@ShowInKishanView
	public final int port;

	public TCPServer(TCPNode net, int port) {
		super(net);
		this.port = port;
	}

	public void start() {
		ByUtils.loop(() -> 1.0, "TCP server thread", () -> {
			try (var socket = new ServerSocket(port)) {
				System.out.println("TCP Server is listening on port " + port);

				while (true) {
					hub().networkAgent.tcp.newSocket(socket.accept(), false);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}
}
