package test.contrib;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.tree.*;

public class TestApp extends JFrame {
	// L&F class names
	static final String metal = "javax.swing.plaf.metal.MetalLookAndFeel";
	static final String motif = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
	static final String nimbus = "sun.swing.plaf.nimbus.NimbusLookAndFeel";

	static final String windows = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
	static final String gtk = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";
	static final String mac = "com.sun.java.swing.plaf.mac.MacLookAndFeel";

	// jgoodies
	static final String jgoodies = "com.jgoodies.looks.windows.WindowsLookAndFeel";
	static final String plastic = "com.jgoodies.looks.plastic.PlasticLookAndFeel";
	static final String plastic3D = "com.jgoodies.looks.plastic.Plastic3DLookAndFeel";
	static final String plasticXP = "com.jgoodies.looks.plastic.PlasticXPLookAndFeel";

	static final String liquid = "com.birosoft.liquid.LiquidLookAndFeel";

	static final String substance = "org.jvnet.substance.skin.SubstanceCremeLookAndFeel";
	static final String napkin = "net.sourceforge.napkinlaf.NapkinLookAndFeel";
	static final String office = "org.fife.plaf.Office2003.Office2003LookAndFeel";

	public static void main(String[] args) {
		try {
			// UIManager.setLookAndFeel(gtk);
			 UIManager.setLookAndFeel(substance);
			// UIManager.put(org.jvnet.substance.SubstanceLookAndFeel.
			// SHOW_EXTRA_WIDGETS, Boolean.TRUE);

			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					TestApp frame = new TestApp("Test App");
					frame.setupFrame();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	TestApp(String title) {
		super(title);
	}

	JTree tree;
	JTabbedPane tabbed;
	JComboBox box;

	void setupFrame() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		JPanel west = new JPanel();
		west.setLayout(new BorderLayout());
		add(panel);
		panel.add(west, BorderLayout.WEST);

		tree = new JTree();
		tree.setCellRenderer(new DefaultTreeCellRenderer());

		JScrollPane sc = new JScrollPane(tree);
		west.add(sc, BorderLayout.CENTER);

		tabbed = new JTabbedPane();
		tabbed.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		// tabbed.setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);
		panel.add(tabbed, BorderLayout.CENTER);

		box = new JComboBox();
		west.add(box, BorderLayout.NORTH);

		for (int i = 0; i < 10; i++) {
			tabbed.add(new JButton("Tab " + i), "Tab " + i);
			box.addItem("Tab " + i);
		}
		panel.add(tabbed, BorderLayout.CENTER);

		box.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					int i = box.getSelectedIndex();
					tabbed.setSelectedIndex(i);
				}
			}
		});

		// tabbed.putClientProperty(org.jvnet.substance.SubstanceLookAndFeel.
		// TABBED_PANE_CLOSE_BUTTONS_PROPERTY, Boolean.TRUE);
		org.jvnet.lafwidget.tabbed.DefaultTabPreviewPainter previewPainter = new org.jvnet.lafwidget.tabbed.DefaultTabPreviewPainter();

		tabbed.putClientProperty(
				org.jvnet.lafwidget.LafWidget.TABBED_PANE_PREVIEW_PAINTER,
				previewPainter);

		int width = 400, height = 300;
		setSize(width, height);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int x = screenSize.width / 2 - width / 2;
		int y = screenSize.height / 2 - height / 2;
		setLocation(x, y);
		setVisible(true);
	}

}