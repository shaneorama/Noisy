package test.issues;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.*;

import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.skin.CremeCoffeeSkin;
import org.jvnet.substance.skin.RavenGraphiteGlassSkin;

public class Issue439 extends JFrame {

	JDesktopPane desktop;

	public Issue439() {

		JPanel panel = new JPanel();
		panel.add(getComboBox());

		desktop = new JDesktopPane();

		setSize(500, 300);

		setLocation(150, 150);

		JInternalFrame iframe = new JInternalFrame();
		JPanel panel2 = new JPanel();
		panel2.add(getComboBox());
		iframe.add(panel2);
		desktop.add(iframe);

		iframe.pack();

		iframe.getRootPane().putClientProperty(
				SubstanceLookAndFeel.SKIN_PROPERTY,
				new RavenGraphiteGlassSkin());

		iframe.setVisible(true);

		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(panel, BorderLayout.SOUTH);
		this.getContentPane().add(desktop, BorderLayout.CENTER);

		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}

	private Component getComboBox() {
		final JComboBox jcb = new JComboBox();
		jcb.addItem("First Item");
		jcb.addItem("Second Item");
		jcb.addItem("Third Item");
		jcb.addItem("Fourth Item");
		jcb.addItem("Fifth Item");
		jcb.addItem("Sixth Item");
		jcb.addItem("Seventh Item");
		jcb.addItem("Eighth Item");
		jcb.addItem("Ninth Item");
		return jcb;
	}

	public static void main(final String[] args) {
		SubstanceLookAndFeel.setSkin(new CremeCoffeeSkin());

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				final Issue439 c = new Issue439();
				c.setVisible(true);
			}
		});
	}

}
