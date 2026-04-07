package byransha.nodes.primitive;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;

import byransha.graph.BGraph;

public class DateNode extends PrimitiveValueNode<OffsetDateTime> {
	public DateNode(BGraph g) {
		super(g);
	}

	@Override
	public void createViews() {
		cachedViews.elements.add(new DateView(g, this));
		super.createViews();
	}

	@Override
	public OffsetDateTime defaultValue() {
		return OffsetDateTime.now();
	}

	@Override
	public String whatIsThis() {
		return "a date";
	}

	@Override
	protected void writeValue(OffsetDateTime v, ObjectOutput out) throws IOException {
		out.writeLong(v.toEpochSecond());
	}

	@Override
	protected OffsetDateTime readValue(ObjectInput in) throws IOException {
		return Instant.ofEpochSecond(in.readLong()).atOffset(ZoneOffset.UTC);
	}

	public static class DateView extends TradUINodeView<DateNode> {

		public DateView(BGraph g, DateNode n) {
			super(g, n);
		}

		@Override
		public String whatItShows() {
			return "a date";
		}

		@Override
		protected boolean allowsEditing() {
			return true;
		}

		@Override
		public JComponent getComponent() {
			SpinnerDateModel model = new SpinnerDateModel();
			JSpinner dateSpinner = new JSpinner(model);

			// Customize the editor to show a specific format
			JSpinner.DateEditor editor = new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy");
			dateSpinner.setEditor(editor);
			return dateSpinner;
		}

	}

}
