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
package org.jvnet.substance.utils.combo;

import java.awt.Insets;

import javax.swing.*;

import org.jvnet.lafwidget.animation.FadeConfigurationManager;
import org.jvnet.lafwidget.animation.FadeKind;
import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.utils.SubstanceArrowButton;
import org.jvnet.substance.utils.SubstanceSizeUtils;

/**
 * Combo box button in <b>Substance</b> look and feel.
 * 
 * @author Kirill Grouchnikov
 */
@SubstanceArrowButton
public final class SubstanceComboBoxButton extends JButton {
	static {
		FadeConfigurationManager.getInstance().disallowFades(
				FadeKind.GHOSTING_BUTTON_PRESS, SubstanceComboBoxButton.class);
		FadeConfigurationManager.getInstance().disallowFades(
				FadeKind.GHOSTING_ICON_ROLLOVER, SubstanceComboBoxButton.class);
	}

	/**
	 * Simple constructor.
	 * 
	 * @param comboBox
	 *            The owner combo box.
	 * @param comboIcon
	 *            The button icon (down arrow).
	 */
	public SubstanceComboBoxButton(JComboBox comboBox) {
		super("");
		this.setModel(new DefaultButtonModel() {
			@Override
			public void setArmed(boolean armed) {
				super.setArmed(this.isPressed() || armed);
			}
		});
		this.setEnabled(comboBox.isEnabled());
		this.setFocusable(false);
		this.setRequestFocusEnabled(comboBox.isEnabled());

		int fontSize = SubstanceSizeUtils.getComponentFontSize(comboBox);
		int tbInset = SubstanceSizeUtils.getAdjustedSize(fontSize, 1, 2, 1,
				false);
		int lrInset = SubstanceSizeUtils.getAdjustedSize(fontSize, 0, 2, 1,
				false);
		this.setMargin(new Insets(tbInset, lrInset, tbInset, tbInset));
		this.putClientProperty(SubstanceLookAndFeel.USE_THEMED_DEFAULT_ICONS,
				Boolean.FALSE);
	}
}
