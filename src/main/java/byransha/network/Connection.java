package byransha.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDateTime;

import byransha.graph.ShowInKishanView;

public class Connection {
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private Socket socket;
	@ShowInKishanView
	private LocalDateTime since;

	@ShowInKishanView
	private int nbMessagesReceived;
	@ShowInKishanView
	private int nbMessagesSent;

	public Connection(Socket socket) throws IOException {
		this.socket = socket;
		out = new ObjectOutputStream(socket.getOutputStream());
		in = new ObjectInputStream(socket.getInputStream());
		since = LocalDateTime.now();

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

	public Message read() throws ClassNotFoundException, IOException {
		var m = (Message) in.readObject();
		++nbMessagesReceived;
		return m;
	}

	public void write(Message msg) throws IOException {
		out.writeObject(msg);
		++nbMessagesSent;
	}

	@Override
	public String toString() {
		return socket.getPort() + " since " + since;
	}

}
