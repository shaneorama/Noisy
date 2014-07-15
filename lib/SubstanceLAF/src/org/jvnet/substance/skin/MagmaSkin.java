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
import org.jvnet.substance.colorscheme.*;
import org.jvnet.substance.painter.border.GlassBorderPainter;
import org.jvnet.substance.painter.decoration.ArcDecorationPainter;
import org.jvnet.substance.painter.decoration.DecorationAreaType;
import org.jvnet.substance.painter.gradient.GlassGradientPainter;
import org.jvnet.substance.painter.highlight.ClassicHighlightPainter;
import org.jvnet.substance.shaper.ClassicButtonShaper;
import org.jvnet.substance.watermark.SubstancePlanktonWatermark;

/**
 * <code>Magma</code> skin. This class is part of officially supported API.
 * 
 * @author Kirill Grouchnikov
 * @since version 3.1
 */
public class MagmaSkin extends SubstanceSkin {
	/**
	 * Display name for <code>this</code> skin.
	 */
	public static final String NAME = "Magma";

	/**
	 * Creates a new <code>Magma</code> skin.
	 */
	public MagmaSkin() {
		SubstanceColorScheme shiftRed = new ShiftColorScheme(
				new SunsetColorScheme(), Color.red, 0.3);
		SubstanceColorScheme defaultScheme = new CharcoalColorScheme();
		SubstanceColorScheme activeScheme = shiftRed.saturate(0.4).named(
				"Magma Active");

		SubstanceColorScheme disabledScheme = new ShadeColorScheme(
				new CharcoalColorScheme(), 0.5) {
			Color foreColor = new Color(104, 93, 90);

			@Override
			public Color getForegroundColor() {
				return this.foreColor;
			}
		}.named("Magma Disabled");

		SubstanceColorSchemeBundle defaultSchemeBundle = new SubstanceColorSchemeBundle(
				activeScheme, defaultScheme, disabledScheme);
		defaultSchemeBundle.registerColorScheme(new CharcoalColorScheme(),
				ColorSchemeAssociationKind.BORDER, ComponentState
						.getActiveStates());
		defaultSchemeBundle.registerColorScheme(activeScheme,
				ColorSchemeAssociationKind.MARK, ComponentState
						.getActiveStates());
		defaultSchemeBundle.registerColorScheme(disabledScheme, 0.7f,
				ComponentState.DISABLED_UNSELECTED,
				ComponentState.DISABLED_SELECTED);

		defaultSchemeBundle.registerHighlightColorScheme(activeScheme
				.saturate(-0.2), 0.7f, ComponentState.ROLLOVER_UNSELECTED);
		defaultSchemeBundle.registerHighlightColorScheme(activeScheme, 0.7f,
				ComponentState.SELECTED);
		defaultSchemeBundle.registerHighlightColorScheme(activeScheme, 0.9f,
				ComponentState.ROLLOVER_SELECTED);
		defaultSchemeBundle.registerHighlightColorScheme(activeScheme
				.saturate(-0.2), 0.7f, ComponentState.ARMED,
				ComponentState.ROLLOVER_ARMED);

		this.registerDecorationAreaSchemeBundle(defaultSchemeBundle,
				DecorationAreaType.NONE);

		this.registerAsDecorationArea(defaultScheme,
				DecorationAreaType.PRIMARY_TITLE_PANE,
				DecorationAreaType.SECONDARY_TITLE_PANE,
				DecorationAreaType.HEADER, DecorationAreaType.FOOTER,
				DecorationAreaType.GENERAL, DecorationAreaType.TOOLBAR);

		this.setSelectedTabFadeStart(1.0);
		this.setSelectedTabFadeEnd(1.0);

		this.watermarkScheme = defaultScheme.shade(0.5);

		this.buttonShaper = new ClassicButtonShaper();
		this.gradientPainter = new GlassGradientPainter();
		this.decorationPainter = new ArcDecorationPainter();
		this.watermark = new SubstancePlanktonWatermark();

		this.borderPainter = new GlassBorderPainter();
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
