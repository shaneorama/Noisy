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
import org.jvnet.substance.api.painter.overlay.BottomLineOverlayPainter;
import org.jvnet.substance.api.painter.overlay.TopShadowOverlayPainter;
import org.jvnet.substance.painter.border.FlatBorderPainter;
import org.jvnet.substance.painter.decoration.*;
import org.jvnet.substance.painter.gradient.SubduedGradientPainter;
import org.jvnet.substance.painter.highlight.ClassicHighlightPainter;
import org.jvnet.substance.shaper.ClassicButtonShaper;

/**
 * <code>Nebula</code> skin. This class is part of officially supported API.
 * 
 * @author Kirill Grouchnikov
 * @since version 4.0
 */
public class NebulaSkin extends SubstanceSkin {
	/**
	 * Display name for <code>this</code> skin.
	 */
	public static final String NAME = "Nebula";

	/**
	 * Overlay painter to paint separator lines on some decoration areas.
	 */
	private BottomLineOverlayPainter bottomLineOverlayPainter;

	/**
	 * Creates a new <code>Nebula</code> skin.
	 */
	public NebulaSkin() {
		SubstanceSkin.ColorSchemes schemes = SubstanceSkin
				.getColorSchemes(NebulaSkin.class.getClassLoader().getResource(
						"org/jvnet/substance/skin/nebula.colorschemes"));

		SubstanceColorScheme activeScheme = schemes.get("Nebula Active");
		SubstanceColorScheme defaultScheme = schemes.get("Nebula Default");
		SubstanceColorScheme rolloverUnselectedScheme = schemes
				.get("Nebula Rollover Unselected");
		SubstanceColorScheme pressedScheme = schemes.get("Nebula Pressed");
		SubstanceColorScheme rolloverSelectedScheme = schemes
				.get("Nebula Rollover Selected");
		SubstanceColorScheme disabledScheme = schemes.get("Nebula Disabled");

		SubstanceColorSchemeBundle defaultSchemeBundle = new SubstanceColorSchemeBundle(
				activeScheme, defaultScheme, disabledScheme);
		defaultSchemeBundle.registerColorScheme(rolloverUnselectedScheme,
				ComponentState.ROLLOVER_UNSELECTED);
		defaultSchemeBundle.registerColorScheme(rolloverSelectedScheme,
				ComponentState.ROLLOVER_SELECTED);
		defaultSchemeBundle.registerColorScheme(pressedScheme,
				ComponentState.PRESSED_SELECTED,
				ComponentState.PRESSED_UNSELECTED, ComponentState.ARMED,
				ComponentState.ROLLOVER_ARMED);

		defaultSchemeBundle.registerHighlightColorScheme(pressedScheme, 0.6f,
				ComponentState.ROLLOVER_UNSELECTED);
		defaultSchemeBundle.registerHighlightColorScheme(pressedScheme, 0.8f,
				ComponentState.SELECTED);
		defaultSchemeBundle.registerHighlightColorScheme(pressedScheme, 0.95f,
				ComponentState.ROLLOVER_SELECTED);
		defaultSchemeBundle.registerHighlightColorScheme(pressedScheme, 0.8f,
				ComponentState.ARMED, ComponentState.ROLLOVER_ARMED);

		registerDecorationAreaSchemeBundle(defaultSchemeBundle,
				DecorationAreaType.NONE);

		this.registerAsDecorationArea(activeScheme.saturate(-0.5),
				DecorationAreaType.PRIMARY_TITLE_PANE,
				DecorationAreaType.SECONDARY_TITLE_PANE,
				DecorationAreaType.HEADER, DecorationAreaType.FOOTER,
				DecorationAreaType.GENERAL);

		// add an overlay painter to paint a drop shadow along the top
		// edge of toolbars
		this.addOverlayPainter(TopShadowOverlayPainter.getInstance(),
				DecorationAreaType.TOOLBAR);

		// add an overlay painter to paint separator lines along the bottom
		// edges of title panes and menu bars
		this.bottomLineOverlayPainter = new BottomLineOverlayPainter(
				new ColorSchemeSingleColorQuery() {
					@Override
					public Color query(SubstanceColorScheme scheme) {
						Color dark = scheme.getDarkColor();
						return new Color(dark.getRed(), dark.getGreen(), dark
								.getBlue(), 160);
					}
				});
		this.addOverlayPainter(this.bottomLineOverlayPainter,
				DecorationAreaType.PRIMARY_TITLE_PANE,
				DecorationAreaType.SECONDARY_TITLE_PANE,
				DecorationAreaType.HEADER);

		this.buttonShaper = new ClassicButtonShaper();
		this.gradientPainter = new SubduedGradientPainter();

		MarbleNoiseDecorationPainter decorationPainter = new MarbleNoiseDecorationPainter();
		decorationPainter.setBaseDecorationPainter(new ArcDecorationPainter());
		decorationPainter.setTextureAlpha(0.3f);
		this.decorationPainter = decorationPainter;

		this.highlightPainter = new ClassicHighlightPainter();
		this.borderPainter = new FlatBorderPainter();
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
