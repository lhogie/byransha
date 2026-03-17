package byransha.ui.swing;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;

import byransha.graph.BNode;
import byransha.graph.view.AvailableActionsView;
import byransha.graph.view.ErrorsView;
import byransha.nodes.system.ChatNode;

public class ChatSheet extends ScrollablePanel {

	JPanel currentFlow = createNewFlow();
	public final ChatNode chat;
	public final JScrollPane scroll;

	public ChatSheet(ChatNode chat) {
		super();
		this.chat = chat;
		var bl = new BoxLayout(this, BoxLayout.Y_AXIS);
		setLayout(bl);
		this.scroll = new JScrollPane(this);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setBorder(new TitledBorder(chat.prettyName()));

		chat.get().forEach(c -> addNode(c));
	}

	int colorIndex;
	Color[] backgroundColors = new Color[] { Color.white, new Color(0xEE, 0xEE, 0xEE, 10) };

	void addNode(BNode n) {
		int nToRemove = 6;

		if (getComponentCount() > nToRemove) {
			for (int i = 0; i < nToRemove; ++i) {
				remove(getComponentCount() - 1);
			}
		}
		
		addSeparator();

//		c = backgroundColors[colorIndex++ % backgroundColors.length];
		this.bgColor = n.getBackgroundColor();

		newLine();
		appendToCurrentFlow("\"" + n.prettyName() + "\" is " + n.whatIsThis() + ". Its ID is");
		appendToCurrentFlow(Utils.idShower(n));
		newLine();
		newLine();
		n.views().getFirst().writeTo(this);
		newLine();

		var err = n.findView(ErrorsView.class);

		if (err.showInViewList()) {
			err.writeTo(this);
			newLine();
		}

		newLine();
		newLine();
		appendToCurrentFlow("What do you want to do?");
		newLine();

		n.findView(AvailableActionsView.class).writeTo(this);
		newLine();
		end();

		new Thread(() -> swipeDownInOneSecond()).start();
	}

	public void swipeDownInOneSecond() {
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		var scrollPane = ((JScrollPane) getParent().getParent());
		JScrollBar vertical = scrollPane.getVerticalScrollBar();
		int nbSteps = vertical.getMaximum() - vertical.getValue();
//		System.out.println(nbSteps);
		int pauseDuration = 1000 / nbSteps;

		for (int i = vertical.getValue(); i < vertical.getMaximum(); ++i) {
//			System.out.println(vertical.getValue() + " -> " + vertical.getMaximum());
			vertical.setValue(i);
			revalidate();
			repaint();

			try {
				Thread.sleep(pauseDuration);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void appendToCurrentFlow(String s) {
		var tf = new JTextArea();
		tf.setText(s);
		tf.setEditable(false);
		tf.setBackground(null);
		tf.setBorder(null);
		tf.setOpaque(false);
		appendToCurrentFlow(tf);
	}

	public void appendToCurrentFlow(JComponent c) {
//		c.setBackground(null);
//		c.setBorder(null);
//		c.setOpaque(true);
		currentFlow.add(c);
	}

	public void newLine() {
		add(currentFlow);
		currentFlow = createNewFlow();
	}

	public Color bgColor;

	private JPanel createNewFlow() {
		var wp = new WrapPanel();
		wp.setBackground(bgColor);
		wp.setBorder(null);
		wp.setOpaque(true);
		return wp;
	}

	public void clear() {
		removeAll();
		revalidate();
		repaint();
	}

	public void end() {
		addSeparator();
		add(Box.createVerticalGlue());
	}
	public void addSeparator() {
		var separator = new JSeparator();
		separator.setPreferredSize(new Dimension(Integer.MAX_VALUE, 1));
		add(separator);
	}

}
