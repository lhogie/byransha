package byransha.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.function.Consumer;

public class Connection {
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private Socket socket;
	private LocalDateTime since;
	String name;

	public Connection(Socket socket) throws IOException, ClassNotFoundException {
		this.socket = socket;
		out = new ObjectOutputStream(socket.getOutputStream());
		in = new ObjectInputStream(socket.getInputStream());
		since = LocalDateTime.now();
		
		this. name = (String) read().content;

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
		return (Message) in.readObject();
	}

	public void write(Message msg) throws IOException {
		out.writeObject(msg);
	}
	
	@Override
	public String toString() {
		return socket.getPort() + " since " + since;
	}
	
}
