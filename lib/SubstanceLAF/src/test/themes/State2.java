package test.themes;

import java.awt.BorderLayout;

import javax.swing.*;

import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.api.SubstanceConstants.FocusKind;
import org.jvnet.substance.skin.OfficeSilver2007Skin;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

public class State2 extends JFrame {
	public State2() {
		super("States");
		this.setLayout(new BorderLayout());

		JButton defaultButton = new JButton("sample");

		JButton rolloverButton = new JButton("sample");
		rolloverButton.getModel().setRollover(true);

		JButton selectedButton = new JButton("sample");
		selectedButton.getModel().setSelected(true);

		JButton rolloverSelectedButton = new JButton("sample");
		rolloverSelectedButton.getModel().setRollover(true);
		rolloverSelectedButton.getModel().setSelected(true);

		JButton pressedButton = new JButton("sample");
		pressedButton.getModel().setPressed(true);
		pressedButton.getModel().setArmed(true);

		JButton pressedSelectedButton = new JButton("sample");
		pressedSelectedButton.getModel().setPressed(true);
		pressedSelectedButton.getModel().setSelected(true);
		pressedSelectedButton.getModel().setArmed(true);

		FormLayout lm = new FormLayout("right:pref, 4dlu, fill:pref:grow", "");
		DefaultFormBuilder builder = new DefaultFormBuilder(lm);
		builder.append("Regular", defaultButton);
		builder.append("Rollover", rolloverButton);
		builder.append("Selected", selectedButton);
		builder.append("Rollover selected", rolloverSelectedButton);
		builder.append("Pressed", pressedButton);
		builder.append("Pressed selected", pressedSelectedButton);

		builder.setDefaultDialogBorder();

		this.add(builder.getPanel());

		this.pack();
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public static void main(String[] args) {
		SubstanceLookAndFeel.setSkin(new OfficeSilver2007Skin());
		UIManager.put(SubstanceLookAndFeel.FOCUS_KIND, FocusKind.NONE);
		JFrame.setDefaultLookAndFeelDecorated(true);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new State2().setVisible(true);
			}
		});
	}

}
