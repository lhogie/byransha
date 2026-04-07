package byransha.graph.view;

import java.awt.Dimension;
import java.lang.reflect.Field;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import com.fasterxml.jackson.databind.JsonNode;

import byransha.graph.BNode;
import byransha.graph.action.list.ListNode;
import byransha.nodes.lab.DynamicValuedNode;
import byransha.nodes.system.ChatNode;
import byransha.ui.swing.ChatSheet;
import byransha.ui.swing.ErrorIndicator;
import byransha.ui.swing.TextDisplayComponent;
import byransha.ui.swing.Utils;
import byransha.ui.swing.WrapPanel;
import byransha.util.Stop;

public class KishanView extends NodeView<BNode> {
	public KishanView(BNode node) {
		super(node.g, node);
	}

	@Override
	public String whatItShows() {
		return "editors for properties";
	}

	@Override
	public JsonNode jsonView() {
		return viewedNode.describeAsJSON();
	}

	@Override
	public void writeTo(ChatSheet sheet) {
		viewedNode.forEachOutInFields(viewedNode.getClass(), BNode.class, (f, out, readOnly) -> {
			if (out != viewedNode) {
				if (out instanceof DynamicValuedNode otf) {
					out = otf.exec();
				}

				if (out == viewedNode)
					throw new IllegalStateException();

				fillLine(sheet.currentLine, f, (ChatSheet) sheet, out);
				sheet.newLine();
			}
		});
	}

	private void fillLine(WrapPanel currentLine, Field f, ChatSheet sheet, BNode out) {
		var fieldNameComponent = new TextDisplayComponent(g.translator, f.getName() + ":");
		fieldNameComponent.setPreferredSize(new Dimension(60, fieldNameComponent.getPreferredSize().height));
		fieldNameComponent.setToolTipText(f.getName());
		Utils.idDropTarget(g, fieldNameComponent, dn -> viewedNode.set(f, dn));
		currentLine.add(fieldNameComponent);

		
		if (out != null) {
			currentLine.add(Utils.idShower(out, 18, 2, ((ChatSheet) sheet).chat));
			currentLine.add(new ErrorIndicator(out));
			var innerSheet = new ChatSheet(sheet.chat);
			out.getViewForKishanView().writeToWithErrors(innerSheet);
			currentLine.add(innerSheet);
//			innerSheet.setPreferredSize(innerSheet.getParent().getPreferredSize());
		}

		{
			var popup = new JPopupMenu();
			var setToNull = new JMenuItem("unset");
			setToNull.addActionListener(e -> {
				try {
					viewedNode.set(f, null);
					sheet.currentLine.removeAll();
					fillLine(sheet.currentLine, f, sheet, out);
					sheet.doLayout();
					sheet.revalidate();
				} catch (Throwable e1) {
					error(e1);
				}
			});
			var replace = new JMenuItem("see candidates");
			replace.addActionListener(e -> {
				var list = new ListNode(g, "all nodes of class " + f.getType().getName());
				g.indexes.byClass.forEachNodeAssignableTo((Class) f.getType(), a -> {
					list.elements.add(a);
					return Stop.no;
				});
				var newChat = new ChatNode(currentUser());
				newChat.append(list);
			});

			if (!this.readOnly) {
				popup.add(out == null ? replace : setToNull);
			}

			fieldNameComponent.setComponentPopupMenu(popup);
		}
	}

	public <N extends BNode> List<N> get(Class<N> c) {
		return g.indexes.byClass.m.get(c).stream().map(n -> (N) n).toList();
	}

	@Override
	protected boolean allowsEditing() {
		return true;
	}
}