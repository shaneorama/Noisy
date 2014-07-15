package test;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;

import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.skin.OfficeBlue2007Skin;
import org.jvnet.substance.skin.TwilightSkin;

public class RoTextField extends JFrame {
	public RoTextField() {
		super("Some simple text");

		this.setLayout(new FlowLayout());

		final JButton button = new JButton("default");
		this.add(button);
		this.getRootPane().setDefaultButton(button);
		
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowActivated(WindowEvent e) {
				button.requestFocusInWindow();
			}
		});
		

		this.setSize(650, 400);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public static void main(String[] args) {
		// FadeTracker.DEBUG_MODE = true;
		JFrame.setDefaultLookAndFeelDecorated(true);
		// UIManager.put(LafWidget.ANIMATION_KIND, AnimationKind.NONE);
		SubstanceLookAndFeel.setSkin(new TwilightSkin());
		System.out.println("bg: "
				+ UIManager.getColor("Table.selectionBackground"));
		System.out.println("bg: "
				+ UIManager.getColor("Tree.selectionBackground"));
		System.out.println("fg: "
				+ UIManager.getColor("Tree.selectionForeground"));
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new RoTextField().setVisible(true);
			}
		});
	}
}
