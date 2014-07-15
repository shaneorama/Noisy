package test.issues;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import org.jvnet.substance.skin.SubstanceBusinessBlackSteelLookAndFeel;

public class Issue407 extends JFrame implements ActionListener {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager
							.setLookAndFeel(new SubstanceBusinessBlackSteelLookAndFeel());
					// UIManager
					// .setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
				} catch (Exception e) {
				}
				UIManager.put("Button.defaultButtonFollowsFocus", Boolean.TRUE);
				Issue407 frame = new Issue407();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				JButton okbutton = new JButton("OK");
				JButton cancelbutton = new JButton("CANCEL");
				cancelbutton.addActionListener(frame);
				okbutton.addActionListener(frame);
				frame.getContentPane().setLayout(new FlowLayout());
				frame.getContentPane().add(okbutton);
				frame.getContentPane().add(cancelbutton);
				frame.getRootPane().setDefaultButton(okbutton);
				frame.setSize(100, 100);
				frame.setVisible(true);
				cancelbutton.requestFocus();
			}
		});
	}

	public void actionPerformed(ActionEvent e) {
		System.out.println(((JButton) e.getSource()).getText());
	}

}