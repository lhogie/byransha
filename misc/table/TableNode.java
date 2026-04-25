package byransha.graph.action.table;

import java.util.List;
import java.util.function.BiConsumer;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.graph.action.Export.CSVData;
import byransha.util.ListenableList;

public class TableNode extends BNode {
	public static class Column extends ListenableList<BNode> {
		public String header;
	}

	String label;
	final public ListenableList<Column> columns = new ListenableList<>();
	private final ListenableList<BNode> selection = new ListenableList<>();

	public TableNode(BGraph g, String label) {
		super(g);
		this.label = label;

		columns.addChangeListener(new ListenableList.ChangeListener<Column>() {

			@Override
			public void onAdd(Column column) {
				changeListeners.forEach(l -> l.changed(TableNode.this));
			}

			@Override
			public void onRemove(Column column) {
				changeListeners.forEach(l -> l.changed(TableNode.this));
			}
		});

		selection.addChangeListener(listener);
	}

	public BNode get(int col, int row) {
		return columns.get(col).get(row);
	}

	@Override
	public void forEachOut(BiConsumer<BNode, String> consumer) {
		super.forEachOut(consumer);
		forEachOutInContent((i, o) -> consumer.accept(o, "" + i));
	}

	@Override
	public void createViews() {
		cachedViews.values.add(new TableNodeView(g, this));
		super.createViews();
	}

	@Override
	public void createActions() {
		super.createActions();
	}

	@Override
	public void removeOut(BNode out) {
		columns.forEach(c -> {
			int i = columns.indexOf(out);

			if (i >= 0) {
				c.set(i, null);
			}
		});
	}

	@Override
	public void toCSVStreams(List<CSVData> l, boolean printHeaders)
			throws IllegalArgumentException, IllegalAccessException {
		var csv = new CSVData();
		l.add(csv);
		
		for (var c : columns) {
			
		}
		
		for (int i = 0; i < values.size(); ++i) {
			values.get(i).fieldsToCSV(i == 0 ? printHeaders : false);
		}
	}

	@Override
	public String whatIsThis() {
		return "a table of nodes";
	}

	@Override
	public String prettyName() {
		return label == null ? "a table" : label;
	}

	public void select(int col, int row) {
		selection.add(get(col, row));
	}

	public void unselect(int col, int row) {
		selection.remove(get(col, row));
	}

	public List<BNode> getSelected() {
		return selection;
	}

	public void reset() {
		super.reset();
		columns.clear();
	}

	public List<BNode> columns(int col) {
		return columns.get(col);
	}

	public int nbColumns() {
		return columns.size();
	}

	public boolean isSelected(BNode n) {
		return selection.contains(n);
	}

}
