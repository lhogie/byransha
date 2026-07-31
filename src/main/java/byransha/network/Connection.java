package byransha.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalDateTime;

import byransha.graph.ShowInKishanView;

public class Connection {
	private DataInputStream in;
	private DataOutputStream out;
	private Socket socket;
	@ShowInKishanView
	private LocalDateTime since;

	@ShowInKishanView
	private int nbMessagesReceived;
	@ShowInKishanView
	private int nbMessagesSent;

	public Connection(Socket socket) throws IOException {
		since = LocalDateTime.now();
		this.socket = socket;
		out = new DataOutputStream(socket.getOutputStream());
		in = new DataInputStream(socket.getInputStream());
	}

	public void close() {
		try {
			if (in != null) {
				in.close();
				in = null;
			}
			if (out != null) {
				out.close();
				out = null;
			}
			if (socket != null) {
				socket.close();
				socket = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean isConnected() {
		return out != null;
	}

	public Message readMessage() throws ClassNotFoundException, IOException {
		int len = in.readInt();
		var bytes = in.readNBytes(len);
		var m = (Message) NetworkAgent.serializer.fromBytes(bytes);
		System.out.println("received " + m);
		++nbMessagesReceived;
		return m;
	}

	public void write(Message msg) throws IOException {
		System.out.println("send " + msg);
		var bytes = NetworkAgent.serializer.toBytes(msg);
		out.writeInt(bytes.length);
		out.write(bytes);
		++nbMessagesSent;
	}

	@Override
	public String toString() {
		return socket.getPort() + " since " + since;
	}

}
