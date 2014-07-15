/*
 * Copyright (c) 2005-2009 Substance Kirill Grouchnikov. All Rights Reserved.
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
package org.jvnet.substance;

import java.awt.Color;
import java.awt.Graphics;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.BasicBorders;
import javax.swing.plaf.basic.BasicTextPaneUI;

import org.jvnet.lafwidget.animation.FadeStateListener;
import org.jvnet.substance.utils.*;

/**
 * UI for text panes in <b>Substance</b> look and feel.
 * 
 * @author Kirill Grouchnikov
 */
public class SubstanceTextPaneUI extends BasicTextPaneUI {
	/**
	 * Listener for fade animations.
	 */
	protected FadeStateListener substanceFadeStateListener;

	/**
	 * The associated text pane.
	 */
	protected JTextPane textPane;

	/**
	 * Property change listener.
	 */
	protected PropertyChangeListener substancePropertyChangeListener;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.ComponentUI#createUI(javax.swing.JComponent)
	 */
	public static ComponentUI createUI(JComponent comp) {
		SubstanceCoreUtilities.testComponentCreationThreadingViolation(comp);
		return new SubstanceTextPaneUI(comp);
	}

	/**
	 * Simple constructor.
	 * 
	 * @param c
	 *            Component (text pane).
	 */
	public SubstanceTextPaneUI(JComponent c) {
		super();
		this.textPane = (JTextPane) c;
	}

	@Override
	protected void installListeners() {
		super.installListeners();

		this.substanceFadeStateListener = new FadeStateListener(this.textPane,
				null, null);
		this.substanceFadeStateListener.registerListeners(false);

		this.substancePropertyChangeListener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if ("font".equals(evt.getPropertyName())) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							// remember the caret location - issue 404
							int caretPos = textPane.getCaretPosition();
							textPane.updateUI();
							textPane.setCaretPosition(caretPos);
						}
					});
				}
			}
		};
		this.textPane
				.addPropertyChangeListener(this.substancePropertyChangeListener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.basic.BasicTextUI#uninstallListeners()
	 */
	@Override
	protected void uninstallListeners() {
		this.substanceFadeStateListener.unregisterListeners();
		this.substanceFadeStateListener = null;

		this.textPane
				.removePropertyChangeListener(this.substancePropertyChangeListener);
		this.substancePropertyChangeListener = null;

		super.uninstallListeners();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.basic.BasicTextUI#installDefaults()
	 */
	@Override
	protected void installDefaults() {
		super.installDefaults();
		Border b = this.textPane.getBorder();
		if (b == null || b instanceof UIResource) {
			Border newB = new BorderUIResource.CompoundBorderUIResource(
					new EmptyBorder(SubstanceSizeUtils
							.getTextBorderInsets(SubstanceSizeUtils
									.getComponentFontSize(this.textPane))),
					new BasicBorders.MarginBorder());
			this.textPane.setBorder(newB);
		}

		// support for per-window skins
		Color foregr = this.textPane.getForeground();
		if ((foregr == null) || (foregr instanceof UIResource)) {
			this.textPane.setForeground(SubstanceColorUtilities
					.getForegroundColor(SubstanceLookAndFeel.getCurrentSkin(
							this.textPane).getMainDefaultColorScheme()));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.plaf.basic.BasicTextUI#paintBackground(java.awt.Graphics)
	 */
	@Override
	protected void paintBackground(Graphics g) {
		SubstanceTextUtilities.paintTextCompBackground(g, this.textPane);
	}
}
