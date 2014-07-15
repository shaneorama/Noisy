package test.issues;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.skin.BusinessBlackSteelSkin;

public class Issue366 extends JFrame {
	public Issue366() {
		final JScrollPane jsp = new JScrollPane();
		this.add(jsp, BorderLayout.CENTER);
		
		JButton nullHor = new JButton("scroll -> null");
		nullHor.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						jsp.setHorizontalScrollBar(null);
					}
				});
			}
		});
		JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		controls.add(nullHor);
		this.add(controls, BorderLayout.SOUTH);
		
		this.setSize(400, 300);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public static void main(String[] args) {
		SubstanceLookAndFeel.setSkin(new BusinessBlackSteelSkin());
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new Issue366().setVisible(true);
			}
		});
	}

}
