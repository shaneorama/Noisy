package test.contrib;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;

import org.jvnet.substance.skin.SubstanceBusinessBlueSteelLookAndFeel;


public class TreeEditor {
	
	public static void main(String[] args) {
		// apply substance LaF to all Frames and Dialogs
		JFrame.setDefaultLookAndFeelDecorated(true);
		try {
			UIManager.setLookAndFeel(new SubstanceBusinessBlueSteelLookAndFeel());
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		Runnable substanceTest = new Runnable() {
			
			class CustomTreeCellEditor extends DefaultTreeCellEditor {
				public CustomTreeCellEditor(final JTree tree, final DefaultTreeCellRenderer renderer) {
			        super(tree, renderer);
			    }
			}

			@Override
			public void run() {
				// create frame
				JFrame mainFrame = new JFrame();
				mainFrame.setTitle("Substance Test");
				mainFrame.setBounds(0, 0, 800, 600);
				
				DefaultMutableTreeNode top = new DefaultMutableTreeNode("top");
				DefaultTreeModel projectModel = new DefaultTreeModel(top);
				final JTree jTree = new JTree(projectModel);
				
				projectModel.insertNodeInto(new DefaultMutableTreeNode("child1"), top, top.getChildCount());
				projectModel.insertNodeInto(new DefaultMutableTreeNode("child2"), top, top.getChildCount());
				projectModel.insertNodeInto(new DefaultMutableTreeNode("child3"), top, top.getChildCount());
				
				// Custom Tree Cell Editor
				CustomTreeCellEditor treeCellEditor = new CustomTreeCellEditor(jTree, new DefaultTreeCellRenderer()); 
				jTree.setCellEditor(treeCellEditor);
				
				jTree.addMouseListener(new MouseAdapter() {
					@Override
					public void mousePressed(MouseEvent e) {
						super.mousePressed(e);
						 if (e.getClickCount() == 3) {
							 jTree.setEditable(true);
							 jTree.startEditingAtPath(jTree.getSelectionPath());
						 }
					}
				});
				
				mainFrame.add(jTree);
				mainFrame.setVisible(true);
			}
			
		};
		
		// create the UI in event dispatch thread
		SwingUtilities.invokeLater(substanceTest);
	}
}
