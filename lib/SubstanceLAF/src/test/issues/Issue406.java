package test.issues;

import java.awt.*;

import javax.swing.*;

import org.jvnet.substance.skin.SubstanceBusinessBlueSteelLookAndFeel;

@SuppressWarnings("serial")
public class Issue406 extends JFrame {

	public static void main(String[] args) {
		try {
			UIManager
					.setLookAndFeel(new SubstanceBusinessBlueSteelLookAndFeel());
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					Issue406 testFrame = new Issue406();
					testFrame.setVisible(true);
				}
			});
		} catch (UnsupportedLookAndFeelException exception) {
			exception.printStackTrace();
		}
	}

	/**
	 * Constructeur
	 */
	public Issue406() {
		initComponents();
	}

	/**
	 * Initialisation des composants graphiques
	 */
	private void initComponents() {
		this.label1 = new JLabel();
		this.checkBox1 = new JCheckBox();

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		Container contentPane = getContentPane();
		contentPane.setLayout(new FlowLayout());

		this.label1.setText("text"); //$NON-NLS-1$
		this.label1.setForeground(Color.orange);
		contentPane.add(this.label1);

		this.checkBox1.setText("text"); //$NON-NLS-1$
		this.checkBox1.setForeground(Color.orange);
		contentPane.add(this.checkBox1);
		pack();
		setLocationRelativeTo(getOwner());
	}

	private JLabel label1;
	private JCheckBox checkBox1;
}