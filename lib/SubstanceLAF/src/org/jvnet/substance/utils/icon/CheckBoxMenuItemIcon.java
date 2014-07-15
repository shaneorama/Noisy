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
package org.jvnet.substance.utils.icon;

import java.awt.Component;
import java.awt.Graphics;

import javax.swing.*;
import javax.swing.plaf.UIResource;

import org.jvnet.lafwidget.animation.FadeKind;
import org.jvnet.lafwidget.animation.FadeState;
import org.jvnet.substance.api.*;
import org.jvnet.substance.painter.border.SubstanceBorderPainter;
import org.jvnet.substance.painter.gradient.SubstanceGradientPainter;
import org.jvnet.substance.utils.*;

/**
 * Icon for the {@link JCheckBoxMenuItem}s.
 * 
 * @author Kirill Grouchnikov
 */
public class CheckBoxMenuItemIcon implements Icon, UIResource {
	/**
	 * The size of <code>this</code> icon.
	 */
	private int size;

	/**
	 * The associated menu item.
	 */
	private JMenuItem menuItem;

	/**
	 * Icon cache to speed up the painting.
	 */
	private static LazyResettableHashMap<Icon> iconMap = new LazyResettableHashMap<Icon>(
			"CheckBoxMenuItemIcon");

	/**
	 * Creates a new icon.
	 * 
	 * @param menuItem
	 *            The corresponding menu item.
	 * @param size
	 *            The size of <code>this</code> icon.
	 */
	public CheckBoxMenuItemIcon(JMenuItem menuItem, int size) {
		this.menuItem = menuItem;
		this.size = size;
	}

	/**
	 * Returns the current icon to paint.
	 * 
	 * @return Icon to paint.
	 */
	private Icon getIconToPaint() {
		if (this.menuItem == null)
			return null;
		ComponentState currState = ComponentState.getState(this.menuItem
				.getModel(), this.menuItem, false);
		ComponentState prevState = SubstanceCoreUtilities
				.getPrevSelComponentState(this.menuItem);

		float checkMarkVisibility = currState.isKindActive(FadeKind.SELECTION) ? 10
				: 0;
		boolean isCheckMarkFadingOut = false;

		SubstanceColorScheme currFillColorScheme = SubstanceColorSchemeUtilities
				.getColorScheme(this.menuItem, ColorSchemeAssociationKind.FILL,
						currState);
		SubstanceColorScheme prevFillColorScheme = SubstanceColorSchemeUtilities
				.getColorScheme(this.menuItem, ColorSchemeAssociationKind.FILL,
						prevState);

		SubstanceColorScheme currMarkColorScheme = SubstanceColorSchemeUtilities
				.getColorScheme(this.menuItem, ColorSchemeAssociationKind.MARK,
						currState);
		SubstanceColorScheme prevMarkColorScheme = SubstanceColorSchemeUtilities
				.getColorScheme(this.menuItem, ColorSchemeAssociationKind.MARK,
						prevState);

		SubstanceColorScheme currBorderColorScheme = SubstanceColorSchemeUtilities
				.getColorScheme(this.menuItem,
						ColorSchemeAssociationKind.BORDER, currState);
		SubstanceColorScheme prevBorderColorScheme = SubstanceColorSchemeUtilities
				.getColorScheme(this.menuItem,
						ColorSchemeAssociationKind.BORDER, prevState);

		float cyclePos = 0;
		FadeState fadeState = SubstanceFadeUtilities.getFadeState(
				this.menuItem, FadeKind.SELECTION, FadeKind.ROLLOVER,
				FadeKind.PRESS, FadeKind.ARM);
		if (fadeState != null) {
			cyclePos = fadeState.getFadePosition();
			if (fadeState.isFadingIn())
				cyclePos = 1.0f - cyclePos;
			if (fadeState.fadeKind == FadeKind.SELECTION) {
				checkMarkVisibility = fadeState.getFadePosition();
				isCheckMarkFadingOut = !fadeState.isFadingIn();
			}
		}
		SubstanceGradientPainter fillPainter = SubstanceCoreUtilities
				.getGradientPainter(this.menuItem);
		SubstanceBorderPainter borderPainter = SubstanceCoreUtilities
				.getBorderPainter(this.menuItem);
		int fontSize = SubstanceSizeUtils.getComponentFontSize(this.menuItem);

		HashMapKey key = SubstanceCoreUtilities.getHashKey(fontSize, currState
				.name(), prevState.name(),
				currFillColorScheme.getDisplayName(), prevFillColorScheme
						.getDisplayName(),
				currMarkColorScheme.getDisplayName(), prevMarkColorScheme
						.getDisplayName(), currBorderColorScheme
						.getDisplayName(), prevBorderColorScheme
						.getDisplayName(), cyclePos, checkMarkVisibility,
				this.size);

		// System.out.println(key);

		Icon result = iconMap.get(key);
		if (result != null)
			return result;
		result = new ImageIcon(SubstanceImageCreator.getCheckBox(this.menuItem,
				fillPainter, borderPainter, this.size + 3, currState,
				prevState, currFillColorScheme, prevFillColorScheme,
				currMarkColorScheme, prevMarkColorScheme,
				currBorderColorScheme, prevBorderColorScheme, cyclePos,
				checkMarkVisibility / 10.f, isCheckMarkFadingOut));

		// if (this.menuItem.isSelected()) {
		// System.out.println("Sel menu : " + currState + " : "
		// + scheme.getDisplayName());
		// }

		iconMap.put(key, result);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.Icon#paintIcon(java.awt.Component, java.awt.Graphics,
	 * int, int)
	 */
	public void paintIcon(Component c, Graphics g, int x, int y) {
		Icon iconToDraw = this.getIconToPaint();
		if (iconToDraw != null)
			iconToDraw.paintIcon(c, g, x, y);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.Icon#getIconWidth()
	 */
	public int getIconWidth() {
		return this.size + 2;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.Icon#getIconHeight()
	 */
	public int getIconHeight() {
		return this.size + 2;
	}
}
