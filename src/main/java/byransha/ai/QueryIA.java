package byransha.ai;

import java.awt.Window;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import static java.lang.System.in;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import byransha.Chat;
import byransha.Element;
import byransha.action.ActionMethod;
import byransha.action.AddButtonOnKishanView;
import byransha.action.Category;
import byransha.action.base.ShowInKishanView;
import byransha.ai.QueryIA.AI;
import byransha.ai.QueryIA.AiResult;
import byransha.ai.QueryIA.ResponseMode;
import byransha.ai.QueryIA.ToolEnabledAssistant;
import byransha.lab.stats.DistributionNode;
import byransha.list.action.FunctionAction;
import byransha.list.action.ListNode;
import byransha.primitive.StringNode;
import byransha.primitive.TextNode;
import byransha.ui.telnet.TelnetSession;
import byransha.util.UUIDUtils;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.ollama.OllamaStreamingChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.store.memory.chat.InMemoryChatMemoryStore;

public class QueryIA extends FunctionAction<Element, Element> {
	private static final ObjectMapper mapper = new ObjectMapper();
	private static final ConcurrentHashMap<String, OllamaStreamingChatModel> MODEL_CACHE = new ConcurrentHashMap<>();
	private static final ConcurrentHashMap<String, ToolEnabledAssistant> ASSISTANT_CACHE = new ConcurrentHashMap<>();
	private static final InMemoryChatMemoryStore MEMORY_STORE = new InMemoryChatMemoryStore();
	private static final int MAX_MESSAGES = 8;

	public enum ResponseMode {
		JSON_ONLY, CONVERSATION
	}

	public enum Temerature {
		LOW, MEDIUM
	}

	@ShowInKishanView
	public final StringNode prompt = new StringNode(this, "", ".+");
	public final JSONNode inputJSON;
	@ShowInKishanView
	public final TextNode info = new TextNode(this,
			"La question est envoyé a l'IA, elle peut se tromper, verifier les réponses",
			"La question est envoyé a l'IA, elle peut se tromper, verifier les réponses");
	public static String PRIMARY_MODEL = "granite4:tiny-h";
	private static final String SERVER_MODEL = "ornith:9b";
	private static final String SERVER_MODEL_2 = "granite4:tiny-h";
	private volatile ResponseMode responseMode = ResponseMode.CONVERSATION;
	private static volatile double myCurrentSpeed = 10.0;
	private static volatile double myAlpha = -1.0;
	private static volatile double myPromptLagMs = 1500.0;
	private static volatile boolean ollamaVerified = false;
	private boolean ActivateListNodeResponse = false;
	private volatile Chat currentChat;
	private static volatile boolean settingModel = false;

	@ActionMethod
	@AddButtonOnKishanView
	public void setModel() {
		settingModel = true;
		SwingUtilities.invokeLater(() -> {
                boolean aAccepte = afficherChargementOllama();
                if (aAccepte) {   
                    ASSISTANT_CACHE.clear();       
					settingModel = false;
	}
	});
}

	@ShowInKishanView
	private final ListNode<AiNode> ShowPeersInfo = getAiNodes();

	private ListNode<AiNode> getAiNodes() {
		ListNode<AiNode> nodeList = new ListNode<>(this, " AI nodes", AiNode.class);
		AiNode localNode = new AiNode(hub());
		localNode.name = "Local IA";
		try {
			localNode.address = java.net.InetAddress.getByName("localhost");
		} catch (Exception e) {}
		localNode.haveAi = true;
		nodeList.elements.add(localNode);
		return nodeList;
	}

	@ActionMethod
	@AddButtonOnKishanView
	public void setConversationMode() {
		SwingUtilities.invokeLater(() -> {
			try {
				System.out.println("Changement du mode de réponse de l'IA. Mode actuel : " + responseMode);
				if (responseMode == ResponseMode.JSON_ONLY) {
					this.responseMode = ResponseMode.CONVERSATION;
					System.out.println("Changement du mode de réponse de l'IA vers " + responseMode);
					JOptionPane.showMessageDialog(null, "La conversation est maintenant en mode conversation.",
							"Mode de réponse", JOptionPane.INFORMATION_MESSAGE);
				} else {
					this.responseMode = ResponseMode.JSON_ONLY;
					System.out.println("Changement du mode de réponse de l'IA vers " + responseMode);
					JOptionPane.showMessageDialog(null, "La conversation est maintenant en mode JSON_ONLY.",
							"Mode de réponse", JOptionPane.INFORMATION_MESSAGE);
				}
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null,
						"Erreur lors du changement du mode de réponse de l'IA: " + e.getMessage(), "Erreur",
						JOptionPane.ERROR_MESSAGE);

			}

		});
	}


	

	public void setAndGetCurrentChat(Chat chat) {
		Chat lastActiveChat;
		this.currentChat = chat;
		try {
			var chatID = UUIDUtils.decode(new BufferedReader(new InputStreamReader(in)).readLine());
		currentChat = (Chat) hub().indexes.byId.get(chatID);
		lastActiveChat = currentChat;
		this.currentChat = lastActiveChat;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void setChat () {
		if (this.currentChat == null) {
			if (this.chat instanceof Chat) {
				this.currentChat = (Chat) this.chat;
			} else if (this.parent instanceof Chat) {
				this.currentChat = (Chat) this.parent;
			}
			else {setAndGetCurrentChat(currentChat);}
		}
	}
		

	@ActionMethod
	@AddButtonOnKishanView
	public void resetMemory() {
		setChat();
			final Chat activeChat = this.currentChat;
			System.out.println("Réinitialisation de la mémoire pour le chat : "
					+ (activeChat != null ? activeChat.idAsText() : "aucun chat actif"));
			SwingUtilities.invokeLater(() -> {
				try {
					if (activeChat != null) {
						String chatId = activeChat.idAsText();
						var messages = MEMORY_STORE.getMessages(chatId);
						int count = (messages != null) ? messages.size() : 0;
						System.out
								.println("Nombre de messages avant suppression pour le chat " + chatId + ": " + count);
						System.out.println(" messages: " + messages);
						MEMORY_STORE.deleteMessages(chatId);
						MEMORY_STORE.deleteMessages("default_session");
						System.out.println(MEMORY_STORE.getMessages(chatId).size()
								+ " messages supprimés pour le chat : " + chatId);
						ASSISTANT_CACHE.clear();
						System.out.println(" memory reset for chat: " + MEMORY_STORE.getMessages(chatId).size()
								+ " messages remaining for chat: " + MEMORY_STORE.getMessages(chatId));
						JOptionPane.showMessageDialog(null, "La mémoire de la conversation a été réinitialisée.",
								"Réinitialisation", JOptionPane.INFORMATION_MESSAGE);
					} else {
						JOptionPane.showMessageDialog(null, "Aucune conversation active pour réinitialiser la mémoire.",
								"Information", JOptionPane.INFORMATION_MESSAGE);
					}
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null,
							"Erreur lors de la réinitialisation de la mémoire: " + e.getMessage(), "Erreur",
							JOptionPane.ERROR_MESSAGE);
				}
			});
		}
	

	// @ActionMethod
	// @AddButtonOnKishanView
	// public void SendRequestToPeerAI() {
	// SwingUtilities.invokeLater(() -> {
	// try {
	// if (ShowPeersInfo.elements.isEmpty()) {
	// JOptionPane.showMessageDialog(null,
	// "Aucun noeud AI disponible pour envoyer la requête.",
	// "Erreur",
	// JOptionPane.ERROR_MESSAGE);
	// return;
	// }
	// AiNode selectedNode = selectBestPeer(ShowPeersInfo.elements);
	// if (selectedNode == null) {
	// JOptionPane.showMessageDialog(null,
	// "Aucun noeud AI sélectionné pour envoyer la requête.",
	// "Erreur",
	// JOptionPane.ERROR_MESSAGE);
	// return;
	// }
	// String userQuestion = prompt.get();
	// if (userQuestion == null || userQuestion.trim().isEmpty()) {
	// JOptionPane.showMessageDialog(null,
	// "La question est vide. Veuillez entrer une question avant d'envoyer la
	// requête.",
	// "Erreur",
	// JOptionPane.ERROR_MESSAGE);
	// return;
	// }
	// String response = sendRequestToPeer(selectedNode, userQuestion);
	// if (response != null) {
	// result = new TextNode(hub(), "Réponse du noeud AI", response);
	// JOptionPane.showMessageDialog(null,
	// "Réponse reçue du noeud AI : " + response,
	// "Réponse",
	// JOptionPane.INFORMATION_MESSAGE);
	// } else {
	// JOptionPane.showMessageDialog(null,
	// "Aucune réponse reçue du noeud AI.",
	// "Information",
	// JOptionPane.INFORMATION_MESSAGE);
	// }
	// } catch (Exception e) {
	// JOptionPane.showMessageDialog(null,
	// "Erreur lors de l'envoi de la requête au noeud AI: " + e.getMessage(),
	// "Erreur",
	// JOptionPane.ERROR_MESSAGE);
	// }
	// });
	// }

	// public synchronized String sendRequestToPeer(AiNode aiNode, String request) {
	// if (aiNode == null) {
	// System.out.println("Peer AI node is not available");
	// return null;
	// }

	// Peer peer = aiNode.getPeer();
	// if (peer == null) {
	// System.out.println("Peer not found in neighborhood for AI node: " +
	// aiNode.name);
	// return null;
	// }

	// try {
	// byransha.network.MessageQ q = new byransha.network.MessageQ(hub().network,
	// 1);
	// hub().network.sendQ.sendObject(request, peer, msg -> {

	// msg.replyTo = q.id;
	// });
	// Message reply = q.q.poll_sync();
	// if (reply != null && reply.contentObject != null) {
	// return reply.contentObject.toString();
	// }
	// return null;
	// } catch (Exception e) {
	// System.out.println("Error sending request to peer AI node: " +
	// e.getMessage());
	// return null;
	// }
	// }





	public static List<String> getInstalledOllamaModels() {
    List<String> models = new ArrayList<>();
    String userHome = System.getProperty("user.home");
    File libraryDir = new File(userHome, ".ollama/models/manifests/registry.ollama.ai/library");
    if (libraryDir.exists() && libraryDir.isDirectory()) {
        File[] modelFolders = libraryDir.listFiles(File::isDirectory);
        if (modelFolders != null) {
            for (File modelFolder : modelFolders) {
                String modelName = modelFolder.getName();
                File[] tagFolders = modelFolder.listFiles();
                
                if (tagFolders != null && tagFolders.length > 0) {
                    for (File tagFolder : tagFolders) {
                        String tagName = tagFolder.getName();
                        models.add(modelName + ":" + tagName);
                    }
                } else {
                    models.add(modelName + ":latest");
                }
            }
        }
    }
    return models;
}

public Timer createDialogTimeoutTimer(int timeoutMillis) {
	Timer timer = new Timer(timeoutMillis, e -> {
		Window[] windows = Window.getWindows();
		for (Window window : windows) {
			if (window instanceof JDialog) {
				JDialog dialog = (JDialog) window;
				if (dialog.getContentPane().getComponentCount() == 1
						&& dialog.getContentPane().getComponent(0) instanceof JOptionPane) {
					dialog.dispose();
				}
			}
		}
	});
	timer.setRepeats(false);
	timer.start();
	return timer;
}


public void PannelComponent(JLabel messageLabel, JLabel modelLabel, JComponent modelSelector,int top, int left, int bottom, int right) {
	Timer t = createDialogTimeoutTimer(30000);
	List<String> installedModels = getInstalledOllamaModels();
	JPanel newPanel = new JPanel();
	newPanel.setLayout(new BoxLayout(newPanel, BoxLayout.Y_AXIS));
	JLabel newmessageLabel = messageLabel;
	JLabel newmodelLabel = modelLabel;
	newmodelLabel.setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right));
	JComponent newmodelSelector = modelSelector;
	JComboBox<String> comboBox = null;
	JTextField textField = null;
	if (!installedModels.isEmpty()) {
		comboBox = new JComboBox<>(installedModels.toArray(new String[0]));
		if (installedModels.contains(PRIMARY_MODEL)) {
			comboBox.setSelectedItem(PRIMARY_MODEL);
		}
		newmodelSelector = comboBox;
	} else {
		textField = new JTextField(PRIMARY_MODEL);
		newmodelSelector = textField;
	}
	 newPanel.add(newmessageLabel);
        newPanel.add(newmodelLabel);
        newPanel.add(newmodelSelector);
        int option = JOptionPane.showConfirmDialog(
                null,
                newPanel,
                "Chargement de l'IA",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
		t.stop();
            switch (option) {
                case JOptionPane.YES_OPTION:
                    String selectedModel;
                    if (comboBox != null) {
                        selectedModel = (String) comboBox.getSelectedItem();
                    } else {
                        selectedModel = textField.getText().trim();
                    }       if (selectedModel != null && !selectedModel.isEmpty()) {
                        PRIMARY_MODEL = selectedModel;
                        System.out.println("chargement du modèle : " + PRIMARY_MODEL);
                    }       break;
                case JOptionPane.NO_OPTION:
                    System.out.println("L'utilisateur a refusé le chargement de l'IA.");
                    JOptionPane.showMessageDialog(
                            null,
                            "Le chargement de l'IA a été refusé..",
                            "chargement de l'IA refusé",
                            JOptionPane.WARNING_MESSAGE
                    );  break;
            }
		
}



	public boolean afficherChargementOllama() {
		System.out.println("Affichage de la boîte de dialogue pour le chargement de l'IA...");
		if (settingModel==false) {
			PannelComponent(new JLabel("Voulez-vous charger l'IA sur votre machine locale ?"),
				new JLabel("Sélectionnez le modèle que vous souhaitez :"), new JComboBox<>(), 10, 0, 10, 0);
	}
	else if (settingModel) {
			PannelComponent(new JLabel("Voulez-vous changer le modèle de l'IA sur votre machine locale ?"),
				new JLabel("Sélectionnez le modèle que vous souhaitez :"), new JComboBox<>(), 10, 0, 10, 350);
	}
	return true;
}

	
	public void afficherAlerteOllama() {
		Timer t = createDialogTimeoutTimer(30000);
		
        JOptionPane.showMessageDialog(
            null, 
           "L'utilisation de l'IA sans serveur distant requiert l'installation d'Ollama ainsi que du modèle sur votre machine locale.", 
            "Configuration requise", 
            JOptionPane.INFORMATION_MESSAGE
        );
		t.stop();
    }


	public static AiNode selectBestPeer(java.util.List<AiNode> aiNodes) {
		if (aiNodes == null || aiNodes.isEmpty()) {
			return null;
		}

		double totalScore = 0.0;
		for (AiNode node : aiNodes) {
			totalScore += node.getScore();
		}

		double randomValue = ThreadLocalRandom.current().nextDouble() * totalScore;
		double cumulativeScore = 0.0;

		for (AiNode node : aiNodes) {
			cumulativeScore += node.getScore();
			if (cumulativeScore >= randomValue) {
				return node;
			}
		}

		return aiNodes.get(aiNodes.size() - 1);
	}

	public static double calculerAlphaAutomatique(long totalParameters, int expertCount) {
		double activeParameters;
		if (expertCount > 0) {
			activeParameters = (totalParameters / 1_000_000_000.0) * 0.4;
		} else {
			activeParameters = totalParameters / 1_000_000_000.0;
		}
		double alpha = 1.0 + (activeParameters * 0.1);
		return Math.clamp(alpha, 1.0, 10.0);
	}

	public static double recupererAlphaDepuisOllama(String ollamaUrl, String modelName) {
		try {
			HttpClient client = HttpClient.newHttpClient();
			String jsonPayload = "{\"name\": \"" + modelName + "\"}";
			HttpRequest request = HttpRequest.newBuilder().uri(URI.create(ollamaUrl + "/api/show"))
					.POST(HttpRequest.BodyPublishers.ofString(jsonPayload)).header("Content-Type", "application/json")
					.build();
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			JsonNode root = mapper.readTree(response.body());
			long totalParams = root.path("model_info").path("general.parameter_count").asLong(3_000_000_000L);
			int experts = root.path("model_info").path("general.expert_count").asInt(0);
			System.out.println("Test Alpha: " + calculerAlphaAutomatique(totalParams, experts));
			return calculerAlphaAutomatique(totalParams, experts);
		} catch (Exception e) {
			System.out.println("  Impossible de lire les specs d'Ollama, alpha par défaut = 1.0");
			return 1.0;
		}
	}

	interface ToolEnabledAssistant {
		@SystemMessage("{{system}}")
		TokenStream chat(@V("system") String systemMessage, @UserMessage String userMessage);
	}

	class AI extends Category {
	}

	public QueryIA(Element n) {
		super(n, AI.class);
		inputJSON = new JSONNode(this, n.describeAsJSON());
	}

	@Override
	public String whatItDoes() {
		return "ask AI";
	}

	@Override
	public boolean applies() {
		return true;
	}

	public static void startOllama() {
		if (Chat.NodeAIUsed) {
			try {
				System.out.println("Démarrage d'Ollama pour préchauffer le modèle...");
				HttpClient client = HttpClient.newHttpClient();
				String jsonPayload = "{\"model\": \"" + PRIMARY_MODEL + "\", \"prompt\": \"\", \"stream\": false}";
				HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:11434/api/generate"))
						.POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
						.header("Content-Type", "application/json").build();
				client.send(request, HttpResponse.BodyHandlers.discarding());
				System.out.println("Modèle préchauffé.");
			} catch (Exception e) {
				System.out.println(
						"Erreur lors du démarrage d'Ollama : ( impossible de precharger le modele) " + e.getMessage());
			}
		}
	}

	@Override
	public void impl() throws Throwable {
		ActivateListNodeResponse = false;
		try {
		setChat();
		}
		 catch (Exception e) {
			throw new IllegalStateException("QueryIA must be used within a ChatNode context");
		}
		var assistant = getOrCreateAssistant();
		var userQuestion = prompt.get();
		if (userQuestion == null || userQuestion.trim().isEmpty()) {
			result = new TextNode(hub(), "IA response", "Erreur: la question envoyée à l'IA est vide.");
			return;
		}

		// (on s'exclut du Load-Balancing)
		try {
			if (myAlpha < 0) {
				myAlpha = recupererAlphaDepuisOllama("http://localhost:11434", PRIMARY_MODEL);
			}
		} catch (Exception e) {
			System.out.println("Erreur lors de la récupération de l'alpha depuis Ollama : " + e.getMessage());
		}
		System.out.println("Envoi de la question à l'IA : " + userQuestion);
		String iaResponse;
		long startTime = System.currentTimeMillis();
		int[] tokensGeneratedCount = { 0 };
		try {
			com.fasterxml.jackson.databind.JsonNode focusedNodeJson = inputNode.describeAsJSON();
			AiResult aiResult = queryIA(assistant, focusedNodeJson, userQuestion);
			iaResponse = aiResult.text;
			tokensGeneratedCount[0] = aiResult.tokenCount;
		} catch (Exception e) {
			System.err.println("Erreur lors de l'appel à queryIA : " + e.getMessage());
			iaResponse = "Erreur interne lors de la génération.";
		} finally {
			// Recalcule notre score de vitesse et on l'annonce
			long durationMs = System.currentTimeMillis() - (startTime + (long) myPromptLagMs);
			if (durationMs > 0 && tokensGeneratedCount[0] > 0) {
				myCurrentSpeed = (tokensGeneratedCount[0] / (double) durationMs) * 1000.0;
				System.out.println("Test speed: " + myCurrentSpeed + " tokens/s");
			}

		}

		// Traiter la réponse
		if (iaResponse != null) {
			if (iaResponse.contains("```json")) {
				iaResponse = iaResponse.substring(iaResponse.indexOf("```json") + 7);
				if (iaResponse.contains("```")) {
					iaResponse = iaResponse.substring(0, iaResponse.lastIndexOf("```"));
				}
			} else if (iaResponse.startsWith("```") && iaResponse.endsWith("```")) {
				iaResponse = iaResponse.substring(3, iaResponse.length() - 3);
			}
			iaResponse = iaResponse.trim();
			if (iaResponse.startsWith("[") && iaResponse.endsWith("]")) {
				try {
					JsonNode parsed = mapper.readTree(iaResponse);
					if (parsed.isArray() && parsed.size() > 0 && parsed.get(0).isTextual()) {
						ActivateListNodeResponse = true;
						System.out.println("Activation du mode ListNode pour la réponse de l'IA");
					}
				} catch (Exception e) {
					// Ignore JSON parsing errors
				}
			}
		}
		try {
			if (responseMode == ResponseMode.CONVERSATION) {
				if (ActivateListNodeResponse) {
					try {
						JsonNode parsed = mapper.readTree(iaResponse);
						var l = new ListNode<Element>(parent, "IA numeric array", Element.class);
						for (JsonNode value : parsed) {
							String idText = value.asText().trim();
							if (idText.isEmpty())
								continue;
							Element realNode = hub().indexes.byId.getByText(idText);
							if (realNode != null) {
								l.elements.add(realNode);
							} else {
								System.out.println("-> Aucun nœud ne possède l'ID '" + idText + "' dans le graphe.");
							}
						}

						result = l;
						return;
					} catch (Exception e) {
						// Ignore JSON parsing errors
					}
				}
			} else if (responseMode == ResponseMode.JSON_ONLY) {
				if (ActivateListNodeResponse) {
					try {
						JsonNode parsed = mapper.readTree(iaResponse);
						var l = new ListNode<Element>(parent, null, "IA numeric array", Element.class);
						for (JsonNode value : parsed) {
							String idText = value.asText().trim();
							if (idText.isEmpty())
								continue;
							Element realNode = hub().indexes.byId.getByText(idText);
							if (realNode != null) {
								l.elements.add(realNode);
							} else {
								System.out.println("-> Aucun nœud ne possède l'ID '" + idText + "' dans le graphe.");
							}
						}

						result = l;
						return;
					} catch (Exception e) {
						// Ignore JSON parsing errors
					}
				}
			} else {
				result = new TextNode(hub(), "IA response", iaResponse);
				return;
			}

			var extractedJson = AiResponseAnalyser.extractFirstJsonPayload(iaResponse);
			var analysableResponse = extractedJson != null ? extractedJson : iaResponse;

			if (AiResponseAnalyser.isArrayOfNumbers(analysableResponse)) {
				JsonNode parsed = mapper.readTree(analysableResponse);
				var l = new ListNode<TextNode>(parent, null, "IA numeric array", TextNode.class);
				for (JsonNode value : parsed) {
					l.elements.add(new TextNode(this, null, "value", value.asText()));
				}
				result = l;
			} else if (AiResponseAnalyser.isDistribution(analysableResponse)) {
				var distributionNode = new DistributionNode<String>(this) {
					@Override
					public String toString() {
						return "IA distribution";
					}
				};

				JsonNode parsed = mapper.readTree(analysableResponse);
				for (var entry : parsed.properties()) {
					distributionNode.entries.addOccurence(entry.getKey(), entry.getValue().asDouble());
				}

				result = distributionNode;
			} else {
				result = new TextNode(parent,null, "IA response", iaResponse);
			}
			if (currentChat != null) {
				String chatId = currentChat.id().toBase62();
				// Récupère l'historique actuel pour ce chat spécifique
				var messages = MEMORY_STORE.getMessages(chatId);
				boolean hasToolMessages = messages.stream()
						.anyMatch(m -> m.type() == dev.langchain4j.data.message.ChatMessageType.TOOL_EXECUTION_RESULT);
				if (hasToolMessages) {
					var cleanMessages = messages.stream()
							.filter(m -> m.type() == dev.langchain4j.data.message.ChatMessageType.USER
									|| m.type() == dev.langchain4j.data.message.ChatMessageType.AI)
							.toList();
					MEMORY_STORE.updateMessages(chatId, cleanMessages);
				}
			}
		}

		finally {
			if (currentChat != null) {
				final Chat chatToAppend = currentChat;
				final Element resToAppend = result;
				SwingUtilities.invokeLater(() -> chatToAppend.append(resToAppend));
			}
		}

	}

	public void parsing (String array,String mapperString) {
		if (ActivateListNodeResponse) {
			try {
				JsonNode parsed = mapper.readTree(mapperString);
				var l = new ListNode<BNode>(parent, array, BNode.class);
				for (JsonNode value : parsed) {
					String idText = value.asText().trim();
					if (idText.isEmpty())
						continue;
					BNode realNode = hub().indexes.byId.getByText(idText);
					if (realNode != null) {
						l.elements.add(realNode);
					} else {
						System.out.println("-> Aucun nœud ne possède l'ID '" + idText + "' dans le graphe.");
					}
				}

				result = l;
			} catch (Exception e) {
				// Ignore JSON parsing errors
			}
		}
	}

	public void setResponseMode(ResponseMode responseMode) {
		this.responseMode = responseMode == null ? ResponseMode.JSON_ONLY : responseMode;
	}

	public ResponseMode getResponseMode() {
		return responseMode;
	}

	static String[] buildLlmPrompt(JsonNode inputJSON, String question, ResponseMode mode, Element inputNode) {
		var normalizedQuestion = question == null ? "" : question.trim();
		var SystemPrompt = new StringBuilder();
		SystemPrompt.append("Your personality: You are a helpful assistant specialized in exploring a data graph..\n");
		if (mode == ResponseMode.JSON_ONLY) {
			SystemPrompt.append(
					"You have access to the graph tools. Before producing the output, call the necessary tools to gather evidence. After using the tools, provide STRICTLY valid JSON .\n");
		} else {
			SystemPrompt.append("Give a conversational answer to the user \n");
		}
		SystemPrompt.append("--- SYSTEM INSTRUCTIONS FOR GRAPH AGENT ---\n");
		SystemPrompt.append("You are an AI connected to a live graph database via GraphTools.\n");
		SystemPrompt.append("You do not know the answer until you call a tool.\n\n");
		SystemPrompt.append("METHODOLOGY FOR ANY QUESTION:\n");
		SystemPrompt.append("RULES TO REMBEMBERS AND TO EXECUTE IF NEEDED AFTER AN EXECUTION");
		SystemPrompt.append(
				" - If you didnt find any result for your research at the current node, use searchByText to find the node in a different part of the graph.\n");
		SystemPrompt.append("1. FIRST STEP DECISION:\n");
		SystemPrompt.append(
				" - use searchByText to find the nodeID of the main concept in the user question. then call the appropriate tools with the nodeID to get the data requested by the user.\n");
		SystemPrompt.append(
				" - If the user asks to filter people by a property (e.g. 'né à Nice', 'born in X'), DO NOT use searchByText. Immediately call 'filterMembersByProperty' using the *current root context node ID* (provided above) to get all members, the property value , and the property name.\n");
		SystemPrompt.append(
				"	- For general details about a structure like COMRED, SIS ect..., use 'getNodeDetails' with the ID of the structure. ATTENTION dont invent names. if you don't find the structure, try with searchByText to get the nodeID and then call 'getNodeDetails'.\n");
		SystemPrompt.append(
				" - If the user is looking for a specific concept or name (e.g. 'cherche le centre X'), extract the main concept and call 'searchByText' with it.\n");
		SystemPrompt.append(
				"3. If the user asks for members or people ('qui travaille', 'membres', 'personnes'), call 'getMembersDetails' with the ID. This tool returns names, first names  for ALL members OR the members the user asks for,  (and you will ONLY return the birth cities, and emails if and ONLY if the user asks for them for ALL members OR the members the user asks for).\n");
		SystemPrompt.append("4. For general details about a node (not members), use 'getNodeDetails'.\n");
		SystemPrompt.append(
				"5. Loop through ALL relevant IDs and property tools until you have collected everything requested.\n");
		SystemPrompt.append(
				"6. Answer the user using ONLY the combined text and details returned by all your tool calls.\n\n");
		SystemPrompt.append(
				"8. If you dont find the main concept, try with searchByText to get the nodeID and then call 'getNodeDetails'.\n");

		SystemPrompt.append("CRITICAL RULES AGAINST HALLUCINATION:\n");
		SystemPrompt.append("- Most questions are in French. Answer in French.\n");
		SystemPrompt.append(
				"- MULTI-STEP MANDATE: NEVER assume an information (like birth city, age, etc.) is missing just because it wasn't in the first tool call. If a specific tool exists for that property, you MUST call it for each ID.\n");
		SystemPrompt.append(
				"- Do not use the email informations to get the name and surname of a person. Get them from the node details.\n");
		SystemPrompt.append(
				"- If the user asks for members ('membres' or 'qui travaille chez'), use 'searchByText' then 'getMembersDetails'. The getMembersDetails tool already returns nom, prénom, ville de naissance, and emails. DO NOT call getNodeDetails for listing members.\n");
		SystemPrompt.append(
				"- When the users asks for \"tout\", list all that he requested, using the tools sequentially to get the data.\n");
		SystemPrompt.append(
				"- Dont repeat the same information twice, for exemple if you have already given the name of a person, do not give it again when you list the members.\n");
		SystemPrompt.append(
				"- STRICT RULE FOR NAMES: Output ONLY the exact names returned by the tools. NEVER invent, guess, or add a first name (prénom) if it is not explicitly written in the tool output.\n");
		SystemPrompt.append(
				"- STRICT FILTERING: When asked to find people matching a condition (e.g. born in Nice), read the tool output carefully. In your final answer, ONLY list the exact people who match the condition. DO NOT list people who do not match, and do NOT mention them at all.\n");
		SystemPrompt.append(
				"- EXAMPLE: If the tool returns 'Martin', you must write 'Martin'. DO NOT write 'Jean Martin' or 'Pierre Martin'. Adding an unprovided first name is strictly forbidden.\n");
		SystemPrompt.append("- If a tool returns no results, say clearly that you found nothing in the database.\n");
		SystemPrompt.append("- DO NOT explain your tools or say 'I don't have access'. Just give the final data.\n");

		SystemPrompt.append("--- END OF INSTRUCTIONS ---\n\n");
		SystemPrompt.append("--- FINAL OUTPUT REQUIREMENT ---\n");
		if (mode == ResponseMode.CONVERSATION) {
			SystemPrompt.append("Provide a short explanation.\n");
			SystemPrompt.append(
					"For the final output, return what the user asked for, and ONLY that. Do not add any extra information or context.\n");
		} else {
			SystemPrompt.append(
					"Output STRICTLY valid JSON ONLY. Do NOT output any intro text, summary, or markdown formatting like ```json.\nCRITICAL RULE: You must NOT use markdown code blocks. Start your response directly with { or [.\n");
		}
		var UserPrompt = new StringBuilder();
		UserPrompt.append("--- USER QUESTION ---\n");
		UserPrompt.append(normalizedQuestion).append("\n\n");
		if (inputNode != null) {
			UserPrompt.append("The current root context node is: ").append(inputNode.idAsText())
					.append(inputNode.getClass().getSimpleName()).append("\n");
		}
		return new String[] { SystemPrompt.toString(), UserPrompt.toString() };
	}

	public static class AiResult {
		public String text;
		public int tokenCount;

		public AiResult(String text, int tokenCount) {
			this.text = text;
			this.tokenCount = tokenCount;
		}
	}

	protected AiResult queryIA(ToolEnabledAssistant assistant, JsonNode inputJSON, String question) throws Exception {
		if (!ollamaVerified) {
			if (!OllamaRequire.checkRequirements()) {
				System.out.println(" Ollama n'est pas installé impossible de faire une requête IA.");
				return new AiResult("Erreur: Ollama n'est pas installé", 0);
			}
			ollamaVerified = true;
		}

		var prompts = buildLlmPrompt(inputJSON, question, responseMode, inputNode);

		// Synchronous fallback wrapper since `impl()` doesn't support async streams
		// yet.
		java.util.concurrent.CompletableFuture<AiResult> future = new java.util.concurrent.CompletableFuture<>();

		long requestStartTime = System.currentTimeMillis();
		boolean[] isFirstToken = { true };

		assistant.chat(prompts[0], prompts[1]).onNext(token -> {
			if (isFirstToken[0]) {
				isFirstToken[0] = false;
				myPromptLagMs = System.currentTimeMillis() - requestStartTime;
				System.out.println("Test prompt lag: " + myPromptLagMs + " ms");
			}
			System.out.print(token);
			System.out.flush(); // FORCE L'AFFICHAGE IMMEDIAT DU TOKEN
		}).onComplete(response -> {
			System.out.println(); // newline after stream
			int tokenCount = 0;
			if (response.tokenUsage() != null && response.tokenUsage().outputTokenCount() != null) {
				tokenCount = response.tokenUsage().outputTokenCount();
			}
			future.complete(new AiResult(response.content().text(), tokenCount));
		}).onError(error -> {
			System.err.println("\n  Erreur pendant le stream IA : " + error.getMessage());
			error.printStackTrace();
			future.completeExceptionally(error);
		}).start();

		return future.join();
	}

	private ToolEnabledAssistant getOrCreateAssistant() throws IOException {
		String currentOllamaUrl = "http://localhost:11434";
		setChat();
		
		try {
			var aiNodes = ShowPeersInfo.get();
			var selectedNode = selectBestPeer(aiNodes);
			if (selectedNode != null) {
				currentOllamaUrl = "http://" + selectedNode.address.getHostAddress() + ":11434";
				System.out.println(
						"Utilisation du noeud AI sélectionné : " + selectedNode.name + " (" + currentOllamaUrl + ")");
			} else {
				System.out.println("Aucun noeud AI disponible, utilisation de l'instance locale d'Ollama.");
			}

		} catch (Exception e) {
			System.out.println("Pas de noeuds disponibles, utilisation de l'instance locale d'Ollama.");
		}
		final String selectedOllamaUrl = currentOllamaUrl;
		String chatId = (currentChat != null) ? currentChat.idAsText() : "default_session";
		var cacheKey = selectedOllamaUrl + "|" + PRIMARY_MODEL + "|" + chatId;
		return ASSISTANT_CACHE.computeIfAbsent(cacheKey, key -> {
			var model = getOrCreateModel(selectedOllamaUrl);
			ChatMemoryProvider chatMemoryProvider = MemoryId -> MessageWindowChatMemory.builder()
					.id(chatId)
					.maxMessages(MAX_MESSAGES)
					.chatMemoryStore(MEMORY_STORE)
					.build();
			return AiServices.builder(ToolEnabledAssistant.class)
					.streamingChatLanguageModel(model)
					.tools(new GraphTools(inputNode))
					.chatMemoryProvider(chatMemoryProvider)
					.build();
		});
	}

	private OllamaStreamingChatModel getOrCreateModel(String ollamaUrl) {
		var cacheKey = ollamaUrl + "|" + PRIMARY_MODEL;

		return MODEL_CACHE.computeIfAbsent(cacheKey, key -> OllamaStreamingChatModel.builder()
				.baseUrl(ollamaUrl)
				.modelName(PRIMARY_MODEL)
				.numCtx(32768)
				.topP(0.95)
				.topK(20)
				.temperature(0.5)
				.timeout(java.time.Duration.ofMinutes(5))
				.logRequests(false) // Mettre à true pour déboguer
				.logResponses(false) // Mettre à true pour déboguer
				.build());
	}

}
