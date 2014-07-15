package test.contrib;

import java.awt.Color;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.*;

/**
 * 
 * @author decoteaud
 */
public class TreeColoring extends JFrame {
	public TreeColoring() {
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		JTree jTree1 = new JTree();
		JScrollPane jScrollPane1 = new JScrollPane(jTree1);
		add(jScrollPane1);
		pack();
		setLocation(100, 100);
		setSize(400, 400);

		// DefaultTreeCellRenderer r = new DefaultTreeCellRenderer() {
		//
		// @Override
		// public Component getTreeCellRendererComponent(JTree tree,
		// Object value, boolean sel, boolean expanded, boolean leaf,
		// int row, boolean hasFocus) {
		// super.getTreeCellRendererComponent(tree, value, sel, expanded,
		// leaf, row, hasFocus);
		// // this.setOpaque(true);
		// return this;
		// }
		// };
		// jTree1.setCellRenderer(r);

		// test 1 colorization issue
		jTree1.setBackground(Color.red);
		// end test 1

		// test 2 set opaque on the tree and cant see view port background
		// jTree1.setOpaque(false);
		// jTree1.setBackground(Color.red); // set so render can pick up
		// jScrollPane1.setBackground(Color.green);
		// jScrollPane1.getViewport().setBackground(Color.blue);

		// end test 2

	}

	public static void main(String[] args) {
		try {
			UIManager
					.setLookAndFeel(new org.jvnet.substance.skin.SubstanceSaharaLookAndFeel());
		} catch (Exception ex) {
			Logger.getLogger(TreeColoring.class.getName()).log(Level.SEVERE,
					null, ex);
		}
		Runnable r = new Runnable() {
			public void run() {
				new TreeColoring().setVisible(true);
			}
		};
		SwingUtilities.invokeLater(r);
	}

}
