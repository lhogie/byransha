package byransha;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.nodes.lab.I3S;
import byransha.ui.ShellServer;
import byransha.ui.javafx.JavaFXFrontend;
import byransha.ui.swing.ScrollablePanel;
import byransha.ui.swing.SwingFrontend;
import byransha.ui.swing.WrapPanel;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {
	static BGraph g;
	  public static void main(String[] args) throws Throwable {
		
		  JFrame frame = new JFrame("BoxLayout + FlowLayout Demo");
          frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
          frame.setSize(500, 400);

          ScrollablePanel mainPanel = new ScrollablePanel();
          mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

          String[][] buttonLabels = {
              {"Alpha", "Beta", "Gamma"},
              {"One", "Two", "Three"},
              {"Red", "Green", "Blue"},
              {"Sun", "Moon", "Star"},
              {"Play", "Pause", "Stop"}
          };

          for (int i = 0; i < 5; i++) {
              WrapPanel rowPanel = new WrapPanel();
              rowPanel.setBorder(BorderFactory.createTitledBorder("Row " + (i + 1)));
              for (String label : buttonLabels[i]) {
                  rowPanel.add(new JButton(label));
              }
              mainPanel.add(rowPanel);
          }

          mainPanel.add(Box.createVerticalGlue());

          JScrollPane scrollPane = new JScrollPane(mainPanel);
          scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
          scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

          frame.add(scrollPane);
          frame.setLocationRelativeTo(null);
          frame.setVisible(true);
		
		
		// java.awt.Toolkit.getDefaultToolkit();
//		Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());
		var argMap = mapArgs(args);

		File d = new File(argMap.getOrDefault("-directory", System.getProperty("user.home") + "/.byransha/"));
		g = new BGraph(d);
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

		//launch(args);
	}

	private static Map<String, String> mapArgs(String... args) {
		var r = new HashMap<String, String>();
		List.of(args).stream().map(a -> a.split("=")).forEach(a -> r.put(a[0], a[1]));
		return r;
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		var vbox = new VBox();
		primaryStage.setScene(new Scene(vbox));
		primaryStage.setWidth(0);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		var width = (9 * screenSize.height) / 16;
		var size = new Dimension(width, screenSize.height);
		var location = new Point((screenSize.width - width) / 2, 0);
		primaryStage.setWidth(size.getWidth());
		primaryStage.setHeight(size.getHeight());
		primaryStage.setX(location.x);
		primaryStage.setY(location.y);
		primaryStage.setTitle("Byransha v" + g.byransha.version.get());
		primaryStage.show();

		new JavaFXFrontend(g, vbox);
	}
}
