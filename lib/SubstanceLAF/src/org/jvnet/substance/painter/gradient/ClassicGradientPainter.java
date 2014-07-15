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
 * Gradient painter that returns images with classic appearance. This class is
 * part of officially supported API.
 * 
 * @author Kirill Grouchnikov
 */
public class ClassicGradientPainter extends StandardGradientPainter {
	/**
	 * Reusable instance of this painter.
	 */
	public static final ClassicGradientPainter INSTANCE = new ClassicGradientPainter();

	/**
	 * Creates a new classic gradient painter.
	 */
	public ClassicGradientPainter() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jvnet.substance.painter.SubstanceGradientPainter#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		return "Classic";
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
		return SubstanceColorUtilities.getInterpolatedColor(super
				.getBottomFillColor(interpolationScheme1, interpolationScheme2,
						cycleCoef, useCyclePosAsInterpolation), super
				.getMidFillColorTop(interpolationScheme1, interpolationScheme2,
						cycleCoef, useCyclePosAsInterpolation), 0.5);
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
		return SubstanceColorUtilities.getInterpolatedColor(super
				.getMidFillColorTop(interpolationScheme1, interpolationScheme2,
						cycleCoef, useCyclePosAsInterpolation), super
				.getBottomFillColor(interpolationScheme1, interpolationScheme2,
						cycleCoef, useCyclePosAsInterpolation), 0.7);
	}
}
