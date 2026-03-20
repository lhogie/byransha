package byransha.nodes.lab.stats;

import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import org.jfree.graphics2d.svg.SVGUtils;

public class DraggableChart {

	/**
	 * Makes a ChartPanel draggable to the file system. Dropping it onto a folder or
	 * the desktop creates a PNG file.
	 */
	public static void makeFileDraggable(ChartPanel chartPanel, String filename) {
		DragSource.getDefaultDragSource().createDefaultDragGestureRecognizer(chartPanel, DnDConstants.ACTION_COPY,
				e -> {
					try {
						Transferable transferable = new ChartFileTransferable(chartPanel.getChart(),
								chartPanel.getWidth(), chartPanel.getHeight(), filename);
						e.startDrag(DragSource.DefaultCopyDrop, transferable);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				});
	}

	// ── Transferable ──────────────────────────────────────────────────────────

	static class ChartFileTransferable implements Transferable {

		// The file list flavor is what the OS file manager accepts
		private static final DataFlavor FILE_LIST_FLAVOR = DataFlavor.javaFileListFlavor;

		// Also offer the image directly for apps that accept image drops
		private static final DataFlavor IMAGE_FLAVOR = DataFlavor.imageFlavor;

		private final JFreeChart chart;
		private final int width, height;
		private final String filename;

		ChartFileTransferable(JFreeChart chart, int w, int h, String filename) {
			this.chart = chart;
			this.width = w > 0 ? w : 800;
			this.height = h > 0 ? h : 600;
			this.filename = filename.endsWith(".svg") ? filename : filename + ".svg";
		}

		@Override
		public DataFlavor[] getTransferDataFlavors() {
			return new DataFlavor[] { FILE_LIST_FLAVOR, IMAGE_FLAVOR };
		}

		@Override
		public boolean isDataFlavorSupported(DataFlavor flavor) {
			return flavor.equals(FILE_LIST_FLAVOR) || flavor.equals(IMAGE_FLAVOR);
		}

		@Override
		public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {

			if (flavor.equals(FILE_LIST_FLAVOR)) {
				Path tmp = Files.createTempFile("chart-", ".svg");
				tmp.toFile().deleteOnExit();

				SVGGraphics2D svg = new SVGGraphics2D(width, height);
				chart.draw(svg, new Rectangle(width, height));
				SVGUtils.writeToSVG(tmp.toFile(), svg.getSVGElement());

				return List.of(tmp.toFile());
			}
			throw new UnsupportedFlavorException(flavor);
		}

		public static void saveChartAsSvg(JFreeChart chart, int width, int height, Path path) throws IOException {
			SVGGraphics2D svg = new SVGGraphics2D(width, height);
			chart.draw(svg, new Rectangle(width, height));
			SVGUtils.writeToSVG(path.toFile(), svg.getSVGElement());
		}
	}
}