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
import org.jvnet.substance.painter.decoration.DecorationAreaType;
import org.jvnet.substance.painter.gradient.MatteGradientPainter;
import org.jvnet.substance.utils.SubstanceColorSchemeUtilities;

/**
 * <code>Dust Coffee</code> skin. This class is part of officially supported
 * API.
 * 
 * @author Kirill Grouchnikov
 * @since version 5.2
 */
public class DustCoffeeSkin extends DustSkin {
	/**
	 * Display name for <code>this</code> skin.
	 */
	public static final String NAME = "Dust Coffee";

	/**
	 * Creates a new <code>Dust Coffee</code> skin.
	 */
	public DustCoffeeSkin() {
		SubstanceColorScheme activeScheme = SubstanceColorSchemeUtilities
				.getColorScheme(DustCoffeeSkin.class
						.getClassLoader()
						.getResource(
								"org/jvnet/substance/skin/coffee-active.colorscheme"));

		SubstanceSkin.ColorSchemes schemes = SubstanceSkin
				.getColorSchemes(DustCoffeeSkin.class.getClassLoader()
						.getResource(
								"org/jvnet/substance/skin/dust.colorschemes"));
		SubstanceColorScheme defaultScheme = schemes.get("Dust Coffee Default");

		SubstanceColorScheme watermarkScheme = schemes
				.get("Dust Coffee Watermark");

		SubstanceColorSchemeBundle defaultSchemeBundle = new SubstanceColorSchemeBundle(
				activeScheme, defaultScheme, defaultScheme);
		defaultSchemeBundle.registerColorScheme(defaultScheme, 0.5f,
				ComponentState.DISABLED_UNSELECTED);
		defaultSchemeBundle.registerColorScheme(activeScheme, 0.5f,
				ComponentState.DISABLED_SELECTED);

		// borders and marks
		SubstanceColorScheme borderDefaultScheme = schemes
				.get("Dust Border Default");
		defaultSchemeBundle.registerColorScheme(borderDefaultScheme,
				ColorSchemeAssociationKind.BORDER, ComponentState.DEFAULT,
				ComponentState.DISABLED_SELECTED,
				ComponentState.DISABLED_UNSELECTED);
		defaultSchemeBundle.registerColorScheme(activeScheme,
				ColorSchemeAssociationKind.BORDER, ComponentState
						.getActiveStates());
		defaultSchemeBundle.registerColorScheme(borderDefaultScheme,
				ColorSchemeAssociationKind.MARK);

		// text highlight
		SubstanceColorScheme textHighlightScheme = schemes
				.get("Dust Coffee Text Highlight");
		defaultSchemeBundle.registerColorScheme(textHighlightScheme,
				ColorSchemeAssociationKind.TEXT_HIGHLIGHT,
				ComponentState.SELECTED, ComponentState.ROLLOVER_SELECTED);

		// custom highlight alphas
		defaultSchemeBundle.registerHighlightColorScheme(activeScheme, 0.6f,
				ComponentState.ROLLOVER_UNSELECTED, ComponentState.ARMED);
		defaultSchemeBundle.registerHighlightColorScheme(activeScheme, 0.8f,
				ComponentState.SELECTED);
		defaultSchemeBundle
				.registerHighlightColorScheme(activeScheme, 1.0f,
						ComponentState.ROLLOVER_SELECTED,
						ComponentState.ROLLOVER_ARMED);

		this.registerDecorationAreaSchemeBundle(defaultSchemeBundle,
				watermarkScheme, DecorationAreaType.NONE);

		this.gradientPainter = new MatteGradientPainter();
	}

	@Override
	public String getDisplayName() {
		return NAME;
	}
}
