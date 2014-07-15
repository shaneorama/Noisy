package test.issues;

import javax.swing.*;

import org.jvnet.substance.skin.SubstanceRavenGraphiteLookAndFeel;

public class Issue360 {

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(new SubstanceRavenGraphiteLookAndFeel());
		} catch (Exception e) {
			e.printStackTrace();
		}

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JDialog dlg = new JDialog();
				Object[][] data = new Object[0][0];
				Object[] columns = new Object[15];

				for (int i = 0; i < 15; i++)
					columns[i] = "Column " + i;

				JTable table = new JTable(data, columns);
				table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
				dlg.add(new JScrollPane(table));
				dlg.setPreferredSize(new java.awt.Dimension(200, 200));
				dlg.pack();

				dlg.setLocationByPlatform(true);

				dlg.setVisible(true);
			}
		});
	}

}
