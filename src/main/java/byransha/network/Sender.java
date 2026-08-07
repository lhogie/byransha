package byransha.network;

import java.io.IOException;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.function.Consumer;

import byransha.graph.LoopingThreadNode;
import byransha.graph.ShowInKishanView;
import byransha.network.routing.BFSRouting;
import byransha.primitive.LongNode;
import byransha.primitive.StringNode;
import byransha.security.NetworkBox;
import byransha.system.SystemNode;
import byransha.util.ByUtils;
import byransha.util.Q;

public class Sender extends SystemNode implements Consumer<Message> {
	private PriorityBlockingQueue<Message> inWait = new PriorityBlockingQueue<>(10,
			(msg1, msg2) -> Long.compare(msg1.sendDateMs, msg2.sendDateMs));
	private Q<Message> toSendNow = new Q<>(10);

	@ShowInKishanView
	protected int nbMessageSent;

	@ShowInKishanView
	final StringNode sendInfo = new StringNode(this);

	@ShowInKishanView
	final BFSRouting routingProtocol = new BFSRouting(this);

	private Thread waitingThread;

	@ShowInKishanView
	private LongNode timeBeforeResendMs = new LongNode(this, 1000);

	public Sender(NetworkAgent net) {
		super(net);
	}

	public void start() {
		this.waitingThread = new LoopingThreadNode(this, () -> 0d, "waiting", () -> {
			try {
				Message msg = inWait.take(); // waiting here
				long waitTime = msg.waitTimeMs();
//				System.out.println("next message will be sent in " + waitTime + "ms");

				if (waitTime > 0) {
					try {
						Thread.sleep(waitTime);
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
			msg.nbAttempts++;

			if (!msg.keepAliveExpired()) {
				var recipient = hub().network.neighborhood.findPeerByName(msg.routingInfo.nameOfRecipient());
				System.out.println("computing route to " + recipient);
				var newRoute = routingProtocol.computeRouteToReach(recipient);
				System.out.println("found " + newRoute);

				if (newRoute != null) { // if a better route could be found
					msg.routingInfo.suggestedRoute = newRoute.stream().map(p -> p.name).toList();
				}
				System.out.println("using route " + msg.routingInfo.suggestedRoute);

				if (msg.routingInfo.suggestedRoute == null) {
					System.out.println("No route to " + recipient + ". Retrying later...");
					errorWhenTryingToSending(msg);
				} else if (msg.routingInfo.suggestedRoute.isEmpty()) {
					System.out.println("delivering locally");
					msg.routingInfo.actualRoute.add(((NetworkAgent) parent).neighborhood.self.name);
					((NetworkAgent) parent).processIncomingMessage(msg);
				} else {
					var relay = hub().network.neighborhood.findPeerByName(msg.routingInfo.suggestedRoute.getFirst());
					System.out.println("relaying via " + relay);

					if (relay.getConnection() != null) {
						if (relay.sharedSecret == null) {
							System.out.println(
									"Cannot route through " + relay.name + ": missing public key. Retrying later...");
							errorWhenTryingToSending(msg);
						} else {
							try {
								System.out.println("sending message to " + recipient + " via route "
										+ msg.routingInfo.suggestedRoute);

								byte[] serializedMsg = ByUtils.serializer.toBytes(msg);
								byte[] hopEncryptedBytes = NetworkBox.encryptFast(relay.sharedSecret, serializedMsg);
								System.out.println("writing to TCP of " + relay);
								relay.getConnection().writeObject(hopEncryptedBytes);
								++nbMessageSent;
								updateInOutInfo();
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
		msg.errorCount++;
//		System.out.println(msg.errorCount);

		if (msg.nbAttempts < msg.maxNbAttempts && !msg.keepAliveExpired()) {
			msg.emissionDateMs = System.currentTimeMillis() + timeBeforeResendMs.get();
//			System.out.println("retrying in " + timeBeforeResendMs.get() + " ms");
			enqueue(msg);
		}
	}

	@Override
	public void accept(Message msg) {
		applyOOInfos(msg);
		enqueue(msg);
	}

	private void enqueue(Message msg) {
		System.out.println("msg scheduled in " + (msg.emissionDateMs - System.currentTimeMillis() + "ms"));

		if (msg.waitTimeMs() == 0) {
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

	private void updateInOutInfo() {
		sendInfo.set(nbMessageSent + " sent");
	}

	@ShowInKishanView
	public int nbMessageInSendingQueue() {
		return inWait.size();
	}

	private void applyOOInfos(Message msg) {
		msg.routingInfo.nameOfRecipient = msg.ooInfos.recipient.name;

		if (msg.ooInfos.recipient.publicKey == null) {
			System.out.println("Cannot send E2E message to " + msg.ooInfos.recipient.name + ": public key is missing.");
			return;
		}

		byte[] rawBytesPayload = ByUtils.serializer.toBytes(msg.ooInfos.content);
		msg.content = NetworkBox.encrypt(hub().network.neighborhood.self.privateKey, msg.ooInfos.recipient.publicKey,
				rawBytesPayload);
	}

	public void considerForwarding(Message msg, Consumer<Message> c) {
		if (msg.routingInfo.actualRoute.contains(hub().network.neighborhood.self.name))
			return;

		enqueue(msg);
	}

}
