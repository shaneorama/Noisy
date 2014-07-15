package test.contrib;

import java.awt.Color;

import javax.swing.*;

import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.skin.SubstanceOfficeBlue2007LookAndFeel;

public class SkinSetBackgroundTest extends JFrame {

	public SkinSetBackgroundTest() {
		JPanel panel1 = new JPanel();
		JTable jTable1 = new JTable();

		jTable1.setModel(new javax.swing.table.DefaultTableModel(
				new Object[][] { { null, null, null, null },
						{ null, null, null, null }, { null, null, null, null },
						{ null, null, null, null }, }, new String[] {
						"Title 1", "Title 2", "Title 3", "Title 4" }));

		jTable1.setSize(220, 360);
		JTree jTree1 = new JTree();
		jTree1.setSize(220, 360);
		jTable1.setBackground(Color.WHITE);
		jTree1.setBackground(Color.white);

		panel1.add(jTable1);
		panel1.add(jTree1);

		UIManager
				.put(SubstanceLookAndFeel.COLORIZATION_FACTOR, new Double(1.0));

		this.add(panel1);
		this.setSize(500, 400);

	}

	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager
							.setLookAndFeel(new SubstanceOfficeBlue2007LookAndFeel());
				} catch (UnsupportedLookAndFeelException ex) {

				}

				new SkinSetBackgroundTest().setVisible(true);

			}
		});
	}

}
