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

import java.awt.Color;

import org.jvnet.substance.api.SubstanceColorScheme;

/**
 * Border painter that returns images with classic appearance. This class is
 * <b>for internal use only</b>.
 * 
 * @author Kirill Grouchnikov
 */
public class SimplisticSoftBorderPainter extends StandardBorderPainter {
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jvnet.substance.painter.SubstanceGradientPainter#getDisplayName()
	 */
	public String getDisplayName() {
		return "Simplistic Soft Border";
	}

	@Override
	public Color getTopBorderColor(SubstanceColorScheme interpolationScheme1,
			SubstanceColorScheme interpolationScheme2, double cycleCoef,
			boolean useCyclePosAsInterpolation) {
		return super.getMidBorderColor(interpolationScheme1,
				interpolationScheme2, cycleCoef, useCyclePosAsInterpolation);
	}

	@Override
	public Color getMidBorderColor(SubstanceColorScheme interpolationScheme1,
			SubstanceColorScheme interpolationScheme2, double cycleCoef,
			boolean useCyclePosAsInterpolation) {
		return super.getBottomBorderColor(interpolationScheme1,
				interpolationScheme2, cycleCoef, useCyclePosAsInterpolation);
	}

	@Override
	public Color getBottomBorderColor(
			SubstanceColorScheme interpolationScheme1,
			SubstanceColorScheme interpolationScheme2, double cycleCoef,
			boolean useCyclePosAsInterpolation) {
		return super.getBottomBorderColor(interpolationScheme1,
				interpolationScheme2, cycleCoef, useCyclePosAsInterpolation);
	}
}
