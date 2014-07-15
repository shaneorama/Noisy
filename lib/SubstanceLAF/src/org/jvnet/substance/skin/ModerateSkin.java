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
import org.jvnet.substance.colorscheme.*;
import org.jvnet.substance.painter.border.ClassicBorderPainter;
import org.jvnet.substance.painter.decoration.ClassicDecorationPainter;
import org.jvnet.substance.painter.decoration.DecorationAreaType;
import org.jvnet.substance.painter.gradient.GlassGradientPainter;
import org.jvnet.substance.painter.highlight.ClassicHighlightPainter;
import org.jvnet.substance.shaper.ClassicButtonShaper;
import org.jvnet.substance.utils.SubstanceColorSchemeUtilities;

/**
 * <code>Moderate</code> skin. This class is part of officially supported API.
 * 
 * @author Kirill Grouchnikov
 * @since version 3.1
 */
public class ModerateSkin extends SubstanceSkin {
	/**
	 * Display name for <code>this</code> skin.
	 */
	public static final String NAME = "Moderate";

	/**
	 * Creates a new <code>Moderate</code> skin.
	 */
	public ModerateSkin() {
		SubstanceColorScheme activeScheme = new SteelBlueColorScheme();
		SubstanceColorScheme defaultScheme = new MetallicColorScheme();
		SubstanceColorScheme disabledScheme = new LightGrayColorScheme();

		SubstanceColorSchemeBundle defaultSchemeBundle = new SubstanceColorSchemeBundle(
				activeScheme, defaultScheme, disabledScheme);
		defaultSchemeBundle
				.registerHighlightColorScheme(new BlendBiColorScheme(
						new TerracottaColorScheme(), new SunGlareColorScheme(),
						0.5));
		this.registerDecorationAreaSchemeBundle(defaultSchemeBundle,
				DecorationAreaType.NONE);

		this.registerAsDecorationArea(activeScheme,
				DecorationAreaType.PRIMARY_TITLE_PANE,
				DecorationAreaType.SECONDARY_TITLE_PANE);

		SubstanceColorScheme generalBgScheme = SubstanceColorSchemeUtilities
				.getColorScheme(ModerateSkin.class
						.getClassLoader()
						.getResource(
								"org/jvnet/substance/skin/lightgray-general-watermark.colorscheme"));
		this.registerAsDecorationArea(generalBgScheme,
				DecorationAreaType.GENERAL, DecorationAreaType.HEADER);

		this.buttonShaper = new ClassicButtonShaper();
		this.gradientPainter = new GlassGradientPainter();
		this.decorationPainter = new ClassicDecorationPainter();
		this.borderPainter = new ClassicBorderPainter();
		this.highlightPainter = new ClassicHighlightPainter();
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
