package test.issues;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.skin.BusinessBlackSteelSkin;

public class Issue414 extends JFrame {
	public Issue414() {
		JTable table = new JTable(new DefaultTableModel() {
			@Override
			public int getRowCount() {
				return 100;
			}

			@Override
			public int getColumnCount() {
				return 10;
			}

			@Override
			public Object getValueAt(int row, int column) {
				return (row + 1) + ":" + (column + 1);
			}

			@Override
			public String getColumnName(int column) {
				return "Column " + (column + 1);
			}
		});

		JScrollPane jsp = new JScrollPane(table);
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
				new Issue414().setVisible(true);
			}
		});
	}

}
