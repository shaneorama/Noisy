package test.samples.lafwidget.clientprop;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import org.jvnet.lafwidget.LafWidget;
import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.skin.BusinessBlackSteelSkin;

/**
 * Test application that shows the use of the
 * {@link LafWidget#TEXT_FLIP_SELECT_ON_ESCAPE} client property.
 * 
 * @author Kirill Grouchnikov
 * @see LafWidget#TEXT_FLIP_SELECT_ON_ESCAPE
 */
public class TextFlipSelectOnEscape extends JFrame {
	/**
	 * Creates the main frame for <code>this</code> sample.
	 */
	public TextFlipSelectOnEscape() {
		super("Text flip select on ESC");

		this.setLayout(new BorderLayout());

		final JTextField jtf = new JTextField("sample text");
		jtf.setColumns(20);

		JPanel main = new JPanel(new FlowLayout(FlowLayout.CENTER));
		this.add(main, BorderLayout.CENTER);
		main.add(jtf);

		JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT));

		final JCheckBox hasSelectOnFocus = new JCheckBox(
				"Has \"flip select on ESC\" behaviour");
		hasSelectOnFocus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jtf.putClientProperty(LafWidget.TEXT_FLIP_SELECT_ON_ESCAPE,
						hasSelectOnFocus.isSelected() ? Boolean.TRUE : null);
			}
		});

		controls.add(hasSelectOnFocus);
		this.add(controls, BorderLayout.SOUTH);

		this.setSize(400, 200);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	/**
	 * The main method for <code>this</code> sample. The arguments are ignored.
	 * 
	 * @param args
	 *            Ignored.
	 * @throws Exception
	 *             If some exception occured. Note that there is no special
	 *             treatment of exception conditions in <code>this</code> sample
	 *             code.
	 */
	public static void main(String[] args) throws Exception {
		JFrame.setDefaultLookAndFeelDecorated(true);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				SubstanceLookAndFeel.setSkin(new BusinessBlackSteelSkin());
				new TextFlipSelectOnEscape().setVisible(true);
			}
		});
	}
}
