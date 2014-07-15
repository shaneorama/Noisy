package test.contrib;

import java.awt.Component;

import javax.swing.*;

import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.api.SubstanceConstants.FocusKind;
import org.jvnet.substance.skin.SubstanceBusinessBlueSteelLookAndFeel;

public class ProgressBarInListTest extends JFrame {

	private JList jList1;
	private JScrollPane jScrollPane1;

	public ProgressBarInListTest() {
		jScrollPane1 = new JScrollPane();
		jList1 = new JList();
		jScrollPane1.setViewportView(jList1);
		getContentPane().add(jScrollPane1, java.awt.BorderLayout.CENTER);
		pack();
		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

		// Test-data
		ListItem f1 = new ListItem("Test_1", 100);
		ListItem f2 = new ListItem("Test_2", 15);
		ListItem f3 = new ListItem("Test_3", 0);

		// Add test-data to Model
		DefaultListModel listModel = new DefaultListModel();
		listModel.addElement(f1);
		listModel.addElement(f2);
		listModel.addElement(f3);

		// Set custom CellRenderer
		jList1.setCellRenderer(new FileTransferListCellRenderer());
		jList1.setModel(listModel);

		// jList1.putClientProperty(LafWidget.ANIMATION_KIND,
		// AnimationKind.NONE);
	}

	public static void main(String args[]) {

		// Set up Substance
		try {
			UIManager
					.setLookAndFeel(new SubstanceBusinessBlueSteelLookAndFeel());
			UIManager.put(SubstanceLookAndFeel.FOCUS_KIND, FocusKind.NONE);
			JFrame.setDefaultLookAndFeelDecorated(true);
		} catch (UnsupportedLookAndFeelException ex) {
			ex.printStackTrace();
		}

		// Show Gui
		java.awt.EventQueue.invokeLater(new Runnable() {

			public void run() {
				ProgressBarInListTest listTest = new ProgressBarInListTest();
				listTest.setTitle("JProgressBar in JList");
				listTest.setSize(300, 500);
				listTest.setLocationRelativeTo(null);
				listTest.setVisible(true);
			}
		});
	}

	/**
	 * Represents an item in the list
	 */
	public class ListItem {

		private String text;
		private int progress;

		public ListItem(String text, int progress) {
			this.text = text;
			this.progress = progress;
		}

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}

		public int getProgress() {
			return progress;
		}

		public void setProgress(int progress) {
			this.progress = progress;
		}
	}

	/**
	 * Custom panel with JLabel and JProgressBar on it. Used to visualize
	 * ListItem objects in an JList.
	 */
	public class ProgressTaskPanel extends JPanel {

		private JLabel label;
		private JProgressBar progressBar;

		public ProgressTaskPanel() {
			// initComponents();
			label = new javax.swing.JLabel();
			progressBar = new javax.swing.JProgressBar();
			setLayout(new java.awt.BorderLayout());
			add(label, java.awt.BorderLayout.PAGE_START);
			add(progressBar, java.awt.BorderLayout.PAGE_END);
		}

		public void setText(String text) {
			label.setText(text);
		}

		public void setProgress(int progress) {
			progressBar.setValue(progress);
		}
	}

	/**
	 * Custom CellRenderer to render ListItem objects
	 */
	public class FileTransferListCellRenderer extends ProgressTaskPanel
			implements ListCellRenderer {

		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {

			ListItem item = (ListItem) value;
			if (cellHasFocus)
				setText("*" + item.getText() + "*");
			else
				setText(item.getText());
			setProgress(item.getProgress());
			setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 2, 2, 2));

			return this;
		}
	}

}
