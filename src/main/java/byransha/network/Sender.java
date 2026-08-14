package byransha.network;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.function.Consumer;

import byransha.Element;
import byransha.action.base.ShowInKishanView;
import byransha.network.routing.MulticastRouting;
import byransha.network.routing.RoutingService;
import byransha.primitive.LongNode;
import byransha.security.NetworkBox;
import byransha.thread.LoopingThreadNode;
import byransha.util.ByUtils;
import byransha.util.Q;

public class Sender extends Element implements Consumer<Message> {
	private PriorityBlockingQueue<Message> inWait = new PriorityBlockingQueue<>(10,
			(msg1, msg2) -> Long.compare(msg1.ser.sendDateMs, msg2.ser.sendDateMs));
	private Q<Message> toSendNow = new Q<>(10);

	@ShowInKishanView
	protected long nbMessageSent;

	@ShowInKishanView
	final RoutingService routingProtocol = new MulticastRouting(this);

	private Thread waitingThread;

	@ShowInKishanView
	private LongNode timeBeforeResendMs = new LongNode(this, null, 1000);

	public Sender(Network net) {
		super(net, null);
	}

	public void start() {
		routingProtocol.start();

		this.waitingThread = new LoopingThreadNode(this, () -> 0d, "waiting", () -> {
			try {
				Message msg = inWait.take(); // waiting here
				long waitTimeMs = msg.waitTimeMs();
				// System.out.println("next message will be sent in " + waitTime + "ms");

				if (waitTimeMs > 0) {
					try {
						Thread.sleep(waitTimeMs);
						toSendNow.add_sync(msg);
					} catch (InterruptedException interrupt) {
						enqueue(msg);
					}
				}
			} catch (InterruptedException interrupt) {
			}
		}).thread;

		new LoopingThreadNode(this, () -> 0d, "to send now", () -> {
			Message msg = toSendNow.poll_sync();
			msg.ser.nbAttempts++;

			if (!msg.keepAliveExpired()) {
				var recipient = msg.recipient();
				System.out.println("computing relays to reach " + recipient);
				List<Peer> relays = new ArrayList<>(routingProtocol.findRelaysToReach(recipient));
				System.out.println("found " + relays);
				relays.removeIf(r -> msg.actualRoute.contains(r.name));
				System.out.println("removing " + msg.actualRoute);
				System.out.println("using " + relays);

				if (recipient == hub().network.neighborhood.self) {
					relays = List.of(hub().network.neighborhood.self);
				}

				for (var relay : relays) {
					System.out.println("relaying via " + relay);

					if (relay.getConnection() != null) {
						if (relay.sharedSecret == null) {
							System.out.println(
									"Cannot route through " + relay.name + ": missing public key. Retrying later...");
							errorWhenTryingToSending(msg);
						} else {
							try {
								byte[] ser = ByUtils.serializer.toBytes(msg.toSer());
								byte[] hopEncryptedBytes = NetworkBox.encryptFast(relay.sharedSecret, ser);
								System.out.println("writing to TCP of " + relay);
								relay.getConnection().writeObject(hopEncryptedBytes);
								++nbMessageSent;
							} catch (IOException e) {
								e.printStackTrace();
								errorWhenTryingToSending(msg);
							}
						}
					} else {
						errorWhenTryingToSending(msg);
					}
				}
			}
		});

	}

	private void errorWhenTryingToSending(Message msg) {
		msg.ser.errorCount++;
		// System.out.println(msg.errorCount);

		if (msg.ser.nbAttempts < msg.ser.maxNbAttempts && !msg.keepAliveExpired()) {
			msg.ser.emissionDateMs = System.currentTimeMillis() + timeBeforeResendMs.get();
			// System.out.println("retrying in " + timeBeforeResendMs.get() + " ms");
			enqueue(msg);
		}
	}

	@Override
	public void accept(Message msg) {
		// applyOOInfos(msg);
		enqueue(msg);
	}

	private void enqueue(Message msg) {
		System.out.println("msg scheduled in " + (msg.ser.emissionDateMs - System.currentTimeMillis() + "ms"));

		if (msg.waitTimeMs() <= 0) {
			System.out.println("adding to SENDNOW queue " + msg);
			toSendNow.add_sync(msg);
		} else {
			System.out.println("adding to WAIT queue " + msg);
			inWait.add(msg);
			boolean insertedOnHead = inWait.peek() == msg;

			if (insertedOnHead) {
				waitingThread.interrupt();
			}
		}
	}

	@ShowInKishanView
	public int nbMessageInSendingQueue() {
		return inWait.size();
	}

	private void applyOOInfos(Message msg) {

		if (msg.recipient.publicKey == null) {
			System.out.println("Cannot send E2E message to " + msg.recipient.name + ": public key is missing.");
			return;
		}

		byte[] rawBytesPayload = ByUtils.serializer.toBytes(msg.content);
		msg.content = NetworkBox.encrypt(hub().network.neighborhood.self.privateKey, msg.recipient.publicKey,
				rawBytesPayload);
	}

	public void considerForwarding(Message msg, Consumer<Message> c) {
		if (msg.actualRoute.contains(hub().network.neighborhood.self.name)) {
			System.out.println("already received, not forwarding: " + msg.actualRoute);
		} else {
			System.out.println("forwarding: " + msg.content);
			enqueue(msg);
		}
	}

}
