package test.issues;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.table.*;

public class Issue402 extends JDialog {

	public static void main(String args[]) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame tempFrame = new JFrame("Substance Test");
				// Set it way off screen in case it becomes visible.
				tempFrame.setLocation(10000, 10000);
				tempFrame.setExtendedState(Frame.ICONIFIED);
				tempFrame
						.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
				tempFrame.setVisible(false);
				new Issue402(tempFrame);
				System.exit(0);
			}
		});
	}

	//==========================================================================
	// ====

	private Issue402(JFrame theParent) {
		super(theParent, "Substance Test", true);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		// Set the look and feel to Substance.
		try {
			UIManager
					.setLookAndFeel("org.jvnet.substance.skin.SubstanceBusinessBlueSteelLookAndFeel");
			SwingUtilities.updateComponentTreeUI(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		JPanel mainPanel = new JPanel(new BorderLayout());

		// Create the table.
		TableModel model = new AbstractTableModel() {
			@Override
			public int getColumnCount() {
				return 10;
			}

			@Override
			public int getRowCount() {
				return 10;
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				return rowIndex + ":" + columnIndex;
			}
		};
		TableRowSorter<TableModel> theSorter = new TableRowSorter<TableModel>(
				model);
		JTable theTable = new JTable(model);
		theTable.setRowSorter(theSorter);
		
		
		JScrollPane theScrollingPane = new JScrollPane(theTable);
		mainPanel.add(theScrollingPane, BorderLayout.CENTER);

		// Add the button.
		JButton doSwitch = new JButton("Switch LAF");
		doSwitch.setDefaultCapable(true);
		getRootPane().setDefaultButton(doSwitch);
		final Issue402 thisDialog = this;
		doSwitch.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				getRootPane().setCursor(
						Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						try {
							UIManager.setLookAndFeel(UIManager
									.getSystemLookAndFeelClassName());
							SwingUtilities.updateComponentTreeUI(thisDialog);
						} catch (Exception e) {
							e.printStackTrace();
						}
						getRootPane()
								.setCursor(
										Cursor
												.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					}
				});
			}
		});
		JPanel tempPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		tempPanel.add(doSwitch);
		mainPanel.add(tempPanel, BorderLayout.SOUTH);
		getContentPane().add(mainPanel);

		setSize(500, 300);
		setLocationRelativeTo(null);
		setVisible(true);
	}
}
