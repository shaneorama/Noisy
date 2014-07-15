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
package org.jvnet.substance.painter.gradient;

import java.awt.Color;

import org.jvnet.substance.api.SubstanceColorScheme;
import org.jvnet.substance.utils.SubstanceColorUtilities;

/**
 * Gradient painter that returns images with flat appearance. This class is
 * <b>for internal use only</b>.
 * 
 * @author Kirill Grouchnikov
 */
public class FlatGradientPainter extends StandardGradientPainter {
	/**
	 * Creates a new flat gradient painter.
	 */
	public FlatGradientPainter() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jvnet.substance.painter.StandardGradientPainter#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		return "Flat";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jvnet.substance.painter.StandardGradientPainter#getMidFillColorTop
	 * (org.jvnet.substance.color.ColorScheme,
	 * org.jvnet.substance.color.ColorScheme, double, boolean)
	 */
	@Override
	public Color getMidFillColorTop(SubstanceColorScheme interpolationScheme1,
			SubstanceColorScheme interpolationScheme2, double cycleCoef,
			boolean useCyclePosAsInterpolation) {
		if (!useCyclePosAsInterpolation) {
			return SubstanceColorUtilities.getInterpolatedColor(
					interpolationScheme1.getDarkColor(), interpolationScheme2
							.getMidColor(), cycleCoef);
		} else {
			return SubstanceColorUtilities.getInterpolatedColor(
					interpolationScheme1.getDarkColor(), interpolationScheme2
							.getDarkColor(), cycleCoef);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jvnet.substance.painter.StandardGradientPainter#getTopFillColor(org
	 * .jvnet.substance.color.ColorScheme,
	 * org.jvnet.substance.color.ColorScheme, double, boolean)
	 */
	@Override
	public Color getTopFillColor(SubstanceColorScheme interpolationScheme1,
			SubstanceColorScheme interpolationScheme2, double cycleCoef,
			boolean useCyclePosAsInterpolation) {
		return this.getMidFillColorTop(interpolationScheme1,
				interpolationScheme2, cycleCoef, useCyclePosAsInterpolation);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jvnet.substance.painter.StandardGradientPainter#getMidFillColorBottom
	 * (org.jvnet.substance.color.ColorScheme,
	 * org.jvnet.substance.color.ColorScheme, double, boolean)
	 */
	@Override
	public Color getMidFillColorBottom(
			SubstanceColorScheme interpolationScheme1,
			SubstanceColorScheme interpolationScheme2, double cycleCoef,
			boolean useCyclePosAsInterpolation) {
		return this.getMidFillColorTop(interpolationScheme1,
				interpolationScheme2, cycleCoef, useCyclePosAsInterpolation);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jvnet.substance.painter.StandardGradientPainter#getBottomFillColor
	 * (org.jvnet.substance.color.ColorScheme,
	 * org.jvnet.substance.color.ColorScheme, double, boolean)
	 */
	@Override
	public Color getBottomFillColor(SubstanceColorScheme interpolationScheme1,
			SubstanceColorScheme interpolationScheme2, double cycleCoef,
			boolean useCyclePosAsInterpolation) {
		return this.getMidFillColorTop(interpolationScheme1,
				interpolationScheme2, cycleCoef, useCyclePosAsInterpolation);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jvnet.substance.painter.StandardGradientPainter#getTopShineColor(
	 * org.jvnet.substance.color.ColorScheme,
	 * org.jvnet.substance.color.ColorScheme, double, boolean)
	 */
	@Override
	public Color getTopShineColor(SubstanceColorScheme interpolationScheme1,
			SubstanceColorScheme interpolationScheme2, double cycleCoef,
			boolean useCyclePosAsInterpolation) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jvnet.substance.painter.StandardGradientPainter#getBottomShineColor
	 * (org.jvnet.substance.color.ColorScheme,
	 * org.jvnet.substance.color.ColorScheme, double, boolean)
	 */
	@Override
	public Color getBottomShineColor(SubstanceColorScheme interpolationScheme1,
			SubstanceColorScheme interpolationScheme2, double cycleCoef,
			boolean useCyclePosAsInterpolation) {
		return null;
	}

	// @Override
	// public Color getMidBorderColor(ColorScheme interpolationScheme1,
	// ColorScheme interpolationScheme2, double cycleCoef,
	// boolean useCyclePosAsInterpolation) {
	// return super.getTopBorderColor(interpolationScheme1,
	// interpolationScheme2, cycleCoef, useCyclePosAsInterpolation);
	// }
	//
	// @Override
	// public Color getBottomBorderColor(ColorScheme interpolationScheme1,
	// ColorScheme interpolationScheme2, double cycleCoef,
	// boolean useCyclePosAsInterpolation) {
	// return super.getTopBorderColor(interpolationScheme1,
	// interpolationScheme2, cycleCoef, useCyclePosAsInterpolation);
	// }
	//
}
