package test.issues;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;

import javax.swing.*;

import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.skin.BusinessSkin;

public class Issue233 extends JDialog {
	Issue233() {
		super((Frame) null, "Test");
		setSize(200, 200);

		JPanel myContentPane = new JPanel();
		myContentPane.setLayout(new BorderLayout());
		setContentPane(myContentPane);

		JTabbedPane tabs = new JTabbedPane();
		tabs.addTab("Foo", new JButton("Test"));
		tabs.addTab("Bar", new JLabel());
		add(tabs, BorderLayout.CENTER);
		// add(new JButton("Test"), BorderLayout.CENTER);

		add(new JLabel("Press Esc to close dialog"), BorderLayout.NORTH);

		// connect "Esc" key with "System.exit(0)"
		String actionName = "VK_ESCAPE";
		Action action = new AbstractAction(actionName) {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		};
		myContentPane.getActionMap().put(actionName, action);
		myContentPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0),
				actionName);

		setVisible(true);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				SubstanceLookAndFeel.setSkin(new BusinessSkin());
				new Issue233();
			}
		});
	}
}