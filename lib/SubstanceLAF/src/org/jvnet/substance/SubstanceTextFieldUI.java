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
import javax.swing.plaf.*;
import javax.swing.plaf.basic.BasicBorders;
import javax.swing.plaf.basic.BasicTextFieldUI;

import org.jvnet.lafwidget.animation.FadeStateListener;
import org.jvnet.substance.utils.*;
import org.jvnet.substance.utils.border.SubstanceBorder;

/**
 * UI for text fields in <b>Substance</b> look and feel.
 * 
 * @author Kirill Grouchnikov
 */
public class SubstanceTextFieldUI extends BasicTextFieldUI {
	/**
	 * Listener for fade animations.
	 */
	protected FadeStateListener substanceFadeStateListener;

	/**
	 * The associated text field.
	 */
	protected JTextField textField;

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
		return new SubstanceTextFieldUI(comp);
	}

	/**
	 * Simple constructor.
	 * 
	 * @param c
	 *            Component (text field).
	 */
	public SubstanceTextFieldUI(JComponent c) {
		super();
		this.textField = (JTextField) c;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.plaf.basic.BasicTextUI#paintBackground(java.awt.Graphics)
	 */
	@Override
	protected void paintBackground(Graphics g) {
		SubstanceTextUtilities.paintTextCompBackground(g, this.textField);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.basic.BasicTextUI#installListeners()
	 */
	@Override
	protected void installListeners() {
		super.installListeners();

		this.substanceFadeStateListener = new FadeStateListener(this.textField,
				null, null);
		this.substanceFadeStateListener.registerListeners(false);

		this.substancePropertyChangeListener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if ("font".equals(evt.getPropertyName())) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							// remember the caret location - issue 404
							int caretPos = textField.getCaretPosition();
							textField.updateUI();
							textField.setCaretPosition(caretPos);
						}
					});
				}
			}
		};
		this.textField
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

		this.textField
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
		Border b = this.textField.getBorder();
		if (b == null || b instanceof UIResource) {
			Border newB = new BorderUIResource.CompoundBorderUIResource(
					new SubstanceBorder(SubstanceSizeUtils
							.getTextBorderInsets(SubstanceSizeUtils
									.getComponentFontSize(this.textField))),
					new BasicBorders.MarginBorder());
			this.textField.setBorder(newB);
		}

		// support for per-window skins
		Color foregr = this.textField.getForeground();
		if ((foregr == null) || (foregr instanceof UIResource)) {
			this.textField.setForeground(SubstanceColorUtilities
					.getForegroundColor(SubstanceLookAndFeel.getCurrentSkin(
							this.textField).getMainDefaultColorScheme()));
		}
	}
}
