package test.contrib;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Random;

import javax.swing.*;

import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.api.SubstanceConstants.TabCloseKind;
import org.jvnet.substance.api.tabbed.TabCloseCallback;
import org.jvnet.substance.api.tabbed.TabCloseListener;
import org.jvnet.substance.skin.SubstanceModerateLookAndFeel;

public class TabCloseProblem implements TabCloseListener {

	public TabCloseProblem() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					try {
						UIManager
								.setLookAndFeel(new SubstanceModerateLookAndFeel());
					} catch (UnsupportedLookAndFeelException e) {
						e.printStackTrace();
						System.exit(0);
					}
					initGUI();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		return;
	}

	private void initGUI() throws ClassNotFoundException {

		// Set up Tab Pane
		JTabbedPane tabPane = new JTabbedPane();
		SubstanceLookAndFeel.registerTabCloseChangeListener(tabPane, this);
		SubstanceLookAndFeel.registerTabCloseChangeListener(this);
		tabPane.putClientProperty(
				SubstanceLookAndFeel.TABBED_PANE_CLOSE_BUTTONS_PROPERTY,
				Boolean.TRUE);
		setupTabCloseOptions(tabPane);

		// Populate Tab Pane
		Random r = new Random();
		for (int i = 0; i < 5; i++) {
			JPanel p = new JPanel();
			p.setBackground(new Color(r.nextInt(255), r.nextInt(255), r
					.nextInt(255)));
			tabPane.addTab("TAB#" + (i + 1), p);
		}

		// Display Tab Pane
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(tabPane, BorderLayout.CENTER);
		JFrame frame = new JFrame("TAB TEST");
		frame.add(panel);
		frame.setSize(500, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		new TabCloseProblem();
	}

	private void setupTabCloseOptions(JTabbedPane tabPane) {
		// create a custom implementation of TabCloseCallback interface.
		TabCloseCallback closeCallback = new TabCloseCallback() {
			public TabCloseKind onAreaClick(JTabbedPane tabbedPane,
					int tabIndex, MouseEvent mouseEvent) {

				return TabCloseKind.NONE;
			}

			public TabCloseKind onCloseButtonClick(JTabbedPane tabbedPane,
					int tabIndex, MouseEvent mouseEvent) {

				if (mouseEvent.isPopupTrigger()) {
					return TabCloseKind.NONE;
				} else {
					if (mouseEvent.isAltDown()) {
						return TabCloseKind.ALL_BUT_THIS;
					} else if (mouseEvent.isShiftDown()) {
						return TabCloseKind.ALL;
					} else {
						return TabCloseKind.THIS;
					}
				}
			}

			public String getAreaTooltip(JTabbedPane tabbedPane, int tabIndex) {
				return null;
			}

			public String getCloseButtonTooltip(JTabbedPane tabbedPane,
					int tabIndex) {
				StringBuffer result = new StringBuffer();
				result
						.append("<html><b><i>[L-click] </b></i>Close this plot (Fires tabClosed() method).<br>");
				result
						.append("<b><i>[Shift+L-click] </b></i>Close all plots (Does not fire tabClosed() method).<br>");
				result
						.append("<b><i>[Alt+L-click] </b></i>Close all other plots (Does not fire tabClosed() method).");
				return result.toString();
			}
		};

		// register the callback on the tabbed pane
		tabPane.putClientProperty(
				SubstanceLookAndFeel.TABBED_PANE_CLOSE_CALLBACK, closeCallback);
	}

	public void tabClosed(JTabbedPane arg0, Component arg1) {
		System.out.println("TAB CLOSED!");
	}

	public void tabClosing(JTabbedPane arg0, Component arg1) {
		System.out.println("Tab closing...");
	}
}