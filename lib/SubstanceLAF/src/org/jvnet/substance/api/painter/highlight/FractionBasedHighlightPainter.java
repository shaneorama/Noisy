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
package org.jvnet.substance.api.painter.highlight;

import java.awt.*;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.util.Set;

import org.jvnet.substance.api.ColorSchemeSingleColorQuery;
import org.jvnet.substance.api.SubstanceColorScheme;
import org.jvnet.substance.api.SubstanceConstants.Side;
import org.jvnet.substance.api.painter.FractionBasedPainter;
import org.jvnet.substance.painter.highlight.SubstanceHighlightPainter;
import org.jvnet.substance.utils.SubstanceColorUtilities;

/**
 * Highlight painter with fraction-based stops and a color query associated with
 * each stop. This class allows creating multi-gradient highlights with exact
 * control over which color is used at every gradient control point.
 * 
 * @author Kirill Grouchnikov
 */
public class FractionBasedHighlightPainter extends FractionBasedPainter
		implements SubstanceHighlightPainter {
	/**
	 * Creates a new fraction-based highlight painter.
	 * 
	 * @param displayName
	 *            The display name of this painter.
	 * @param fractions
	 *            The fractions of this painter. Must be strictly increasing,
	 *            starting from 0.0 and ending at 1.0.
	 * @param colorQueries
	 *            The color queries of this painter. Must have the same size as
	 *            the fractions array, and all entries must be non-
	 *            <code>null</code>.
	 */
	public FractionBasedHighlightPainter(String displayName, float[] fractions,
			ColorSchemeSingleColorQuery[] colorQueries) {
		super(displayName, fractions, colorQueries);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jvnet.substance.painter.highlight.SubstanceHighlightPainter#
	 * paintHighlight(java.awt.Graphics2D, java.awt.Component, int, int,
	 * java.util.Set, org.jvnet.substance.api.SubstanceColorScheme,
	 * org.jvnet.substance.api.SubstanceColorScheme, float)
	 */
	@Override
	public void paintHighlight(Graphics2D graphics, Component comp, int width,
			int height, Set<Side> openSides, SubstanceColorScheme colorScheme1,
			SubstanceColorScheme colorScheme2, float cyclePos) {
		Graphics2D g2d = (Graphics2D) graphics.create();

		double cycleCoef = 1.0 - cyclePos;

		Color[] fillColors = new Color[this.fractions.length];
		for (int i = 0; i < this.fractions.length; i++) {
			ColorSchemeSingleColorQuery colorQuery = this.colorQueries[i];
			Color color1 = colorQuery.query(colorScheme1);
			Color color2 = colorQuery.query(colorScheme2);
			fillColors[i] = SubstanceColorUtilities.getInterpolatedColor(
					color1, color2, cycleCoef);
		}

		MultipleGradientPaint gradient = new LinearGradientPaint(0, 0, 0,
				height, this.fractions, fillColors, CycleMethod.REPEAT);
		g2d.setPaint(gradient);
		g2d.fillRect(0, 0, width, height);
		g2d.dispose();
	}
}
