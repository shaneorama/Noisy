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

import java.awt.*;

import org.jvnet.substance.api.SubstanceColorScheme;
import org.jvnet.substance.api.trait.SubstanceTrait;

/**
 * Gradient painter interface for <b>Substance</b> look and feel. This class is
 * part of officially supported API.
 * 
 * @author Kirill Grouchnikov
 */
public interface SubstanceGradientPainter extends SubstanceTrait {
	/**
	 * Paints the background that matches the specified parameters.
	 * 
	 * @param g
	 *            Graphics context.
	 * @param comp
	 *            Component to paint.
	 * @param width
	 *            Width of a UI component.
	 * @param height
	 *            Height of a UI component.
	 * @param contour
	 *            Contour of a UI component.
	 * @param isFocused
	 *            Indication whether component owns the focus.
	 * @param colorScheme1
	 *            The first color scheme.
	 * @param colorScheme2
	 *            The second color scheme.
	 * @param cyclePos
	 *            Cycle position. Is used for rollover and pulsation effects.
	 *            Must be in 0..1 range.
	 * @param hasShine
	 *            Indication whether the returned image should have a 3D shine
	 *            spot in its top half.
	 * @param useCyclePosAsInterpolation
	 *            Indicates the algorithm to use for computing various colors.
	 *            If <code>true</code>, the <code>cyclePos</code> is used to
	 *            interpolate colors between different color components of both
	 *            color schemes. If <code>false</code>, the
	 *            <code>cyclePos</code> is used to interpolate colors between
	 *            different color components of the first color scheme.
	 */
	public void paintContourBackground(Graphics g, Component comp, int width,
			int height, Shape contour, boolean isFocused,
			SubstanceColorScheme colorScheme1,
			SubstanceColorScheme colorScheme2, float cyclePos,
			boolean hasShine, boolean useCyclePosAsInterpolation);
}
