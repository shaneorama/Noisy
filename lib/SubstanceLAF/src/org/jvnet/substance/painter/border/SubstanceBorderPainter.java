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
import org.jvnet.substance.api.trait.SubstanceTrait;
import org.jvnet.substance.painter.gradient.SubstanceGradientPainter;

/**
 * Border painter interface for <b>Substance</b> look and feel. This class is
 * part of officially supported API.<br>
 * <br>
 * 
 * Starting from version 4.0, the borders of some controls (buttons, check
 * boxes, tabs, scroll bars etc) are painted by border painters. Up until
 * version 4.0 this has been done by gradient painters (
 * {@link SubstanceGradientPainter}) instead. Note that a custom gradient
 * painter may continue painting the borders, but these will be overriden by the
 * current border painter. To allow easier migration, use
 * {@link NullBorderPainter}.
 * 
 * @author Kirill Grouchnikov
 * @since version 4.0
 */
public interface SubstanceBorderPainter extends SubstanceTrait {
	/**
	 * Paints the control border.
	 * 
	 * @param g
	 *            Graphics.
	 * @param c
	 *            Component.
	 * @param width
	 *            Width of a UI component.
	 * @param height
	 *            Height of a UI component.
	 * @param contour
	 *            Contour of a UI component.
	 * @param innerContour
	 *            Inner contour of a UI component. May be ignored if the
	 *            specific implementation paints only the outside border.
	 * @param colorScheme1
	 *            The first color scheme.
	 * @param colorScheme2
	 *            The second color scheme.
	 * @param cyclePos
	 *            Cycle position. Is used for rollover and pulsation effects.
	 *            Must be in 0..1 range.
	 * @param useCyclePosAsInterpolation
	 *            Indicates the algorithm to use for computing various colors.
	 *            If <code>true</code>, the <code>cyclePos</code> is used to
	 *            interpolate colors between different color components of both
	 *            color schemes. If <code>false</code>, the
	 *            <code>cyclePos</code> is used to interpolate colors between
	 *            different color components of the first color scheme.
	 */
	public void paintBorder(Graphics g, Component c, int width, int height,
			Shape contour, Shape innerContour,
			SubstanceColorScheme colorScheme1,
			SubstanceColorScheme colorScheme2, float cyclePos,
			boolean useCyclePosAsInterpolation);

	/**
	 * Returns boolean indication whether this border painter is painting the
	 * inner contours.
	 * 
	 * @return <code>true</code> if this border painter is painting the inner
	 *         contours, <code>false</code> otherwise.
	 */
	public boolean isPaintingInnerContour();
}
