package byransha.graph.view;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.io.IOException;
import java.util.List;

import javax.swing.JTextField;

import com.fasterxml.jackson.databind.JsonNode;

import butils.Base62;
import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.ui.swing.ChatSheet;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class KishanView extends NodeView<BNode> {
	public KishanView(BGraph g, BNode node) {
		super(g, node);
	}

	@Override
	public String whatItShows() {
		return "editors for properties";
	}

	@Override
	public JsonNode toJSON() {
		return viewedNode.toJSONNode();
	}

	@Override
	public void writeTo(ChatSheet pane) {
		viewedNode.forEachOutInFields((f, out, readOnly) -> {
			if (out != viewedNode) {
				var idTf = new JTextField();
				idTf.setText(out.idAsText());
				idTf.setEditable(readOnly);
				pane.appendToCurrentFlow(idTf);

				var jumpButton = out.createJumpComponent();
				jumpButton.setText(f.getName());
				jumpButton.setToolTipText(f.getName());
				pane.appendToCurrentFlow(jumpButton);
				new DropTarget(jumpButton, new DropTargetAdapter() {
					@Override
					public void dragOver(DropTargetDragEvent dtde) {

					}

					@Override
					public void drop(DropTargetDropEvent e) {
						try {
							var droppedNode = node(e);

							if (droppedNode.getClass().isAssignableFrom(f.getType())) {
								e.acceptDrop(DnDConstants.ACTION_COPY);
								System.out.println("drag'n drop OK");
								viewedNode.set(f, droppedNode);
							} else {
								System.out.println("drag'n drop NOOO");
								e.rejectDrop();
							}
							e.dropComplete(true);
						} catch (Exception ex) {
							e.dropComplete(false);
							error(ex);
						}
					}

					private BNode node(DropTargetDropEvent e) throws UnsupportedFlavorException, IOException {
						String text = (String) e.getTransferable().getTransferData(DataFlavor.stringFlavor);
						long id = Base62.decode(text);
						return g.indexes.byId.get(id);
					}
				});

				out.getKishanView().writeTo(pane);
				pane.newLine();
			}
		});
	}

	public List<BNode> alternatives(java.lang.reflect.Field f) {
		return get((Class<BNode>) (Class) f.getClass());
	}

	public <N extends BNode> List<N> get(Class<N> c) {
		return g.indexes.byClass.m.get(c).stream().map(n -> (N) n).toList();
	}

	@Override
	public void writeTo(Pane lines) {
		viewedNode.forEachOutInFields((f, out, readOnly) -> {
			if (out != viewedNode) {
				var flow = new TextFlow();
				flow.getChildren().add(new Text(readOnly ? "r-" : "rw"));
				var jt = (JumpTo) out.findView(JumpTo.class);
				jt.setLabel(f.getName());
				jt.writeTo(flow);
				out.getKishanView().writeTo(flow);
				lines.getChildren().add(flow);
			}
		});
	}

	@Override
	protected boolean allowsEditing() {
		return true;
	}
}