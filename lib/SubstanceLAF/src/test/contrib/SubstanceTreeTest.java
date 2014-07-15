package test.contrib;

import java.awt.*;
import javax.swing.*;
import javax.swing.tree.*;

public class SubstanceTreeTest extends JFrame {

	SubstanceTreeTest() {
		getContentPane().add(createList());
		pack();
		setVisible(true);
	}

	JScrollPane createList() {

		JTree jTree = new JTree();
		jTree.setModel(createTreeModel());

		// This line causes the exception
		jTree.setFont(new Font(jTree.getFont().getName(), Font.PLAIN, 18));

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(jTree);

		/* All done. */
		return scrollPane;
	}

	DefaultTreeModel createTreeModel() {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Movies"); //$NON-NLS-1$

		DefaultTreeModel model = new DefaultTreeModel(root, false);
		Object[] data = new String[] { "Predator", "Terminator", "Conan",
				"Rambo" };

		for (int i = 0; i < data.length; i++) {
			root.add(new DefaultMutableTreeNode(data[i]));
		}
		return model;
	}

	public static void main(String args[]) {
		try {
			UIManager
					.setLookAndFeel("org.jvnet.substance.skin.SubstanceCremeLookAndFeel");
		} catch (Exception e) {
			e.printStackTrace();
		}

		EventQueue.invokeLater(new Runnable() {
			public final void run() {
				new SubstanceTreeTest();
			}
		});
	}
}
