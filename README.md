Install: 
Please download the self-contained JAR file from [here](https://webusers.i3s.unice.fr/~hogie/software/byransha//downloads/bin/byransha.jar) and run it with the command: `java -jar byransha.jar`



**Byransha** is a Java-based middleware and runtime for building graph-oriented business applications. At its core, it treats all data as nodes in a navigable object graph: every entity, value, user action, and system service is an instance of a base `Element` class wired together through parent-child and referential relationships.

Key architectural traits visible in the source:

* **Graph-native data model** — The entire application state lives in memory as a graph of `Element` nodes indexed by ID and class. Nodes expose their fields and methods as navigable “outs,” and the framework provides BFS traversal, inverse relations, and reflective class metadata.
* **Event sourcing & persistence** — State changes are recorded in an append-only `EventList`  enabling time-travel and replay. 
* **Multi-modal frontends** — It ships with a Swing-based “chat” UI where users explore the graph through conversational panels, plus Telnet server for local text access. A web layer exists but is secondary in the current codebase.
* **Integrated AI layer** — Uses LangChain4j + Ollama to embed local LLM inference directly into the graph. The  action exposes a tool-enabled assistant  that can search nodes, retrieve details, and traverse relationships so the model answers questions grounded in live graph data rather than hallucinating.
* **Networking & security** — Includes a peer-to-peer networking stack (`Network`, `TCPNode`, `PeerManager`) with encrypted messaging (BouncyCastle), public-key exchange, and message queues for distributed setups.
* **Access control** — Role-based permissions (`User`, `AdminRole`, `VisitorRole`) govern visibility and edit rights per node.
* **Auto-update & deployment** — The runtime can self-update by downloading new JAR binaries and optionally restarting; it also installs systemd service scripts and desktop shortcuts.

In practice, Byransha acts as a self-contained “living database” server: developers model business domains as Java classes extending `Element` or `BusinessElement`, the framework automatically indexes them, exposes graph navigation actions (search, export, jump, AI query), and renders them through a hybrid Swing/chat interface. The included `LabApplication` (with entities like `Lab`, `Genre`, `Status`) suggests its original target is academic/institutional information management, though the framework is generic.


# Team
- Luc Hogie (CNRS Research Engineer, leader and main contributor)
- Dylan Malaussena (Master 2 student, work on AI-powered data analysis)
- Alexandre Boutrik (Master 1 student, work on security)

## former members
- Charles-Axelle Essaga
- Mourtada-Essadik Ennouni
- Kishan Turpin
- Quentin Nicolini
- Lamyae Fakir
- Mathéo Balazuc
- Maxime Céleste
- Matthias Carré
- Nour El Bazzal
- Mourad Haddoudi
