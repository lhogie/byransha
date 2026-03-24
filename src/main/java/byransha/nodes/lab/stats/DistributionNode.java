package byransha.nodes.lab.stats;

import java.awt.Color;
import java.awt.Dimension;
import java.util.HashSet;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.graph.view.NodeView;
import byransha.ui.swing.Sheet;

public abstract class DistributionNode<V> extends BNode {
	public DistributionNode(BGraph g) {
		super(g);
	}

	public static class Entry<V> {
		public V element;
		public double n;
	}

	@Override
	public void createViews() {
		cachedViews.elements.add(new DistributionView<>(g, this));
		super.createViews();
	}

	public static class Distribution<V> extends HashSet<Entry<V>> {
		public void addOccurence(V a, double n) {
			var e = getEntry(a);

			if (e == null) {
				add(e = new Entry<>());
				e.element = a;
				e.n = n;
			} else {
				e.n += n;
			}
		}

		public Entry<V> getEntry(V a) {
			for (var e : this) {
				if (e == a) {
					return e;
				}
			}

			return null;
		}

		public ObjectNode toJSON() {
			var r = new ObjectNode(factory);
			forEach(e -> r.put(e.element.toString(), e.n));
			return r;
		}
	}

	public final Distribution<V> entries = new Distribution<>();

	@Override
	public String whatIsThis() {
		return "a distribution";
	}

	public static class DistributionView<V> extends NodeView<DistributionNode<V>> {

		public DistributionView(BGraph g, DistributionNode<V> node) {
			super(g, node);
		}

		@Override
		public void writeTo(Sheet sheet) {
			DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
			viewedNode.entries.forEach(e -> dataset.setValue(e.element.toString(), e.n));

			JFreeChart chart = ChartFactory.createPieChart("", dataset, false, true, false);
			chart.setBackgroundPaint(new Color(0, 0, 0, 0)); // chart background
			chart.setBackgroundImageAlpha(0f);
			chart.setBorderVisible(false);

			PiePlot plot = (PiePlot) chart.getPlot();
			plot.setBackgroundPaint(new Color(0, 0, 0, 0)); // plot background
			plot.setOutlineVisible(false); // remove plot border
			plot.setShadowPaint(new Color(0, 0, 0, 0)); // remove shadow

// Make ChartPanel transparent too
			ChartPanel chartPanel = new ChartPanel(chart);
			chartPanel.setOpaque(false);
			chartPanel.setBackground(new Color(0, 0, 0, 0));
			chartPanel.setOpaque(false);
			chartPanel.setPreferredSize(new Dimension(300, 300));
			DraggableChart.makeFileDraggable(chartPanel, viewedNode.prettyName());
			sheet.appendToCurrentLine(chartPanel);
		}

		@Override
		public JsonNode jsonView() {
			return viewedNode.entries.toJSON();
		}

		@Override
		public String whatItShows() {
			return "a distribution";
		}

		@Override
		protected boolean allowsEditing() {
			return false;
		}

	}
}
