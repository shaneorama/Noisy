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
import org.jvnet.substance.api.painter.overlay.TopLineOverlayPainter;
import org.jvnet.substance.painter.border.*;
import org.jvnet.substance.painter.decoration.DecorationAreaType;
import org.jvnet.substance.painter.decoration.MatteDecorationPainter;
import org.jvnet.substance.painter.gradient.StandardGradientPainter;
import org.jvnet.substance.painter.highlight.ClassicHighlightPainter;
import org.jvnet.substance.shaper.ClassicButtonShaper;

/**
 * <code>Dust</code> skin. This class is part of officially supported API.
 * 
 * @author Kirill Grouchnikov
 * @since version 5.2
 */
public class DustSkin extends SubstanceSkin {
	/**
	 * Display name for <code>this</code> skin.
	 */
	public static final String NAME = "Dust";

	/**
	 * Overlay painter to paint a dark line along the bottom edge of the
	 * menubar.
	 */
	private BottomLineOverlayPainter menuOverlayPainter;

	/**
	 * Overlay painter to paint a light line along the top edge of the toolbars.
	 */
	private TopLineOverlayPainter toolbarOverlayPainter;

	/**
	 * Creates a new <code>Dust</code> skin.
	 */
	public DustSkin() {
		SubstanceSkin.ColorSchemes schemes = SubstanceSkin
				.getColorSchemes(DustSkin.class.getClassLoader().getResource(
						"org/jvnet/substance/skin/dust.colorschemes"));
		SubstanceColorScheme activeScheme = schemes.get("Dust Active");
		SubstanceColorScheme defaultScheme = schemes.get("Dust Default");

		SubstanceColorSchemeBundle defaultSchemeBundle = new SubstanceColorSchemeBundle(
				activeScheme, defaultScheme, defaultScheme);
		defaultSchemeBundle.registerColorScheme(defaultScheme, 0.5f,
				ComponentState.DISABLED_UNSELECTED);
		defaultSchemeBundle.registerColorScheme(activeScheme, 0.5f,
				ComponentState.DISABLED_SELECTED);

		// borders
		SubstanceColorScheme borderDefaultScheme = schemes
				.get("Dust Border Default");
		SubstanceColorScheme borderActiveScheme = schemes
				.get("Dust Border Active");

		defaultSchemeBundle.registerColorScheme(borderDefaultScheme,
				ColorSchemeAssociationKind.BORDER, ComponentState.DEFAULT,
				ComponentState.DISABLED_SELECTED,
				ComponentState.DISABLED_UNSELECTED);
		defaultSchemeBundle.registerColorScheme(borderActiveScheme,
				ColorSchemeAssociationKind.BORDER, ComponentState
						.getActiveStates());
		defaultSchemeBundle.registerColorScheme(borderDefaultScheme,
				ColorSchemeAssociationKind.MARK);

		this.registerDecorationAreaSchemeBundle(defaultSchemeBundle,
				DecorationAreaType.NONE);

		// header color scheme bundle
		SubstanceColorScheme headerActiveScheme = schemes
				.get("Dust Header Active");
		SubstanceColorScheme headerDefaultScheme = schemes
				.get("Dust Header Default");

		SubstanceColorScheme headerWatermarkScheme = schemes
				.get("Dust Header Watermark");

		SubstanceColorScheme headerSeparatorScheme = schemes
				.get("Dust Header Separator");

		SubstanceColorScheme headerBorderScheme = schemes
				.get("Dust Header Border");

		SubstanceColorSchemeBundle headerSchemeBundle = new SubstanceColorSchemeBundle(
				headerActiveScheme, headerDefaultScheme, headerDefaultScheme);
		headerSchemeBundle.registerColorScheme(headerDefaultScheme, 0.7f,
				ComponentState.DISABLED_UNSELECTED);
		headerSchemeBundle.registerColorScheme(headerActiveScheme, 0.7f,
				ComponentState.DISABLED_SELECTED);

		headerSchemeBundle.registerColorScheme(headerBorderScheme,
				ColorSchemeAssociationKind.BORDER);
		headerSchemeBundle.registerColorScheme(headerSeparatorScheme,
				ColorSchemeAssociationKind.SEPARATOR);

		headerSchemeBundle.registerHighlightColorScheme(headerActiveScheme,
				1.0f);
		// the next line is to have consistent coloring during the rollover
		// menu animations
		headerSchemeBundle.registerHighlightColorScheme(headerActiveScheme,
				0.0f, ComponentState.DEFAULT);

		this.registerDecorationAreaSchemeBundle(headerSchemeBundle,
				DecorationAreaType.TOOLBAR);

		this.registerDecorationAreaSchemeBundle(headerSchemeBundle,
				headerWatermarkScheme, DecorationAreaType.PRIMARY_TITLE_PANE,
				DecorationAreaType.SECONDARY_TITLE_PANE,
				DecorationAreaType.HEADER, DecorationAreaType.FOOTER);

		setSelectedTabFadeStart(0.1);
		setSelectedTabFadeEnd(0.3);

		// add two overlay painters to create a bezel line between
		// menu bar and toolbars
		this.menuOverlayPainter = new BottomLineOverlayPainter(
				new ColorSchemeSingleColorQuery() {
					@Override
					public Color query(SubstanceColorScheme scheme) {
						return scheme.getUltraDarkColor().darker();
					}
				});
		this.toolbarOverlayPainter = new TopLineOverlayPainter(
				new ColorSchemeSingleColorQuery() {
					@Override
					public Color query(SubstanceColorScheme scheme) {
						Color fg = scheme.getForegroundColor();
						return new Color(fg.getRed(), fg.getGreen(), fg
								.getBlue(), 32);
					}
				});
		this.addOverlayPainter(this.menuOverlayPainter,
				DecorationAreaType.HEADER);
		this.addOverlayPainter(this.toolbarOverlayPainter,
				DecorationAreaType.TOOLBAR);

		this.buttonShaper = new ClassicButtonShaper();
		this.watermark = null;
		this.gradientPainter = new StandardGradientPainter();
		this.decorationPainter = new MatteDecorationPainter();
		this.highlightPainter = new ClassicHighlightPainter();
		this.borderPainter = new CompositeBorderPainter("Dust",
				new ClassicBorderPainter(), new DelegateBorderPainter(
						"Dust Inner", new ClassicBorderPainter(), 0x60FFFFFF,
						0x30FFFFFF, 0x18FFFFFF, new ColorSchemeTransform() {
							@Override
							public SubstanceColorScheme transform(
									SubstanceColorScheme scheme) {
								return scheme.shiftBackground(
										scheme.getUltraLightColor(), 0.8).tint(
										0.6).saturate(0.2);
							}
						}));
	}

	public String getDisplayName() {
		return NAME;
	}
}
