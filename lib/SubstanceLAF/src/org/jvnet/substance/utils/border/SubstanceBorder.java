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
package org.jvnet.substance.utils.border;

import java.awt.*;
import java.awt.image.BufferedImage;

import javax.swing.border.Border;
import javax.swing.plaf.UIResource;

import org.jvnet.lafwidget.animation.FadeKind;
import org.jvnet.lafwidget.animation.FadeTracker;
import org.jvnet.lafwidget.layout.TransitionLayout;
import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.api.*;
import org.jvnet.substance.utils.*;

/**
 * Gradient border for the <b>Substance</b> look and feel. This class is <b>for
 * internal use only</b>.
 * 
 * @author Kirill Grouchnikov
 */
public class SubstanceBorder implements Border, UIResource {
	/**
	 * Insets of <code>this</code> border.
	 */
	protected Insets myInsets;

	/**
	 * Border opacity.
	 */
	protected float alpha;

	/**
	 * When the border is painted, the default radius is multiplied by this
	 * factor.
	 */
	protected float radiusScaleFactor;

	/**
	 * Cache of small border images.
	 */
	private static LazyResettableHashMap<BufferedImage> smallImageCache = new LazyResettableHashMap<BufferedImage>(
			"SubstanceBorder");

	/**
	 * Creates a new border with dynamic insets (computed at the invocation time
	 * of {@link #getBorderInsets(Component)} call).
	 */
	public SubstanceBorder() {
		super();
		this.alpha = 1.0f;
		this.radiusScaleFactor = 0.5f;
	}

	/**
	 * Creates a new border with dynamic insets (computed at the invocation time
	 * of {@link #getBorderInsets(Component)} call).
	 * 
	 * @param radiusScaleFactor
	 *            Radius scale factor.
	 */
	public SubstanceBorder(float radiusScaleFactor) {
		this();
		this.radiusScaleFactor = radiusScaleFactor;
	}

	/**
	 * Creates a new border with the specified insets.
	 * 
	 * @param insets
	 *            Insets.
	 */
	public SubstanceBorder(Insets insets) {
		this();
		this.myInsets = new Insets(insets.top, insets.left, insets.bottom,
				insets.right);
	}

	/**
	 * Sets the alpha for this border.
	 * 
	 * @param alpha
	 *            Alpha factor.
	 */
	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}

	/**
	 * Paints border instance for the specified component.
	 * 
	 * @param c
	 *            The component.
	 * @param g
	 *            Graphics context.
	 * @param x
	 *            Component left X (in graphics context).
	 * @param y
	 *            Component top Y (in graphics context).
	 * @param width
	 *            Component width.
	 * @param height
	 *            Component height.
	 * @param isEnabled
	 *            Component enabled status.
	 * @param hasFocus
	 *            Component focus ownership status.
	 * @param alpha
	 *            Alpha value.
	 */
	private void paintBorder(Component c, Graphics g, int x, int y, int width,
			int height, boolean isEnabled, boolean hasFocus, float alpha) {
		// failsafe for LAF change
		if (!SubstanceLookAndFeel.isCurrentLookAndFeel()) {
			return;
		}

		if ((width <= 0) || (height <= 0))
			return;

		Graphics2D graphics = (Graphics2D) g.create();

		float cyclePos = 1.0f;

		FadeTracker fadeTracker = FadeTracker.getInstance();
		boolean isFocusAnimated = fadeTracker.isTracked(c, FadeKind.FOCUS);

		float radius = this.radiusScaleFactor
				* SubstanceSizeUtils
						.getClassicButtonCornerRadius(SubstanceSizeUtils
								.getComponentFontSize(c));

		ComponentState state = isEnabled ? ComponentState.DEFAULT
				: ComponentState.DISABLED_UNSELECTED;
		SubstanceColorScheme borderColorScheme = SubstanceColorSchemeUtilities
				.getColorScheme(c, ColorSchemeAssociationKind.BORDER, state);
		SubstanceColorScheme borderColorScheme2 = borderColorScheme;
		float finalAlpha = alpha;

		if (isFocusAnimated || c.hasFocus()) {
			if (isFocusAnimated) {
				cyclePos = fadeTracker.getFade(c, FadeKind.FOCUS);
			}
			borderColorScheme2 = SubstanceColorSchemeUtilities.getColorScheme(
					c, ColorSchemeAssociationKind.BORDER,
					ComponentState.SELECTED);
		} else {
			finalAlpha *= SubstanceColorSchemeUtilities.getAlpha(c, state);
		}
		graphics.setComposite(TransitionLayout.getAlphaComposite(c, finalAlpha,
				g));

		if (width * height < 100000) {
			HashMapKey hashKey = SubstanceCoreUtilities
					.getHashKey(SubstanceCoreUtilities.getBorderPainter(c)
							.getDisplayName(), SubstanceSizeUtils
							.getComponentFontSize(c), width, height, radius,
							borderColorScheme.getDisplayName(),
							borderColorScheme2.getDisplayName(), cyclePos);
			if (!smallImageCache.containsKey(hashKey)) {
				BufferedImage toCache = SubstanceCoreUtilities.getBlankImage(
						width, height);
				Graphics2D g2d = toCache.createGraphics();
				SubstanceImageCreator
						.paintBorder(c, g2d, 0, 0, width, height, radius,
								borderColorScheme, borderColorScheme2, cyclePos);
				g2d.dispose();
				smallImageCache.put(hashKey, toCache);
			}
			graphics.drawImage(smallImageCache.get(hashKey), x, y, null);
		} else {
			// for borders larger than 100000 pixels, use simple
			// painting

			// SubstanceBorderPainter borderPainter = SubstanceCoreUtilities
			// .getBorderPainter(c);
			// if (borderPainter instanceof InnerDelegateBorderPainter) {
			// InnerDelegateBorderPainter inner = (InnerDelegateBorderPainter)
			// borderPainter;
			// int componentFontSize = SubstanceSizeUtils
			// .getComponentFontSize(c);
			// // int borderDelta = (int) Math.floor(SubstanceSizeUtils
			// // .getBorderStrokeWidth(componentFontSize) / 2.0);
			// int borderThickness = (int) SubstanceSizeUtils
			// .getBorderStrokeWidth(componentFontSize);
			// graphics.translate(x + borderThickness, y + borderThickness);
			// SubstanceImageCreator.paintSimpleBorder(c, graphics, width - 2
			// * borderThickness, height - 2 * borderThickness, inner
			// .getShiftScheme(borderColorScheme), inner
			// .getShiftScheme(borderColorScheme2), cyclePos);
			// graphics.translate(-x - borderThickness, -y - borderThickness);
			// }

			graphics.translate(x, y);
			SubstanceImageCreator.paintSimpleBorder(c, graphics, width, height,
					borderColorScheme, borderColorScheme2, cyclePos);
			// SubstanceImageCreator.paintBorder(c, graphics, x, y, width,
			// height,
			// radius, borderColorScheme, borderColorScheme2, cyclePos);
		}

		graphics.dispose();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.border.Border#paintBorder(java.awt.Component,
	 * java.awt.Graphics, int, int, int, int)
	 */
	public void paintBorder(Component c, Graphics g, int x, int y, int width,
			int height) {
		paintBorder(c, g, x, y, width, height, c.isEnabled(), c.hasFocus(),
				this.alpha);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.border.Border#getBorderInsets(java.awt.Component)
	 */
	public Insets getBorderInsets(Component c) {
		if (this.myInsets == null) {
			return SubstanceSizeUtils.getDefaultBorderInsets(SubstanceSizeUtils
					.getComponentFontSize(c));
		}
		return this.myInsets;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.border.Border#isBorderOpaque()
	 */
	public boolean isBorderOpaque() {
		return false;
	}

	/**
	 * Returns the radius scale factor of this border.
	 * 
	 * @return The radius scale factor of this border.
	 */
	public float getRadiusScaleFactor() {
		return this.radiusScaleFactor;
	}
}
