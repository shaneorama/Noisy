package test.issues;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import org.jvnet.substance.skin.SubstanceRavenLookAndFeel;

public class Issue358 extends JFrame {
	public Issue358() {
		super("Substance Test");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		final JSplitPane splitPane = new JSplitPane();
		splitPane.setLeftComponent(new JLabel("Left Label"));
		splitPane.setRightComponent(new JLabel("Right Label"));
		JButton button = new JButton("Press Me");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				splitPane.setEnabled(false);
			}
		});
		add(button, BorderLayout.WEST);
		add(splitPane, BorderLayout.CENTER);
		pack();
		setVisible(true);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(new SubstanceRavenLookAndFeel());
				} catch (UnsupportedLookAndFeelException e) {
					e.printStackTrace();
				}
				new Issue358();
			}
		});
	}
}
