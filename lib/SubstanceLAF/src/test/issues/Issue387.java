package test.issues;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.skin.BusinessBlueSteelSkin;

public class Issue387 extends JFrame {
	public Issue387() {
		super("Test setBackground(null)");

		this.setLayout(new FlowLayout());
		final JSpinner spinner = new JSpinner(new SpinnerNumberModel(50, 0,
				100, 10));
		spinner.setBackground(null);
		System.out.println(spinner.getBackground());
		this.add(spinner);
		JButton button = new JButton("set bg to red");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				spinner.setBackground(Color.red);
				System.out.println(spinner.getBackground());
			}
		});
		this.add(button);
		JButton button2 = new JButton("set bg to null");
		button2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				spinner.setBackground(null);
				System.out.println(spinner.getBackground());
			}
		});
		this.add(button2);

		this.setSize(200, 100);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public static void main(String[] args) {
		SubstanceLookAndFeel.setSkin(new BusinessBlueSteelSkin());
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new Issue387().setVisible(true);
			}
		});
	}

}
