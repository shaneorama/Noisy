package test.issues;

import javax.swing.*;

public class Issue357 {

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		javax.swing.JDialog.setDefaultLookAndFeelDecorated(true);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JDialog dlg = new JDialog();
				JTabbedPane pane = new JTabbedPane();
				pane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

				for (int i = 0; i < 10; i++)
					pane.addTab("Tab " + i, new JPanel());

				dlg.add(pane);
				pane.setSelectedIndex(9);
				dlg.setPreferredSize(new java.awt.Dimension(200, 200));
				dlg.pack();

				dlg.setLocationByPlatform(true);

				dlg.setVisible(true);
			}
		});
	}

}
