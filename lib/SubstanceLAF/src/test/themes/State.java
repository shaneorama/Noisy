package test.themes;

import java.awt.FlowLayout;

import javax.swing.*;

import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.api.SubstanceConstants.FocusKind;
import org.jvnet.substance.skin.SubstanceBusinessBlackSteelLookAndFeel;

public class State extends JFrame {
	public State() {
		super("Control states");
		this.setLayout(new FlowLayout());

		JToggleButton button1 = new JToggleButton("active");
		button1.setSelected(true);
		JButton button2 = new JButton("default");
		JButton button3 = new JButton("disabled");
		button3.setEnabled(false);

		this.add(button1);
		this.add(button2);
		this.add(button3);

		this.pack();
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public static void main(String[] args) throws Exception {
		UIManager.setLookAndFeel(new SubstanceBusinessBlackSteelLookAndFeel());
		UIManager.put(SubstanceLookAndFeel.FOCUS_KIND, FocusKind.NONE);
		JFrame.setDefaultLookAndFeelDecorated(true);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new State().setVisible(true);
			}
		});
	}

}
