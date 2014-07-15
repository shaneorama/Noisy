package test.issues;

import java.awt.BorderLayout;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.skin.BusinessSkin;

public class Issue383 extends JFrame {
	public Issue383() {
		JTable table = new JTable(new DefaultTableModel() {
			@Override
			public int getColumnCount() {
				return 4;
			}

			@Override
			public int getRowCount() {
				return 10;
			}

			@Override
			public Object getValueAt(int row, int column) {
				return row + ":" + column;
			}

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		});
		// table.setShowGrid(false);
		this.add(table, BorderLayout.CENTER);

		this.setSize(400, 300);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public static void main(String[] args) {
		SubstanceLookAndFeel.setSkin(new BusinessSkin());
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new Issue383().setVisible(true);
			}
		});
	}

}
