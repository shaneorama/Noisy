package test.issues;

import java.awt.*;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;

import org.jvnet.substance.skin.SubstanceBusinessBlackSteelLookAndFeel;

public class Issue460 extends JFrame {
	JTable demoTable = new JTable();
	JPanel mainPanel = new JPanel(new BorderLayout());

	public Issue460() {
		try {
			setContentPane(mainPanel);
			mainPanel.setOpaque(false);
			mainPanel.add(demoTable, java.awt.BorderLayout.CENTER);
			demoTable.setModel(new demoTableModel());
			demoTable.setShowVerticalLines(true);
			demoTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
			demoTable.setDefaultRenderer(String.class,
					new customTableCellRenderer());
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager
							.setLookAndFeel(new SubstanceBusinessBlackSteelLookAndFeel());
				} catch (UnsupportedLookAndFeelException ex) {
					ex.printStackTrace();
				}

				Issue460 frame1 = new Issue460();
				frame1.setSize(new Dimension(500, 300));
				frame1.validate();
				frame1.setVisible(true);
			}
		});
	}

	private class customTableCellRenderer extends JLabel implements
			TableCellRenderer {
		public customTableCellRenderer() {
			super();
			setOpaque(true);
		}

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			setText(value.toString());
			if (isSelected) {
				setBackground(Color.YELLOW);
			} else if (column == 0) {
				setBackground(Color.RED);
			} else if (column == 1) {
				setBackground(Color.CYAN);
			} else {
				setBackground(Color.MAGENTA);
			}
			return this;
		}
	}

	private class demoTableModel extends AbstractTableModel {
		private Object[][] data;
		private String[] columnNames;

		public demoTableModel() {
			columnNames = new String[] { "Data1", "Data2", "Data3" };
			data = new Object[6][columnNames.length];
			for (int i = 0; i < data.length; i++) {
				data[i][0] = "One";
				data[i][1] = "Two";
				data[i][2] = "Three";
			}
		}

		public int getRowCount() {
			return data.length;
		}

		public int getColumnCount() {
			return columnNames.length;
		}

		@Override
		public String getColumnName(int column) {
			return columnNames[column];
		}

		public Object getValueAt(int row, int column) {
			return data[row][column];
		}

		@Override
		public void setValueAt(Object value, int row, int column) {
			data[row][column] = value;
		}

		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}

		@Override
		public Class getColumnClass(int column) {
			return String.class;
		}
	}
}
