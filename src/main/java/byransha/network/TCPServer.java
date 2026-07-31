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

		ByUtils.thread("TCP server thread", () -> {
			while (true) {
				try (var socket = new ServerSocket(port)) {
					System.out.println("TCP Server is listening on port " + port);

					while (true) {
						net.newSocket(socket.accept(), false);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}

				sleep(1);
			}
		});
	}
}
