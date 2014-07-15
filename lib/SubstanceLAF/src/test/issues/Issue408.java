package test.issues;

import javax.swing.*;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;

import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.skin.BusinessBlackSteelSkin;

public class Issue408 extends JFrame {
	public Issue408() {
		JTable table = new JTable(new DefaultTableModel() {
			@Override
			public int getRowCount() {
				return 10;
			}

			@Override
			public int getColumnCount() {
				return 5;
			}

			@Override
			public Object getValueAt(int row, int column) {
				return row + ":" + column;
			}

			@Override
			public String getColumnName(int column) {
				return "Column " + column;
			}
		});

		JScrollPane jsp = new JScrollPane(table);
		table.setTableHeader(null);
		table.setColumnModel(new DefaultTableColumnModel());
		this.add(jsp);

		this.setSize(500, 400);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public static void main(String[] args) {
		SubstanceLookAndFeel.setSkin(new BusinessBlackSteelSkin());
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new Issue408().setVisible(true);
			}
		});
	}

}
