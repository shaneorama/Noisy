/*
 * Copyright (c) 2005-2008 Substance Kirill Grouchnikov. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * 
 *  o Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer. 
 *     
 *  o Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution. 
 *     
 *  o Neither the name of Substance Kirill Grouchnikov nor the names of 
 *    its contributors may be used to endorse or promote products derived 
 *    from this software without specific prior written permission. 
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR 
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; 
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE 
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. 
 */
package test.samples.substance.api;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.Set;

import javax.swing.*;

import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.api.SubstanceConstants.TabCloseKind;
import org.jvnet.substance.api.tabbed.MultipleTabCloseListener;
import org.jvnet.substance.api.tabbed.TabCloseCallback;
import org.jvnet.substance.skin.BusinessBlackSteelSkin;

import test.Check;

/**
 * Test application that shows the use of the
 * {@link SubstanceLookAndFeel#registerTabCloseChangeListener(org.jvnet.substance.tabbed.BaseTabCloseListener)}
 * API with registering a tab close listener that listens on multiple tab
 * closing on a specific tabbed pane.
 * 
 * @author Kirill Grouchnikov
 * @see SubstanceLookAndFeel#registerTabCloseChangeListener(org.jvnet.substance.tabbed.BaseTabCloseListener)
 */
public class RegisterTabCloseChangeListener_GeneralMultiple extends JFrame {
	/**
	 * Creates the main frame for <code>this</code> sample.
	 */
	public RegisterTabCloseChangeListener_GeneralMultiple() {
		super("Register tab close listener");

		this.setLayout(new BorderLayout());

		final JTabbedPane jtp = new JTabbedPane();
		jtp.addTab("tab1", Check.getIcon("flag_sweden"), new JPanel());
		jtp.addTab("tab2", Check.getIcon("flag_mexico"), new JPanel());
		jtp.addTab("tab3", Check.getIcon("flag_hong_kong"), new JPanel());

		jtp.putClientProperty(
				SubstanceLookAndFeel.TABBED_PANE_CLOSE_BUTTONS_PROPERTY,
				Boolean.TRUE);

		// create a custom implementation of TabCloseCallback interface.
		TabCloseCallback closeCallback = new TabCloseCallback() {
			public TabCloseKind onAreaClick(JTabbedPane tabbedPane,
					int tabIndex, MouseEvent mouseEvent) {
				if (mouseEvent.getButton() != MouseEvent.BUTTON3)
					return TabCloseKind.NONE;
				if (mouseEvent.isShiftDown()) {
					return TabCloseKind.ALL;
				}
				return TabCloseKind.THIS;
			}

			public TabCloseKind onCloseButtonClick(JTabbedPane tabbedPane,
					int tabIndex, MouseEvent mouseEvent) {
				if (mouseEvent.isAltDown()) {
					return TabCloseKind.ALL_BUT_THIS;
				}
				if (mouseEvent.isShiftDown()) {
					return TabCloseKind.ALL;
				}
				return TabCloseKind.THIS;
			}

			public String getAreaTooltip(JTabbedPane tabbedPane, int tabIndex) {
				return null;
			}

			public String getCloseButtonTooltip(JTabbedPane tabbedPane,
					int tabIndex) {
				StringBuffer result = new StringBuffer();
				result.append("<html><body>");
				result.append("Mouse click closes <b>"
						+ tabbedPane.getTitleAt(tabIndex) + "</b> tab");
				result
						.append("<br><b>Alt</b>-Mouse click closes all tabs but <b>"
								+ tabbedPane.getTitleAt(tabIndex) + "</b> tab");
				result.append("<br><b>Shift</b>-Mouse click closes all tabs");
				result.append("</body></html>");
				return result.toString();
			}
		};

		// register the callback on the tabbed pane
		jtp.putClientProperty(SubstanceLookAndFeel.TABBED_PANE_CLOSE_CALLBACK,
				closeCallback);

		// register tab close listener on all tabbed panes.
		SubstanceLookAndFeel
				.registerTabCloseChangeListener(new MultipleTabCloseListener() {
					public void tabsClosing(JTabbedPane tabbedPane,
							Set<Component> tabComponents) {
						StringBuffer sb = new StringBuffer("Tab (s)");
						String sep = " [";
						for (Component comp : tabComponents) {
							sb.append(sep);
							sep = ", ";
							sb.append(tabbedPane.getTitleAt(tabbedPane
									.indexOfComponent(comp)));
						}
						sb.append("] closing");
						System.out.println(sb.toString());
					}

					public void tabsClosed(JTabbedPane tabbedPane,
							Set<Component> tabComponents) {
						System.out.println(tabComponents.size()
								+ " tab(s) closed");
					}
				});

		this.add(jtp, BorderLayout.CENTER);

		this.setSize(400, 200);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	/**
	 * The main method for <code>this</code> sample. The arguments are ignored.
	 * 
	 * @param args
	 *            Ignored.
	 */
	public static void main(String[] args) {
		JFrame.setDefaultLookAndFeelDecorated(true);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				SubstanceLookAndFeel.setSkin(new BusinessBlackSteelSkin());
				new RegisterTabCloseChangeListener_GeneralMultiple()
						.setVisible(true);
			}
		});
	}
}
