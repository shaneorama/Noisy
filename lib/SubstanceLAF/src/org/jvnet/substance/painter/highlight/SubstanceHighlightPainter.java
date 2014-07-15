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
package org.jvnet.substance.painter.highlight;

import java.awt.Component;
import java.awt.Graphics2D;
import java.util.Set;

import org.jvnet.substance.api.SubstanceColorScheme;
import org.jvnet.substance.api.SubstanceConstants.Side;
import org.jvnet.substance.api.trait.SubstanceTrait;

/**
 * Highlight painter interface for <b>Substance</b> look and feel. This class is
 * part of officially supported API.<br>
 * <br>
 * 
 * @author Kirill Grouchnikov
 * @since version 4.3
 */
public interface SubstanceHighlightPainter extends SubstanceTrait {
	/**
	 * Paints the highlight.
	 * 
	 * @param graphics
	 *            Graphics context.
	 * @param comp
	 *            Component.
	 * @param width
	 *            Width.
	 * @param height
	 *            Height.
	 * @param borderAlpha
	 *            Border alpha factor.
	 * @param openSides
	 *            The sides specified in this set will not be painted. Can be
	 *            <code>null</code> or empty.
	 * @param colorScheme1
	 *            The first color scheme.
	 * @param colorScheme2
	 *            The second color scheme.
	 * @param borderColorScheme1
	 *            The first border color scheme.
	 * @param borderColorScheme2
	 *            The second border color scheme.
	 * @param cyclePos
	 *            Cycle position. Is used for rollover and selection animations.
	 *            Must be in 0..1 range.
	 */
	public void paintHighlight(Graphics2D graphics, Component comp, int width,
			int height, Set<Side> openSides, SubstanceColorScheme colorScheme1,
			SubstanceColorScheme colorScheme2, float cyclePos);
}
