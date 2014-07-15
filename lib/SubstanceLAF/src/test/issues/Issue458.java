package test.issues;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;

import org.jvnet.substance.skin.SubstanceBusinessBlackSteelLookAndFeel;

public class Issue458 extends JFrame {

	JScrollPane scrollPane = new JScrollPane();
	JTable scrollingTable = new JTable();
	JTable headerTable = new JTable();
	JPanel mainPanel = new JPanel(new BorderLayout());
	int rowHeaderLastColumnDragStartX;

	public Issue458() {
		try {
			setContentPane(mainPanel);
			mainPanel.setOpaque(false);
			mainPanel.add(scrollPane, java.awt.BorderLayout.CENTER);
			headerTable.setModel(new HeaderTableModel());
			scrollingTable.setModel(new ScrollingTableModel());

			headerTable.setShowVerticalLines(true);
			headerTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
			scrollingTable.setShowVerticalLines(true);
			scrollingTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

			scrollPane.getViewport().add(scrollingTable);

			scrollPane.setRowHeaderView(headerTable);
			scrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER, headerTable
					.getTableHeader());
			headerTable
					.setPreferredScrollableViewportSize(new Dimension(200, 0));
			headerTable.getTableHeader().addMouseListener(
					new HeaderMouseHandler());
			headerTable.getTableHeader().addMouseMotionListener(
					new HeaderMouseMotionHandler());
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

				Issue458 frame1 = new Issue458();
				frame1.setSize(new Dimension(500, 300));
				frame1.validate();
				frame1.setVisible(true);
			}
		});
	}

	private class HeaderMouseHandler extends MouseAdapter {
		/**
		 * This handler allows the user to grab the row header table last column
		 * and resize the row header table by dragging it. This assumes that the
		 * rowHeader is correctly installed as the corner of a viewport, and
		 * that the viewport itself contains the main table. This method sets
		 * the starting point for subsequent drag operations.
		 * 
		 * @param e
		 *            MouseEvent
		 */
		@Override
		public void mousePressed(MouseEvent e) {
			JTableHeader h = (JTableHeader) e.getSource();
			int viewColumn = headerTable.getColumnModel().getColumnIndexAtX(
					e.getX());
			int column = -1;
			if (viewColumn != -1) {
				column = headerTable.getColumnModel().getColumn(viewColumn)
						.getModelIndex();
			}
			if (column == headerTable.getColumnCount() - 1) {
				Rectangle r = h.getHeaderRect(column);
				int extent = r.width + r.x;
				if (extent - e.getX() <= 3) {
					rowHeaderLastColumnDragStartX = e.getX();
				}
			}
		}

		/**
		 * Cancel the row header drag operation.
		 * 
		 * @param e
		 *            MouseEvent
		 */
		@Override
		public void mouseReleased(MouseEvent e) {
			rowHeaderLastColumnDragStartX = -1;
		}
	}

	private class HeaderMouseMotionHandler extends MouseMotionAdapter {
		/**
		 * This handler allows the user to grab the row header table last column
		 * and resize the row header table by dragging it. This assumes that the
		 * rowHeader is correctly installed as the corner of a viewport, and
		 * that the viewport itself contains the main table.
		 * 
		 * @param e
		 *            MouseEvent
		 */
		@Override
		public void mouseDragged(MouseEvent e) {
			JTableHeader h = (JTableHeader) e.getSource();
			int viewColumn = headerTable.getColumnModel().getColumnIndexAtX(
					e.getX());
			int column = -1;
			if (viewColumn != -1) {
				column = headerTable.getColumnModel().getColumn(viewColumn)
						.getModelIndex();
			}
			if (column == headerTable.getColumnCount() - 1
					&& rowHeaderLastColumnDragStartX > -1) {
				int dragMovementX = e.getX() - rowHeaderLastColumnDragStartX;
				Dimension d = headerTable.getPreferredScrollableViewportSize();
				Rectangle vr = scrollingTable.getVisibleRect();
				// don't allow moving the column past the visible rectangle,
				// making it impossible to regrab the column
				// in fact don't allow the main table width to become less than
				// 100 otherwise its not so useful
				if (dragMovementX < vr.width - 100) {
					rowHeaderLastColumnDragStartX = e.getX();
					Dimension newD = new Dimension(d.width + dragMovementX,
							d.height);
					headerTable.setPreferredScrollableViewportSize(newD);
					headerTable.revalidate();
					repaint();
					headerTable.repaint();
				}
			}
		}
	}

	private class ScrollingTableModel extends AbstractTableModel {
		private Object[][] data;
		private String[] columnNames;

		public ScrollingTableModel() {
			columnNames = new String[] { "Data1", "Data2", "Data3", "Data 4",
					"Data 5", "Data 6" };
			data = new Object[6][columnNames.length];
			for (int i = 0; i < data.length; i++) {
				data[i][0] = "One";
				data[i][1] = "Two";
				data[i][2] = "Three";
				data[i][3] = "Four";
				data[i][4] = "Five";
				data[i][5] = "Six";
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

	private class HeaderTableModel extends AbstractTableModel {
		private Object[][] data;
		private String[] columnNames;

		public HeaderTableModel() {
			columnNames = new String[] { "Header1", "Header2" };
			data = new Object[6][columnNames.length];
			for (int i = 0; i < data.length; i++) {
				data[i][0] = "ABC";
				data[i][1] = "XYZ";
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
