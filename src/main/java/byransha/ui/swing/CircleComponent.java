package byransha.ui.swing;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;

/**
 * A Swing component that renders a filled circle with customizable size and
 * color.
 *
 * Usage: CircleComponent circle = new CircleComponent(60, Color.RED);
 * CircleComponent circle = new CircleComponent(60, Color.BLUE, Color.BLACK, 2);
 */
public class CircleComponent extends JComponent {

	private int diameter;
	private Color fillColor;
	private Color borderColor;
	private int borderWidth;
	private boolean antialias = true;

	// ── Constructors ──────────────────────────────────────────────────────────

	public CircleComponent(int diameter, Color fillColor) {
		this(diameter, fillColor, null, 0);
	}

	public CircleComponent(int diameter, Color fillColor, Color borderColor, int borderWidth) {
		this.diameter = diameter;
		this.fillColor = fillColor;
		this.borderColor = borderColor;
		this.borderWidth = borderWidth;
		setOpaque(false);
		updatePreferredSize();
		ToolTipManager.sharedInstance().registerComponent(this);
	}

	@Override
	public String getToolTipText() {
		String tip = super.getToolTipText();
		if (tip == null) {
			for (Component c = getParent(); c instanceof JComponent jc; c = c.getParent()) {
				tip = jc.getToolTipText();
				if (tip != null)
					return tip;
			}
		}
		return tip;
	}
	// ── Painting ──────────────────────────────────────────────────────────────

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g.create();

		if (antialias) {
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		}

		int pad = borderWidth;
		int d = diameter - pad * 2;
		int x = (getWidth() - d) / 2;
		int y = (getHeight() - d) / 2;

		Ellipse2D.Float ellipse = new Ellipse2D.Float(x, y, d, d);

		// Fill
		if (fillColor != null) {
			g2.setColor(fillColor);
			g2.fill(ellipse);
		}

		// Border
		if (borderColor != null && borderWidth > 0) {
			g2.setColor(borderColor);
			g2.setStroke(new BasicStroke(borderWidth));
			g2.draw(ellipse);
		}

		g2.dispose();
	}

	// ── Sizing ────────────────────────────────────────────────────────────────

	private void updatePreferredSize() {
		setPreferredSize(new Dimension(diameter, diameter));
		setMinimumSize(new Dimension(diameter, diameter));
		setMaximumSize(new Dimension(diameter, diameter));
	}

	// ── Getters / Setters ─────────────────────────────────────────────────────

	public int getDiameter() {
		return diameter;
	}

	public void setDiameter(int diameter) {
		this.diameter = diameter;
		updatePreferredSize();
		revalidate();
		repaint();
	}

	public Color getFillColor() {
		return fillColor;
	}

	public void setFillColor(Color fillColor) {
		this.fillColor = fillColor;
		repaint();
	}

	public Color getBorderColor() {
		return borderColor;
	}

	public void setBorderColor(Color borderColor) {
		this.borderColor = borderColor;
		repaint();
	}

	public int getBorderWidth() {
		return borderWidth;
	}

	public void setBorderWidth(int borderWidth) {
		this.borderWidth = borderWidth;
		repaint();
	}

	public boolean isAntialias() {
		return antialias;
	}

	public void setAntialias(boolean antialias) {
		this.antialias = antialias;
		repaint();
	}

	// ── Demo ──────────────────────────────────────────────────────────────────

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			JFrame frame = new JFrame("CircleComponent Demo");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setBackground(new Color(30, 30, 40));

			JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
			panel.setBackground(new Color(30, 30, 40));

			// Plain circles
			panel.add(new CircleComponent(40, new Color(0xFF6B6B)));
			panel.add(new CircleComponent(60, new Color(0xFFB86C)));
			panel.add(new CircleComponent(80, new Color(0xF1FA8C)));
			panel.add(new CircleComponent(60, new Color(0x50FA7B)));
			panel.add(new CircleComponent(40, new Color(0x8BE9FD)));

			// Circles with borders
			panel.add(new CircleComponent(60, new Color(0x6272A4), new Color(0xBD93F9), 3));
			panel.add(new CircleComponent(80, new Color(0x44475A), new Color(0xFF79C6), 4));

			// Slider to resize a circle live
			CircleComponent live = new CircleComponent(50, new Color(0xFF5555), Color.WHITE, 2);
			JSlider slider = new JSlider(10, 150, 50);
			slider.setBackground(new Color(30, 30, 40));
			slider.setForeground(Color.WHITE);
			slider.addChangeListener(e -> live.setDiameter(slider.getValue()));

			JPanel livePanel = new JPanel(new BorderLayout(10, 10));
			livePanel.setBackground(new Color(30, 30, 40));
			livePanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
			livePanel.add(new JLabel("Resize:") {
				{
					setForeground(Color.WHITE);
				}
			}, BorderLayout.NORTH);
			livePanel.add(slider, BorderLayout.CENTER);
			livePanel.add(live, BorderLayout.SOUTH);

			frame.add(panel, BorderLayout.CENTER);
			frame.add(livePanel, BorderLayout.SOUTH);
			frame.pack();
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
		});
	}
}