package test.issues;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.jvnet.substance.skin.SubstanceBusinessBlackSteelLookAndFeel;

public class Issue378 extends JFrame {

	public Issue378() {
		JTable table = new JTable(new String[][] {
				{ "1", "2", "3", "4", "5", "6" },
				{ "1", "2", "3", "4", "5", "6" } }, new String[] { "1", "2",
				"3", "4", "5", "6" });
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		JScrollPane scrollPane = new JScrollPane(table);
		add(scrollPane);
		setSize(300, 200);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	public static void main(String[] args) throws Exception {
		UIManager.setLookAndFeel(new SubstanceBusinessBlackSteelLookAndFeel());
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new Issue378();
			}
		});
	}

}
