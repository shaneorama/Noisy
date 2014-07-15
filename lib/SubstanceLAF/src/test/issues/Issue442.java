package test.issues;

import java.awt.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.skin.CremeCoffeeSkin;
import org.jvnet.substance.skin.RavenGraphiteGlassSkin;

public class Issue442 extends JFrame {

	JDesktopPane desktop;

	public Issue442() {

		SubstanceLookAndFeel.setSkin(new CremeCoffeeSkin());

		JPanel panel = new JPanel();
		panel.add(getTable());

		desktop = new JDesktopPane();

		setSize(500, 300);

		setLocation(150, 150);

		JInternalFrame iframe = new JInternalFrame();
		JPanel panel2 = new JPanel();
		panel2.add(getTable());
		iframe.add(panel2);
		desktop.add(iframe);

		iframe.pack();

		iframe.getRootPane().putClientProperty(
				SubstanceLookAndFeel.SKIN_PROPERTY,
				new RavenGraphiteGlassSkin());

		iframe.setVisible(true);

		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(panel, BorderLayout.SOUTH);
		this.getContentPane().add(desktop, BorderLayout.CENTER);

		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}

	private Component getTable() {
		final JScrollPane pane = new JScrollPane();
		final JTable jtable = new JTable();
		jtable.setAutoCreateRowSorter(true);
		jtable.setModel(new DefaultTableModel(new Object[][] {
				{ "item", "item" }, { "item", "item" }, { "item", "item" },
				{ "item", "item" } }, new String[] { "Title 1", "Title 2" }));
		jtable.setColumnSelectionAllowed(true);
		jtable.setRowSelectionAllowed(true);
		pane.setViewportView(jtable);
		pane.setPreferredSize(new Dimension(200, 100));

		return pane;
	}

	public static void main(final String[] args) {

		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				final Issue442 c = new Issue442();
				c.setVisible(true);
			}
		});
	}

}
