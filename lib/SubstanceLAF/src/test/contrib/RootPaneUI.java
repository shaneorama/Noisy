package test.contrib;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;

import org.jvnet.substance.SubstanceRootPaneUI;

public class RootPaneUI extends SubstanceRootPaneUI {

	public static ComponentUI createUI(JComponent c) {
		return new RootPaneUI();
	}

	/**
	 * custom titlepane
	 */
	@Override
	public JComponent createTitlePane(JRootPane root) {
		return new JLabel(
				"This is a very-very-very-very-very-very-very wide title pane component.");
	}
}