package test.issues;

import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import org.jvnet.substance.skin.SubstanceModerateLookAndFeel;

public class Issue399 {

	boolean useSubstance = true;

	public Issue399() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					if (useSubstance) {
						try {
							UIManager
									.setLookAndFeel(new SubstanceModerateLookAndFeel());
						} catch (UnsupportedLookAndFeelException e) {
							e.printStackTrace();
						}
					} else {
						JFrame.setDefaultLookAndFeelDecorated(true);
					}
					initGUI();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		return;
	}

	private void initGUI() throws ClassNotFoundException {
		final JTable table = new JTable();

		table.setDropMode(DropMode.INSERT_ROWS);
		table.setDragEnabled(true);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		final DefaultTableModel tm = new DefaultTableModel();
		for (int i = 0; i < 10; i++) {
			tm.addColumn("Column: " + i);
		}
		for (int i = 0; i < 10; i++) {
			String[] text = new String[10];
			for (int j = 0; j < 10; j++) {
				text[j] = i + ":" + j;
			}
			tm.addRow(text);
		}
		table.setModel(tm);

		table.setTransferHandler(new TransferHandler() {
			public boolean canImport(TransferHandler.TransferSupport info) {
				return true;
			}

			public boolean importData(TransferHandler.TransferSupport info) {
				return true;
			}

			public void exportDone(JComponent c, Transferable t, int action) {
			}

			public int getSourceActions(JComponent c) {
				return MOVE;
			}

			protected Transferable createTransferable(JComponent c) {
				return new StringSelection("Test");
			}
		});

		JScrollPane scroll = new JScrollPane(table);
		JFrame frame = new JFrame("DRAG TEST");
		frame.add(scroll);
		frame.setSize(500, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		new Issue399();
	}
}