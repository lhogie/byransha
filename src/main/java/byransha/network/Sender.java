package byransha.network;

import java.io.IOException;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.function.Consumer;

import byransha.graph.ServiceNode;
import byransha.graph.ShowInKishanView;
import byransha.primitive.LongNode;
import byransha.primitive.StringNode;
import byransha.security.NetworkBox;
import byransha.util.ByUtils;
import byransha.util.Q;

public class Sender extends ServiceNode {
	private PriorityBlockingQueue<Message> inWait = new PriorityBlockingQueue<>(10,
			(msg1, msg2) -> Long.compare(msg1.sendDateMs, msg2.sendDateMs));
	private Q<Message> toSendNow = new Q<>(10);

	@ShowInKishanView
	protected int nbMessageSent;

	@ShowInKishanView
	final StringNode sendInfo = new StringNode(this);

	@ShowInKishanView
	final RoutingService routingProtocol = new BFSRouting(this);

	private Thread waitingThread;

	@ShowInKishanView
	private LongNode timeBeforeResendMs = new LongNode(this, 1000);

	public Sender(NetworkAgent net) {
		super(net);
	}

	public void start() {
		this.waitingThread = ByUtils.loop(() -> 0d, "waiting", () -> {
			try {
				Message msg = inWait.take(); // waiting here
				long waitTime = msg.waitTimeMs();
//				System.out.println("next message will be sent in " + waitTime + "ms");

				if (waitTime > 0) {
					try {
						Thread.sleep(waitTime);
						toSendNow.add_sync(msg);
					} catch (InterruptedException interrupt) {
						add(msg);
					}
				}
			} catch (InterruptedException interrupt) {
			}
		});

		ByUtils.thread("to send now", () -> {
			while (true) {
				Message msg = toSendNow.poll_sync();
				msg.nbAttempts++;

				if (!msg.keepAliveExpired()) {
					var recipient = hub().networkAgent.neighborhood.findPeerByName(msg.routingInfo.nameOfRecipient());
					var newRoute = routingProtocol.computeRouteToReach(recipient);

					if (newRoute != null) { // if a better route could be found
						msg.routingInfo.suggestedRoute = newRoute.stream().map(p -> p.name).toList();
					}

					
					System.out.println("suggested route: " + msg.routingInfo.suggestedRoute);
					if (msg.routingInfo.suggestedRoute == null) {
//						System.out.println(hub().networkAgent.neighborhood.neighbors());
						System.out.println("No route to " + recipient + ". Retrying later...");
						addAfterError(msg);
					} else if (msg.routingInfo.suggestedRoute.isEmpty()) {
						System.out.println("delivering locally");
						msg.routingInfo.actualRoute.add(((NetworkAgent) parent).neighborhood.self.name);
						((NetworkAgent) parent).processIncomingMessage(msg);
					} else {
						var relay = hub().networkAgent.neighborhood
								.findPeerByName(msg.routingInfo.suggestedRoute.getFirst());

						if (relay.getConnection() != null) {
							if (relay.sharedSecret == null) {
								System.out.println("Cannot route through " + relay.name
										+ ": missing public key. Retrying later...");
								addAfterError(msg);
							} else {
								try {
									System.out.println("sending message to " + relay.name + " via route "
											+ msg.routingInfo.suggestedRoute);

									byte[] serializedMsg = ByUtils.serializer.toBytes(msg);
									byte[] hopEncryptedBytes = NetworkBox.encryptFast(relay.sharedSecret,
											serializedMsg);
									relay.getConnection().writeObject(hopEncryptedBytes);
									++nbMessageSent;
									updateInOutInfo();
								} catch (IOException e) {
									e.printStackTrace();
									addAfterError(msg);
								}
							}
						} else {
							addAfterError(msg);
						}
					}
				}
			}
		});

	}

	private void addAfterError(Message msg) {
		msg.errorCount++;
		System.out.println(msg.errorCount);

		if (msg.nbAttempts < msg.maxNbAttempts) {
			msg.emissionDateMs = System.currentTimeMillis() + timeBeforeResendMs.get();
			System.out.println("retrying in " + timeBeforeResendMs.get() + " ms");
			add(msg);
		}
	}

	private void add(Message msg) {
		if (msg.waitTimeMs() == 0) {
			toSendNow.add_sync(msg);
		} else {
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

	public void send(Message msg) {
		applyingOOInfos(msg);
		add(msg);
	}

	private void applyingOOInfos(Message msg) {
		msg.routingInfo.nameOfRecipient = msg.ooInfos.recipient.name;

		if (msg.ooInfos.recipient.publicKey == null) {
			System.out.println("Cannot send E2E message to " + msg.ooInfos.recipient.name + ": public key is missing.");
			return;
		}

		byte[] rawBytesPayload = ByUtils.serializer.toBytes(msg.ooInfos.content);
		msg.content = NetworkBox.encrypt(hub().networkAgent.neighborhood.self.privateKey,
				msg.ooInfos.recipient.publicKey, rawBytesPayload);
	}

	public void considerForwarding(Message msg, Consumer<Message> c) {
		if (msg.routingInfo.actualRoute.contains(hub().networkAgent.neighborhood.self.name))
			return;

		add(msg);
	}

}
