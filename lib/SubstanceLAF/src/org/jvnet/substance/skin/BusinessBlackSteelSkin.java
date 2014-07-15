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

import java.awt.Color;

import org.jvnet.substance.api.*;
import org.jvnet.substance.api.painter.overlay.TopShadowOverlayPainter;
import org.jvnet.substance.colorscheme.*;
import org.jvnet.substance.painter.border.ClassicBorderPainter;
import org.jvnet.substance.painter.decoration.*;
import org.jvnet.substance.painter.gradient.ClassicGradientPainter;
import org.jvnet.substance.painter.highlight.ClassicHighlightPainter;
import org.jvnet.substance.shaper.ClassicButtonShaper;

/**
 * <code>Business Black Steel</code> skin. This class is part of officially
 * supported API.
 * 
 * @author Kirill Grouchnikov
 * @since version 3.3
 */
public class BusinessBlackSteelSkin extends SubstanceSkin {
	/**
	 * Display name for <code>this</code> skin.
	 */
	public static final String NAME = "Business Black Steel";

	/**
	 * Creates a new <code>Business</code> skin.
	 */
	public BusinessBlackSteelSkin() {
		SubstanceColorScheme activeScheme = new SteelBlueColorScheme().tint(
				0.15).named("Business Black Steel Active");
		SubstanceColorScheme defaultScheme = new MetallicColorScheme().tint(
				0.05).named("Business Black Steel Default");
		SubstanceColorScheme disabledScheme = new LightGrayColorScheme().tint(
				0.05).named("Business Black Steel Disabled");

		// the default color scheme bundle
		SubstanceColorSchemeBundle defaultSchemeBundle = new SubstanceColorSchemeBundle(
				activeScheme, defaultScheme, disabledScheme);
		defaultSchemeBundle.registerHighlightColorScheme(activeScheme, 0.6f,
				ComponentState.ROLLOVER_UNSELECTED);
		defaultSchemeBundle.registerHighlightColorScheme(activeScheme, 0.8f,
				ComponentState.SELECTED);
		defaultSchemeBundle.registerHighlightColorScheme(activeScheme, 0.95f,
				ComponentState.ROLLOVER_SELECTED);
		defaultSchemeBundle.registerHighlightColorScheme(activeScheme, 0.8f,
				ComponentState.ARMED, ComponentState.ROLLOVER_ARMED);
		this.registerDecorationAreaSchemeBundle(defaultSchemeBundle,
				DecorationAreaType.NONE);

		// color scheme bundle for title panes
		SubstanceColorScheme activeHeaderScheme = new EbonyColorScheme()
				.shiftBackground(Color.black, 0.3).tint(0.05).named(
						"Business Black Steel Active Header");
		SubstanceColorScheme defaultHeaderScheme = new EbonyColorScheme().tint(
				0.05).named("Business Black Steel Default Header");
		SubstanceColorSchemeBundle headerSchemeBundle = new SubstanceColorSchemeBundle(
				activeHeaderScheme, defaultHeaderScheme, disabledScheme);
		headerSchemeBundle.registerColorScheme(defaultHeaderScheme, 0.5f,
				ComponentState.DISABLED_UNSELECTED,
				ComponentState.DISABLED_SELECTED);
		headerSchemeBundle.registerHighlightColorScheme(activeScheme, 0.6f,
				ComponentState.ROLLOVER_UNSELECTED);
		headerSchemeBundle.registerHighlightColorScheme(activeScheme, 0.8f,
				ComponentState.SELECTED);
		headerSchemeBundle.registerHighlightColorScheme(activeScheme, 0.95f,
				ComponentState.ROLLOVER_SELECTED);
		headerSchemeBundle.registerHighlightColorScheme(activeScheme, 0.8f,
				ComponentState.ARMED, ComponentState.ROLLOVER_ARMED);

		this.registerDecorationAreaSchemeBundle(headerSchemeBundle,
				activeHeaderScheme, DecorationAreaType.PRIMARY_TITLE_PANE,
				DecorationAreaType.SECONDARY_TITLE_PANE,
				DecorationAreaType.HEADER);

		// color scheme bundle for general areas
		SubstanceColorScheme activeGeneralScheme = activeScheme.shade(0.1)
				.saturate(-0.5).named("Business Black Steel Active General");
		SubstanceColorScheme defaultGeneralScheme = activeScheme.tint(0.3)
				.saturate(-0.7).named("Business Black Steel Default General");
		SubstanceColorSchemeBundle generalSchemeBundle = new SubstanceColorSchemeBundle(
				activeGeneralScheme, defaultGeneralScheme, disabledScheme);
		generalSchemeBundle.registerColorScheme(disabledScheme, 0.5f,
				ComponentState.DISABLED_UNSELECTED);
		this.registerDecorationAreaSchemeBundle(generalSchemeBundle,
				DecorationAreaType.FOOTER, DecorationAreaType.GENERAL);

		// add an overlay painter to paint a drop shadow along the top
		// edge of toolbars
		this.addOverlayPainter(TopShadowOverlayPainter.getInstance(),
				DecorationAreaType.TOOLBAR);

		this.buttonShaper = new ClassicButtonShaper();
		this.gradientPainter = new ClassicGradientPainter();
		this.borderPainter = new ClassicBorderPainter();

		BrushedMetalDecorationPainter decorationPainter = new BrushedMetalDecorationPainter();
		decorationPainter.setBaseDecorationPainter(new ArcDecorationPainter());
		decorationPainter.setTextureAlpha(0.02f);
		this.decorationPainter = decorationPainter;

		this.highlightPainter = new ClassicHighlightPainter();

		this.borderPainter = new ClassicBorderPainter();
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
