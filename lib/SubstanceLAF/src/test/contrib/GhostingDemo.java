package test.contrib;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;

import javax.swing.*;

import org.jvnet.lafwidget.animation.FadeConfigurationManager;
import org.jvnet.lafwidget.animation.FadeKind;
import org.jvnet.substance.skin.SubstanceBusinessBlueSteelLookAndFeel;

public class GhostingDemo extends javax.swing.JFrame {
	public static void main(final String[] args) {
		Locale.setDefault(Locale.ENGLISH);
		try {
			UIManager
					.setLookAndFeel(new SubstanceBusinessBlueSteelLookAndFeel());
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		FadeConfigurationManager.getInstance().allowFades(
				FadeKind.GHOSTING_BUTTON_PRESS);

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				GhostingDemo inst = new GhostingDemo();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}

	private JButton buttonExit;

	public GhostingDemo() {
		super("Ghost demo");
		initGUI();
	}

	private void initGUI() {
		setLayout(new FlowLayout());
		add(getButtonExit());
		setSize(300, 300);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}

	public JButton getButtonExit() {
		if (this.buttonExit == null) {
			this.buttonExit = new JButton("Button");
			this.buttonExit.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					JOptionPane.showConfirmDialog(GhostingDemo.this,
							"some text ?", "title", JOptionPane.YES_NO_OPTION);
				}
			});
		}
		return this.buttonExit;
	}
}
