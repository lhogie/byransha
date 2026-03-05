package byransha;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.nodes.lab.I3S;
import byransha.ui.ShellServer;
import byransha.ui.javafx.ByText;
import byransha.ui.swing.SwingFrontend;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

public class Main extends Application {

	public static void main(String[] args) throws Throwable {
		// java.awt.Toolkit.getDefaultToolkit();
//		Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());
		var argMap = mapArgs(args);

		File d = new File(argMap.getOrDefault("-directory", System.getProperty("user.home") + "/.byransha/"));
		var g = new BGraph(d);
		g.application = (BNode) Class.forName(argMap.getOrDefault("appClass", I3S.class.getName()))
				.getConstructor(BGraph.class).newInstance(g);

		g.nodeCreator.addBusinessClassesIn(g.application.getClass().getPackage());

//		new WebServer(g, Integer.parseInt(argMap.getOrDefault("--web-port", "8080")));
		new SwingFrontend(g);
		new ShellServer(g, Integer.parseInt(argMap.getOrDefault("--telnet-port", "1000")));
		// new JavaFXFrontend(g);

		g.eventList.add(new CreateNewPerson("Luc"));
		g.eventList.add(new CreateNewPerson("Dylan"));
		g.eventList.add(new CreateNewPerson("Sophie"));

		System.out.println("playing events");
		g.eventList.goToNow(e -> System.out.println("event: " + e));

		launch(args);
	}

	private static Map<String, String> mapArgs(String... args) {
		var r = new HashMap<String, String>();
		List.of(args).stream().map(a -> a.split("=")).forEach(a -> r.put(a[0], a[1]));
		return r;
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		TextFlow textFlow = new TextFlow();
		textFlow.setTextAlignment(TextAlignment.LEFT);
		//textFlow.setLineSpacing(5.0);
		//textFlow.setPrefWidth(200); // Forces wrapping

		Scene scene = new Scene(textFlow);

		primaryStage.setTitle("Byransha");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
}
