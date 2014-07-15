package test.issues;

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.*;

import org.jvnet.substance.skin.SubstanceRavenGraphiteLookAndFeel;

public class Issue361 {

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(new SubstanceRavenGraphiteLookAndFeel());
		} catch (Exception e) {
			e.printStackTrace();
		}

		JDialog dlg = new JDialog();
		Object[][] data = new Object[0][0];
		Object[] columns = new Object[15];

		for (int i = 0; i < 15; i++)
			columns[i] = "Column " + i;

		final JTable table = new JTable(data, columns);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		JPanel p = new JPanel();
		p.setLayout(new BorderLayout());

		final JCheckBox checkEnableTable = new JCheckBox("Enable");
		checkEnableTable.setSelected(true);

		checkEnableTable.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				table.setEnabled(checkEnableTable.isSelected());
				table.getTableHeader()
						.setEnabled(checkEnableTable.isSelected());
			}
		});

		p.add(checkEnableTable, BorderLayout.NORTH);
		p.add(new JScrollPane(table));

		dlg.add(p);
		dlg.setPreferredSize(new java.awt.Dimension(300, 300));
		dlg.pack();

		dlg.setLocationByPlatform(true);

		dlg.setVisible(true);
	}

}
