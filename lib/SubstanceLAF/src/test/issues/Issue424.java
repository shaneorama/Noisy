package test.issues;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.*;

import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.skin.NebulaBrickWallSkin;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

public class Issue424 extends JFrame {
	public Issue424() {
		FormLayout lm = new FormLayout("fill:pref");
		DefaultFormBuilder builder = new DefaultFormBuilder(lm);
		JComboBox combo = new JComboBox(new Object[] { "sample" });
		combo.setFont(new Font("Tahoma", Font.PLAIN, 11));
		combo.setFocusable(false);
		builder.append(combo);
		JSpinner spinner = new JSpinner(new SpinnerNumberModel(100, 0, 1000, 10));
		spinner.setFont(new Font("Tahoma", Font.PLAIN, 11));
		spinner.setFocusable(false);
		builder.append(spinner);
		
		builder.setDefaultDialogBorder();
		
		this.add(builder.getPanel(), BorderLayout.CENTER);
		this.setSize(300, 200);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SubstanceLookAndFeel.setSkin(new NebulaBrickWallSkin());
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new Issue424().setVisible(true);
			}
		});
	}

}
