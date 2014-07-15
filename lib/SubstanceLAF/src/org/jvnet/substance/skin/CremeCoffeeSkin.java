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
package org.jvnet.substance.skin;

import org.jvnet.substance.api.*;
import org.jvnet.substance.colorscheme.CremeColorScheme;
import org.jvnet.substance.colorscheme.LightGrayColorScheme;
import org.jvnet.substance.painter.border.*;
import org.jvnet.substance.painter.decoration.ArcDecorationPainter;
import org.jvnet.substance.painter.decoration.DecorationAreaType;
import org.jvnet.substance.painter.gradient.MatteGradientPainter;
import org.jvnet.substance.painter.highlight.ClassicHighlightPainter;
import org.jvnet.substance.shaper.ClassicButtonShaper;
import org.jvnet.substance.utils.SubstanceColorSchemeUtilities;

/**
 * <code>Creme Coffee</code> skin. This class is part of officially supported
 * API.
 * 
 * @author Kirill Grouchnikov
 * @since version 4.1
 */
public class CremeCoffeeSkin extends SubstanceSkin {
	/**
	 * Display name for <code>this</code> skin.
	 */
	public static final String NAME = "Creme Coffee";

	/**
	 * Creates a new <code>Creme Coffee</code> skin.
	 */
	public CremeCoffeeSkin() {
		SubstanceColorScheme activeScheme = SubstanceColorSchemeUtilities
				.getColorScheme(CremeCoffeeSkin.class
						.getClassLoader()
						.getResource(
								"org/jvnet/substance/skin/coffee-active.colorscheme"));
		SubstanceColorScheme defaultScheme = new CremeColorScheme();
		SubstanceColorScheme disabledScheme = new LightGrayColorScheme().tint(
				0.35).named("Creme Coffee Disabled");

		SubstanceColorSchemeBundle defaultSchemeBundle = new SubstanceColorSchemeBundle(
				activeScheme, defaultScheme, disabledScheme);
		this.registerDecorationAreaSchemeBundle(defaultSchemeBundle,
				DecorationAreaType.NONE);

		this.registerAsDecorationArea(defaultScheme,
				DecorationAreaType.PRIMARY_TITLE_PANE,
				DecorationAreaType.SECONDARY_TITLE_PANE,
				DecorationAreaType.HEADER, DecorationAreaType.FOOTER,
				DecorationAreaType.GENERAL, DecorationAreaType.TOOLBAR);

		this.buttonShaper = new ClassicButtonShaper();
		this.gradientPainter = new MatteGradientPainter();
		this.decorationPainter = new ArcDecorationPainter();
		this.highlightPainter = new ClassicHighlightPainter();
		this.borderPainter = new CompositeBorderPainter("Creme Coffee",
				new GlassBorderPainter(), new DelegateBorderPainter(
						"Creme Coffee Inner", new GlassBorderPainter(),
						new ColorSchemeTransform() {
							@Override
							public SubstanceColorScheme transform(
									SubstanceColorScheme scheme) {
								return scheme.tint(0.8f);
							}
						}));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jvnet.substance.skin.SubstanceSkin#getDisplayName()
	 */
	public String getDisplayName() {
		return NAME;
	}
}