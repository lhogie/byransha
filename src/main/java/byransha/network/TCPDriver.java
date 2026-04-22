package byransha.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;

import byransha.util.GZip;

public class TCPDriver extends IPDriver {
	public static final int port = 9876;
	ServerSocket socket;

	public TCPDriver(NetworkAgent g) throws FileNotFoundException, IOException {
		super(g);

		new Thread(() -> {
			try {
				socket = new ServerSocket(port);
				System.out.println("TCP Server is listening on port " + port);

				while (true) {
					var client = socket.accept();

					new Thread(() -> {
						var from = client.getInetAddress();
						var peer = na().findPeer(from);
						
						try {
							peer.out = new DataOutputStream(client.getOutputStream());
							var is = new DataInputStream(client.getInputStream());

							while (true) {
								int len = is.readInt();
								var bytes = is.readNBytes(len);
								var msg = (Message) g.serializer.fromBytes(GZip.gunzip(bytes));
								g.handle(msg);
							}
						}
						catch (IOException err) {
							error(err);
						}
					}).start();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}, "network agent UDP reception thread").start();
	}

	@Override
	protected String protocol() {
		return "TCP";
	}

	@Override
	public void send(byte[] msgBytes, PeerNode to) throws IOException {
		to.out.writeInt(msgBytes.length);
		to.out.write(msgBytes);
//		to.out.flush();
	}
}
