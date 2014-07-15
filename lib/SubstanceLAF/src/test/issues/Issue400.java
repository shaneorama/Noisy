package test.issues;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.skin.SubstanceBusinessBlackSteelLookAndFeel;

/**
 * Test application that shows the use of the {@link
 * SubstanceLookAndFeel#COLORIZATION_FACTOR} client property.
 * 
 * @author Kirill Grouchnikov
 * @see SubstanceLookAndFeel#COLORIZATION_FACTOR
 */
public class Issue400 extends JFrame {
	/**
	 * Creates the main frame for <code>this</code> sample.
	 */
	public Issue400() {
		super("Colorization factor");

		this.setLayout(new BorderLayout());

		// CODE TAKEN DIRECT FROM ColorizationFactor
		final JPanel panel = new JPanel(new FlowLayout());
		JButton button = new JButton("sample");
		button.setBackground(Color.yellow);
		button.setForeground(Color.red);
		panel.add(button);
		JCheckBox checkbox = new JCheckBox("sample");
		checkbox.setSelected(true);
		checkbox.setBackground(Color.green.brighter());
		checkbox.setForeground(Color.blue.darker());
		panel.add(checkbox);
		JRadioButton radiobutton = new JRadioButton("sample");
		radiobutton.setSelected(true);
		radiobutton.setBackground(Color.yellow);
		radiobutton.setForeground(Color.green.darker());
		panel.add(radiobutton);
		panel.setBackground(Color.RED);

		// ADDITION OF MENUBAR AND LABELS
		final JMenuBar menuBar = new JMenuBar();
		
		menuBar.setBackground(Color.RED);
		menuBar.setOpaque(true);

		JCheckBox menuCheckBox = new JCheckBox("Menu Check Box works...");
		menuCheckBox.setSelected(true);
		menuCheckBox.setBackground(Color.green.brighter());
		menuCheckBox.setForeground(Color.blue.darker());
		menuBar.add(menuCheckBox);

		JLabel menuLabel = new JLabel("Menu label doesn't work...");
		menuLabel.setOpaque(true);
		menuLabel.setBackground(Color.blue);
		menuBar.add(menuLabel);

		JLabel panelLabel = new JLabel("Panel label works...");
		panelLabel.setOpaque(true);
		panelLabel.setBackground(Color.blue);
		panel.add(panelLabel);

		this.add(menuBar, BorderLayout.NORTH);
		this.add(panel, BorderLayout.CENTER);
		
		

		JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		final JSlider colorizationSlider = new JSlider(0, 100, 50);
		colorizationSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				double val = colorizationSlider.getValue() / 100.0;
				System.out.println("Current val: " + val);
				panel.putClientProperty(
						SubstanceLookAndFeel.COLORIZATION_FACTOR, new Double(
								val));
				panel.repaint();
				menuBar.putClientProperty(
						SubstanceLookAndFeel.COLORIZATION_FACTOR, new Double(
								val));
				menuBar.repaint();
			}
		});
		
		JButton textErrorButton = new JButton("Set colorization to 0.29");
		textErrorButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				colorizationSlider.setValue(29);
			}			
		});
		
		controls.add(textErrorButton);
		controls.add(colorizationSlider);

		this.add(controls, BorderLayout.SOUTH);

		this.setSize(400, 200);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	/**
	 * The main method for <code>this</code> sample. The arguments are ignored.
	 * 
	 * @param args
	 *     Ignored.
	 * @throws Exception
	 *     If some exception occured. Note that there is no special treatment
	 *     of exception conditions in <code>this</code> sample code.
	 */
	public static void main(String[] args) throws Exception {
		UIManager.setLookAndFeel(new SubstanceBusinessBlackSteelLookAndFeel());
		JFrame.setDefaultLookAndFeelDecorated(true);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new Issue400().setVisible(true);
			}
		});
	}
}