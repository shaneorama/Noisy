package test.issues;

import javax.swing.*;

import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.skin.BusinessSkin;

public class Issue382 extends JFrame {
	Issue382() {
		super("Test");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(400, 200);

		JTabbedPane tabs = new JTabbedPane();
		tabs.addTab("Foo", null); // Exception

		add(tabs);
		setVisible(true);
	}

	public static void main(String[] args) {
		SubstanceLookAndFeel.setSkin(new BusinessSkin());
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new Issue382();
			}
		});
	}
}
