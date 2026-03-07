package byransha.ui.swing;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.LayoutManager;

import javax.swing.JButton;
import javax.swing.JPanel;

public class ClosablePanel extends JPanel {

	public interface CloseListener {
		void onClose(ClosablePanel panel);
	}

	private CloseListener closeListener;

	public ClosablePanel (JPanel content) {
		setLayout(new BorderLayout());

		add(content, BorderLayout.CENTER);

		// Close button in top-right
		JButton closeBtn = new JButton("✕");
		closeBtn.setMargin(new Insets(0, 4, 0, 4));
		closeBtn.setFocusable(false);
		closeBtn.addActionListener(e -> {
			if (closeListener != null)
				closeListener.onClose(this);
		});

		setCloseListener(p -> {
			Container parent = p.getParent();
			parent.remove(p);
			parent.revalidate();
			parent.repaint();
		});

		JPanel topBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 2, 2));
		topBar.add(closeBtn);
		add(topBar, BorderLayout.NORTH);
	}

	public void setCloseListener(CloseListener listener) {
		this.closeListener = listener;
	}
}