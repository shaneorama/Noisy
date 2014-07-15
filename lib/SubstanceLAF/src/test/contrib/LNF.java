package test.contrib;

import java.awt.*;
import java.awt.event.ActionEvent;

import javax.swing.*;

import org.jvnet.substance.skin.SubstanceNebulaLookAndFeel;

public class LNF extends JFrame {
	public LNF() {
		setSize(500, 500);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		getContentPane()
				.add(
						new JButton(
								new AbstractAction(
										"you should see the yellow rectangle near the left edge") {
									public void actionPerformed(ActionEvent e) {
										setExtendedState(NORMAL);
										setExtendedState(MAXIMIZED_BOTH);
									}
								}) {
							@Override
							public void paint(Graphics gg) {
								super.paint(gg);
								final Graphics2D g = (Graphics2D) gg;
								g.setColor(Color.YELLOW);
								g.fillRect(0, 0, 4, 100);
								g.dispose();
							}
						});
	}

	public static void main(String[] args) {
		try {
			JFrame.setDefaultLookAndFeelDecorated(true);
			JDialog.setDefaultLookAndFeelDecorated(true);
			UIManager.setLookAndFeel(new SubstanceNebulaLookAndFeel());
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new LNF().setVisible(true);
			}
		});
	}
}
