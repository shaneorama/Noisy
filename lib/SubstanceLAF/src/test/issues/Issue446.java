package test.issues;

import javax.swing.*;

public class Issue446 extends JFrame {

	public Issue446() {
		JTree tabTree = new JTree();
		tabTree.setEditable(true);
		add(tabTree);
		setSize(500, 500);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public static void main(String[] args) throws Exception {
		UIManager
				.setLookAndFeel("org.jvnet.substance.skin.SubstanceBusinessLookAndFeel");
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new Issue446();
			}
		});

	}

}
