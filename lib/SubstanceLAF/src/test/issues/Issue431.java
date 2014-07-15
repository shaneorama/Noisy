package test.issues;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.*;

import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.skin.SubstanceRavenGraphiteGlassLookAndFeel;

/**
 * 
 * @author bio-aulas
 */
public class Issue431 implements Runnable {

	public static void main(String[] args) {
		try {
			UIManager
					.setLookAndFeel(new SubstanceRavenGraphiteGlassLookAndFeel());
		} catch (Exception e) {
			e.printStackTrace();
		}
		SwingUtilities.invokeLater(new Issue431());
	}

	public void run() {
		JFrame f = new JFrame();
		JPanel p = new JPanel(new BorderLayout());
		JTextArea a = new JTextArea();
		a.setBackground(Color.WHITE);
		a.putClientProperty(SubstanceLookAndFeel.COLORIZATION_FACTOR,
				new Double(1.0));
		if (!Color.WHITE.equals(a.getBackground())) {
			System.out.println("bad");
		}

		p.add(a, BorderLayout.CENTER);
		f.add(p);
		f.pack();
		// but displayed value is not white.
		f.setVisible(true);
	}

	private static void setLookAndFeel() {
	}
}
