package test;

import javax.swing.JLabel;
import javax.swing.UIManager;

import org.jvnet.substance.skin.SubstanceBusinessBlackSteelLookAndFeel;

public class EdtViolation {
	public static void main(String[] args) throws Exception {
		UIManager.setLookAndFeel(new SubstanceBusinessBlackSteelLookAndFeel());
		JLabel label = new JLabel("text");
		
	}

}
