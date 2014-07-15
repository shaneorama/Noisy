package test.contrib;

import java.awt.*;
import java.util.Date;

import javax.swing.*;
import javax.swing.table.*;

import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.api.renderers.SubstanceDefaultTableCellRenderer;

public class TableRenderDemo extends JPanel {

	private boolean DEBUG = false;

	public static void main(String[] args) {

		// Schedule a job for the event-dispatching thread:

		// creating and showing this application's GUI.

		javax.swing.SwingUtilities.invokeLater(new Runnable() {

			// KIRILL: Setting LAF

			public void run() {

				String LAFName = "org.jvnet.substance.skin.SubstanceCremeCoffeeLookAndFeel";

				try {

					UIManager.setLookAndFeel(LAFName);

				} catch (Exception e) {

					e.printStackTrace();

				}

				//SubstanceLookAndFeel.setSkin(new CookbookSkin());

				createAndShowGUI();

			}

		});

	}

	// KIRILL Test Renderer setting foreground to blue

	public class TestRenderer extends SubstanceDefaultTableCellRenderer {

		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value,

				boolean isSelected, boolean hasFocus, int row, int column) {

			// TODO Auto-generated method stub

			Component a = super.getTableCellRendererComponent(table, value,
					isSelected, hasFocus,

					row, column);

			a.setForeground(Color.blue);

			return a;

		}

	};

	public TableRenderDemo() {

		super(new GridLayout(1, 0));

		JTable table = new JTable(new MyTableModel());

		// KIRILL: font setting, even before than default renderers setting

		table.setFont(new Font("Arial Black", Font.BOLD, 16));

		table.setPreferredScrollableViewportSize(new Dimension(500, 70));

		table.setFillsViewportHeight(true);

		// Create the scroll pane and add the table to it.

		JScrollPane scrollPane = new JScrollPane(table);

		// Set up column sizes.

		initColumnSizes(table);

		// Fiddle with the Sport column's cell editors/renderers.

		setrenderers(table, table.getColumnModel().getColumn(2));

		// Add the scroll pane to this panel.

		add(scrollPane);

	}

	/*
	 * 
	 * This method picks good column sizes. If all column heads are wider than
	 * 
	 * the column's cells' contents, then you can just use
	 * 
	 * column.sizeWidthToFit().
	 */

	private void initColumnSizes(JTable table) {

		MyTableModel model = (MyTableModel) table.getModel();

		TableColumn column = null;

		Component comp = null;

		int headerWidth = 0;

		int cellWidth = 0;

		Object[] longValues = model.longValues;

		TableCellRenderer headerRenderer = table.getTableHeader()
				.getDefaultRenderer();

		for (int i = 0; i < 5; i++) {

			column = table.getColumnModel().getColumn(i);

			comp = headerRenderer.getTableCellRendererComponent(null, column
					.getHeaderValue(),

			false, false, 0, 0);

			headerWidth = comp.getPreferredSize().width;

			comp = table.getDefaultRenderer(model.getColumnClass(i))
					.getTableCellRendererComponent(

					table, longValues[i], false, false, 0, i);

			cellWidth = comp.getPreferredSize().width;

			if (DEBUG) {

				System.out.println("Initializing width of column " + i + ". "
						+ "headerWidth = "

						+ headerWidth + "; cellWidth = " + cellWidth);

			}

			column.setPreferredWidth(Math.max(headerWidth, cellWidth));

		}

	}

	public void setrenderers(JTable table, TableColumn sportColumn) {

		// Set up the editor for the sport cells.

		JComboBox comboBox = new JComboBox();

		comboBox.addItem("Snowboarding");

		comboBox.addItem("Rowing");

		comboBox.addItem("Knitting");

		comboBox.addItem("Speed reading");

		comboBox.addItem("Pool");

		comboBox.addItem("None of the above");

		sportColumn.setCellEditor(new DefaultCellEditor(comboBox));

		// Set up tool tips for the sport cells.

		DefaultTableCellRenderer renderer = new SubstanceDefaultTableCellRenderer() {

			@Override
			public Component getTableCellRendererComponent(JTable table,
					Object value,

					boolean isSelected, boolean hasFocus, int row, int column) {

				// TODO Auto-generated method stub

				Component a = super.getTableCellRendererComponent(table, value,
						isSelected,

						hasFocus, row, column);

				a.setForeground(Color.blue);
				a.setBackground(Color.yellow);

				return a;

			}

		};

		renderer.setToolTipText("Click for combo box");

		sportColumn.setCellRenderer(renderer);

		renderer = new TestRenderer();

		table.setDefaultRenderer(Date.class, renderer);

		// renderer.setToolTipText("Click for combo box");

		// table.getColumnModel().getColumn(table.getColumnCount() -

		// 1).setCellRenderer(renderer);

	}

	class MyTableModel extends AbstractTableModel {

		private String[] columnNames = { "First Name", "Last Name", "Sport",
				"# of Years",

				"Vegetarian", "DATE" };

		private Object[][] data = {

		{ "Mary", "Campione", "Snowboarding",

		new Integer(5), new Boolean(false),

		new Date() },

		{ "Alison", "Huml", "Rowing", new Integer(3),

		new Boolean(true), new Date() },

		{ "Kathy", "Walrath", "Knitting",

		new Integer(2), new Boolean(false),

		new Date() },

		{ "Sharon", "Zakhour", "Speed reading",

		new Integer(20), new Boolean(true),

		new Date() },

		{ "Philip", "Milne", "Pool", new Integer(10),

		new Boolean(false), new Date() } };

		public final Object[] longValues = { "Sharon", "Campione",
				"None of the above",

				new Integer(20), Boolean.TRUE, new Date() };

		public int getColumnCount() {

			return columnNames.length;

		}

		public int getRowCount() {

			return data.length;

		}

		public String getColumnName(int col) {

			return columnNames[col];

		}

		public Object getValueAt(int row, int col) {

			return data[row][col];

		}

		/*
		 * 
		 * JTable uses this method to determine the default renderer/ editor for
		 * 
		 * each cell. If we didn't implement this method, then the last column
		 * 
		 * would contain text ("true"/"false"), rather than a check box.
		 */

		public Class getColumnClass(int c) {

			return getValueAt(0, c).getClass();

		}

		/*
		 * 
		 * Don't need to implement this method unless your table's editable.
		 */

		public boolean isCellEditable(int row, int col) {

			// Note that the data/cell address is constant,

			// no matter where the cell appears onscreen.

			if (col < 2) {

				return false;

			} else {

				return true;

			}

		}

		/*
		 * 
		 * Don't need to implement this method unless your table's data can
		 * 
		 * change.
		 */

		public void setValueAt(Object value, int row, int col) {

			if (DEBUG) {

				System.out.println("Setting value at " + row + "," + col
						+ " to " + value

						+ " (an instance of " + value.getClass() + ")");

			}

			data[row][col] = value;

			fireTableCellUpdated(row, col);

			if (DEBUG) {

				System.out.println("New value of data:");

				printDebugData();

			}

		}

		private void printDebugData() {

			int numRows = getRowCount();

			int numCols = getColumnCount();

			for (int i = 0; i < numRows; i++) {

				System.out.print("    row " + i + ":");

				for (int j = 0; j < numCols; j++) {

					System.out.print("  " + data[i][j]);

				}

				System.out.println();

			}

			System.out.println("--------------------------");

		}

	}

	/**
	 * 
	 * Create the GUI and show it. For thread safety, this method should be
	 * 
	 * invoked from the event-dispatching thread.
	 */

	private static void createAndShowGUI() {

		// Create and set up the window.

		JFrame frame = new JFrame("TableRenderDemo");

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Create and set up the content pane.

		TableRenderDemo newContentPane = new TableRenderDemo();

		newContentPane.setOpaque(true); // content panes must be opaque

		frame.setContentPane(newContentPane);

		// Display the window.

		frame.setSize(1000, 400);
		
		frame.setLocationRelativeTo(null);

		frame.setVisible(true);

	}

}
