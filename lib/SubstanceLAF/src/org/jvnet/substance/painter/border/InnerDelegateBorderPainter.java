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
package org.jvnet.substance.painter.border;

import java.awt.*;

import org.jvnet.substance.api.SubstanceColorScheme;
import org.jvnet.substance.api.SubstanceConstants.ColorShiftKind;
import org.jvnet.substance.colorscheme.*;
import org.jvnet.substance.utils.*;

/**
 * Abstract base class for painters that provide inner painting. The
 * implementation is based on three main parts:
 * <ul>
 * <li>The delegate border painter that paints the outer and inner parts of the
 * border.</li>
 * <li>Shift kind that specifies how to compute the color scheme of the inner
 * part based on the color scheme of the border.</li>
 * <li>Shift coefficient that specifies how different is the color scheme of the
 * inner part from the color scheme of the border.</li>
 * </ul>
 * 
 * @author Kirill Grouchnikov
 * @deprecated Will be removed in version 6.0. Use
 *             {@link CompositeBorderPainter} and {@link DelegateBorderPainter}
 *             instead.
 */
@Deprecated
public abstract class InnerDelegateBorderPainter implements
		SubstanceBorderPainter {
	/**
	 * Display name of <code>this</code> painter.
	 */
	protected String painterName;

	/**
	 * Mandatory delegate painter.
	 */
	protected SubstanceBorderPainter delegate;

	/**
	 * Shift coefficient. Must be in 0.0-1.0 range.
	 */
	protected float shiftCoef;

	/**
	 * Color shift kind.
	 */
	protected ColorShiftKind shiftKind;

	/**
	 * Map of shifted color schemes (to speed up the subsequent lookups).
	 */
	protected final static LazyResettableHashMap<SubstanceColorScheme> shiftMap = new LazyResettableHashMap<SubstanceColorScheme>(
			"InnerDelegateBorderPainter");

	/**
	 * Creates an inner painter.
	 * 
	 * @param painterName
	 *            Painter display name.
	 * @param delegate
	 *            Delegate painter.
	 */
	public InnerDelegateBorderPainter(String painterName,
			SubstanceBorderPainter delegate) {
		this(painterName, delegate, 0.7f, ColorShiftKind.TINT);
	}

	/**
	 * Creates an inner painter.
	 * 
	 * @param painterName
	 *            Painter display name.
	 * @param delegate
	 *            Delegate painter.
	 * @param shiftCoef
	 *            Shift coefficient. Must be in 0.0-1.0 range.
	 * @param shiftKind
	 *            Color shift kind.
	 */
	public InnerDelegateBorderPainter(String painterName,
			SubstanceBorderPainter delegate, float shiftCoef,
			ColorShiftKind shiftKind) {
		this.painterName = painterName;
		this.delegate = delegate;
		this.shiftCoef = shiftCoef;
		this.shiftKind = shiftKind;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jvnet.substance.border.SubstanceBorderPainter#getDisplayName()
	 */
	public String getDisplayName() {
		return this.painterName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jvnet.substance.border.SubstanceBorderPainter#paintBorder(java.awt
	 * .Graphics, java.awt.Component, int, int, java.awt.Shape, java.awt.Shape,
	 * org.jvnet.substance.color.ColorScheme,
	 * org.jvnet.substance.color.ColorScheme, float, boolean)
	 */
	public void paintBorder(Graphics g, Component comp, int width, int height,
			Shape contour, Shape innerContour,
			SubstanceColorScheme colorScheme1,
			SubstanceColorScheme colorScheme2, float cyclePos,
			boolean useCyclePosAsInterpolation) {
		if (innerContour != null) {
			this.delegate.paintBorder(g, comp, width, height, innerContour,
					null, getShiftScheme(colorScheme1),
					getShiftScheme(colorScheme2), cyclePos,
					useCyclePosAsInterpolation);
		}
		if (contour != null) {
			this.delegate.paintBorder(g, comp, width, height, contour, null,
					colorScheme1, colorScheme2, cyclePos,
					useCyclePosAsInterpolation);
		}
	}

	/**
	 * Retrieves a shifted color scheme.
	 * 
	 * @param orig
	 *            Original color scheme.
	 * @return Shifted color scheme.
	 */
	public SubstanceColorScheme getShiftScheme(SubstanceColorScheme orig) {
		HashMapKey key = SubstanceCoreUtilities.getHashKey(orig
				.getDisplayName(), this.shiftCoef, this.shiftKind.name());
		if (!shiftMap.containsKey(key)) {
			switch (shiftKind) {
			case TINT:
				shiftMap.put(key, new TintColorScheme(orig, this.shiftCoef));
				break;
			case TONE:
				shiftMap.put(key, new ToneColorScheme(orig, this.shiftCoef));
				break;
			case SHADE:
				shiftMap.put(key, new ShadeColorScheme(orig, this.shiftCoef));
				break;
			case THEME_LIGHT:
				shiftMap.put(key, new ShiftColorScheme(orig, orig
						.getUltraLightColor().brighter(), this.shiftCoef));
				break;
			}
		}
		return shiftMap.get(key);
	}

	/**
	 * Returns the painting delegate.
	 * 
	 * @return Painting delegate.
	 */
	public SubstanceBorderPainter getDelegate() {
		return this.delegate;
	}

	@Override
	public boolean isPaintingInnerContour() {
		return true;
	}
}
