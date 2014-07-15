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

import java.awt.*;
import java.awt.image.BufferedImage;

import javax.swing.*;

import org.jvnet.lafwidget.animation.FadeKind;
import org.jvnet.lafwidget.animation.FadeState;
import org.jvnet.substance.api.*;
import org.jvnet.substance.utils.*;

/**
 * Icon with transition-aware capabilities. Has a delegate that does the actual
 * painting based on the transition color schemes. This class is used heavily on
 * Substance-provided icons, such as title pane button icons, arrow icons on
 * scroll bars and combos etc.
 * 
 * @author Kirill Grouchnikov
 */
@TransitionAware
public class TransitionAwareIcon implements Icon {
	/**
	 * The delegate needs to implement the method in this interface based on the
	 * provided color scheme. The color scheme is computed based on the
	 * transitions that are happening on the associated button.
	 * 
	 * @author Kirill Grouchnikov
	 */
	public static interface Delegate {
		/**
		 * Returns the icon that matches the specified scheme.
		 * 
		 * @param scheme
		 *            Color scheme.
		 * @return Icon that matches the specified scheme.
		 */
		public Icon getColorSchemeIcon(SubstanceColorScheme scheme);
	}

	public static interface ColorSchemeAssociationKindDelegate {
		public ColorSchemeAssociationKind getColorSchemeAssociationKind(
				ComponentState state);
	}

	/**
	 * The associated component.
	 */
	private JComponent comp;

	/**
	 * The associated model.
	 */
	private ButtonModel model;

	/**
	 * Delegate to compute the actual icons.
	 */
	protected Delegate delegate;

	protected ColorSchemeAssociationKindDelegate colorSchemeAssociationKindDelegate;

	protected String uniqueIconTypeId;

	/**
	 * Icon cache to speed up the subsequent icon painting. The basic assumption
	 * is that the {@link #delegate} returns an icon that paints the same for
	 * the same parameters.
	 */
	private static LazyResettableHashMap<Icon> iconMap = new LazyResettableHashMap<Icon>(
			"TransitionAwareIcon");

	private int iconWidth;

	private int iconHeight;

	/**
	 * Creates a new transition-aware icon.
	 * 
	 * @param button
	 *            Associated button.
	 * @param delegate
	 *            Delegate to compute the actual icons.
	 */
	public TransitionAwareIcon(AbstractButton button, Delegate delegate,
			String uniqueIconTypeId) {
		this(button, (button == null) ? null : button.getModel(), delegate,
				uniqueIconTypeId);
	}

	/**
	 * Creates a new transition-aware icon.
	 * 
	 * @param comp
	 *            Associated component.
	 * @param model
	 *            Associated model.
	 * @param delegate
	 *            Delegate to compute the actual icons.
	 */
	public TransitionAwareIcon(JComponent comp, ButtonModel model,
			Delegate delegate, String uniqueIconTypeId) {
		this(comp, model, delegate, null, uniqueIconTypeId);
	}

	/**
	 * Creates a new transition-aware icon.
	 * 
	 * @param comp
	 *            Associated component.
	 * @param model
	 *            Associated model.
	 * @param delegate
	 *            Delegate to compute the actual icons.
	 */
	public TransitionAwareIcon(
			JComponent comp,
			ButtonModel model,
			Delegate delegate,
			ColorSchemeAssociationKindDelegate colorSchemeAssociationKindDelegate,
			String uniqueIconTypeId) {
		this.comp = comp;
		this.model = model;
		this.delegate = delegate;
		this.colorSchemeAssociationKindDelegate = colorSchemeAssociationKindDelegate;
		this.uniqueIconTypeId = uniqueIconTypeId;
		this.iconWidth = this.delegate.getColorSchemeIcon(
				SubstanceColorSchemeUtilities
						.getColorScheme(comp, ColorSchemeAssociationKind.MARK,
								ComponentState.DEFAULT)).getIconWidth();
		this.iconHeight = this.delegate.getColorSchemeIcon(
				SubstanceColorSchemeUtilities
						.getColorScheme(comp, ColorSchemeAssociationKind.MARK,
								ComponentState.DEFAULT)).getIconHeight();
	}

	/**
	 * Returns the current icon to paint.
	 * 
	 * @return Icon to paint.
	 */
	private Icon getIconToPaint() {
		ComponentState currState = ComponentState.getState(this.model,
				this.comp);
		ComponentState prevState = SubstanceCoreUtilities
				.getPrevComponentState(this.comp);

		if (!currState.isKindActive(FadeKind.ENABLE))
			prevState = currState;
		float cyclePos = currState.getCyclePosition();

		ColorSchemeAssociationKind currColorSchemeAssociationKind = (this.colorSchemeAssociationKindDelegate == null) ? ColorSchemeAssociationKind.MARK
				: this.colorSchemeAssociationKindDelegate
						.getColorSchemeAssociationKind(currState);
		SubstanceColorScheme currScheme = SubstanceColorSchemeUtilities
				.getColorScheme(this.comp, currColorSchemeAssociationKind,
						currState);

		SubstanceColorScheme prevScheme = currScheme;

		FadeState fadeState = SubstanceFadeUtilities.getFadeState(this.comp,
				FadeKind.ROLLOVER, FadeKind.SELECTION, FadeKind.PRESS,
				FadeKind.ARM);
		if (fadeState != null) {
			ColorSchemeAssociationKind prevColorSchemeAssociationKind = (this.colorSchemeAssociationKindDelegate == null) ? ColorSchemeAssociationKind.MARK
					: this.colorSchemeAssociationKindDelegate
							.getColorSchemeAssociationKind(prevState);
			prevScheme = SubstanceColorSchemeUtilities.getColorScheme(
					this.comp, prevColorSchemeAssociationKind, prevState);
			cyclePos = fadeState.getFadePosition();
			if (!fadeState.isFadingIn())
				cyclePos = 1.0f - cyclePos;
		}
		float currAlpha = SubstanceColorSchemeUtilities.getAlpha(this.comp,
				currState);
		float prevAlpha = SubstanceColorSchemeUtilities.getAlpha(this.comp,
				prevState);

		HashMapKey key = SubstanceCoreUtilities.getHashKey(
				this.uniqueIconTypeId, SubstanceSizeUtils
						.getComponentFontSize(this.comp), currScheme
						.getDisplayName(), prevScheme.getDisplayName(),
				currAlpha, prevAlpha, cyclePos);
		// System.out.println(key);
		if (!iconMap.containsKey(key)) {
			Icon icon = this.delegate.getColorSchemeIcon(currScheme);
			Icon prevIcon = this.delegate.getColorSchemeIcon(prevScheme);

			BufferedImage temp = SubstanceCoreUtilities.getBlankImage(icon
					.getIconWidth(), icon.getIconHeight());
			Graphics2D g2d = temp.createGraphics();

			if (currScheme == prevScheme) {
				// same color scheme - can paint just the current icon, no
				// matter what the cycle position is.
				g2d.setComposite(AlphaComposite.getInstance(
						AlphaComposite.SRC_OVER, currAlpha));
				icon.paintIcon(this.comp, g2d, 0, 0);
			} else {
				// make optimizations for limit values of the cycle position.
				if (cyclePos < 1.0f) {
					g2d.setComposite(AlphaComposite.SrcOver.derive(prevAlpha));
					prevIcon.paintIcon(this.comp, g2d, 0, 0);
				}
				if (cyclePos > 0.0f) {
					g2d.setComposite(AlphaComposite.SrcOver.derive(currAlpha
							* cyclePos));
					icon.paintIcon(this.comp, g2d, 0, 0);
				}
			}

			iconMap.put(key, new ImageIcon(temp));
			g2d.dispose();
		}

		Icon result = iconMap.get(key);
		// if (result.getIconHeight() != this.getIconHeight())
		// throw new IllegalStateException("Component "
		// + comp.getClass().getSimpleName() + "[@" + comp.hashCode()
		// + "] in " + comp.getParent().getClass().getSimpleName()
		// + "[@" + comp.getParent().hashCode()
		// + "] - unexpected height mismatch : "
		// + result.getIconHeight() + ":" + this.getIconHeight());
		// if (result.getIconWidth() != this.getIconWidth())
		// throw new IllegalStateException("Component "
		// + comp.getClass().getSimpleName() + "[@" + comp.hashCode()
		// + "] in " + comp.getParent().getClass().getSimpleName()
		// + "[@" + comp.getParent().hashCode()
		// + "] - unexpected width mismatch : "
		// + result.getIconWidth() + ":" + this.getIconWidth());

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.Icon#getIconHeight()
	 */
	public int getIconHeight() {
		return this.iconHeight;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.Icon#getIconWidth()
	 */
	public int getIconWidth() {
		return this.iconWidth;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.Icon#paintIcon(java.awt.Component, java.awt.Graphics,
	 * int, int)
	 */
	public void paintIcon(Component c, Graphics g, int x, int y) {
		this.getIconToPaint().paintIcon(c, g, x, y);
	}
}
