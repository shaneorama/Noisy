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
import org.jvnet.substance.colorscheme.EbonyColorScheme;
import org.jvnet.substance.painter.border.*;
import org.jvnet.substance.painter.decoration.ArcDecorationPainter;
import org.jvnet.substance.painter.decoration.DecorationAreaType;
import org.jvnet.substance.painter.gradient.GlassGradientPainter;
import org.jvnet.substance.painter.highlight.GlassHighlightPainter;
import org.jvnet.substance.shaper.ClassicButtonShaper;
import org.jvnet.substance.utils.SubstanceColorUtilities;

/**
 * <code>Raven Graphite Glass</code> skin. This class is part of officially
 * supported API.
 * 
 * @author Kirill Grouchnikov
 * @since version 3.3
 */
public class RavenGraphiteGlassSkin extends SubstanceSkin {
	/**
	 * Display name for <code>this</code> skin.
	 */
	public static final String NAME = "Raven Graphite Glass";

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
	 * Creates a new <code>Raven Graphite</code> skin.
	 */
	public RavenGraphiteGlassSkin() {
		SubstanceSkin.ColorSchemes schemes = SubstanceSkin
				.getColorSchemes(RavenGraphiteGlassSkin.class
						.getClassLoader()
						.getResource(
								"org/jvnet/substance/skin/ravengraphite.colorschemes"));

		SubstanceColorScheme activeScheme = schemes
				.get("Raven Graphite Active");
		SubstanceColorScheme selectedDisabledScheme = schemes
				.get("Raven Graphite Selected Disabled");
		SubstanceColorScheme disabledScheme = schemes
				.get("Raven Graphite Disabled");

		SubstanceColorScheme defaultScheme = schemes
				.get("Raven Graphite Default");
		SubstanceColorScheme backgroundScheme = schemes
				.get("Raven Graphite Background");
		SubstanceColorSchemeBundle defaultSchemeBundle = new SubstanceColorSchemeBundle(
				activeScheme, defaultScheme, disabledScheme);

		// highlight fill scheme + custom alphas
		SubstanceColorScheme highlightScheme = schemes
				.get("Raven Graphite Highlight");
		defaultSchemeBundle.registerHighlightColorScheme(highlightScheme, 0.6f,
				ComponentState.ROLLOVER_UNSELECTED);
		defaultSchemeBundle.registerHighlightColorScheme(highlightScheme, 0.8f,
				ComponentState.SELECTED);
		defaultSchemeBundle.registerHighlightColorScheme(highlightScheme, 1.0f,
				ComponentState.ROLLOVER_SELECTED);
		defaultSchemeBundle.registerHighlightColorScheme(highlightScheme,
				0.75f, ComponentState.ARMED, ComponentState.ROLLOVER_ARMED);

		// highlight border scheme
		SubstanceColorScheme borderScheme = schemes
				.get("Raven Graphite Border");
		SubstanceColorScheme separatorScheme = schemes
				.get("Raven Graphite Separator");
		defaultSchemeBundle.registerColorScheme(new EbonyColorScheme(),
				ColorSchemeAssociationKind.HIGHLIGHT_BORDER, ComponentState
						.getActiveStates());
		defaultSchemeBundle.registerColorScheme(borderScheme,
				ColorSchemeAssociationKind.BORDER);
		defaultSchemeBundle.registerColorScheme(separatorScheme,
				ColorSchemeAssociationKind.SEPARATOR);

		// text highlight scheme
		SubstanceColorScheme textHighlightScheme = schemes
				.get("Raven Graphite Text Highlight");
		defaultSchemeBundle.registerColorScheme(textHighlightScheme,
				ColorSchemeAssociationKind.TEXT_HIGHLIGHT,
				ComponentState.SELECTED, ComponentState.ROLLOVER_SELECTED);

		defaultSchemeBundle.registerColorScheme(highlightScheme,
				ComponentState.ARMED, ComponentState.ROLLOVER_ARMED);

		// register schemes for disabled states
		defaultSchemeBundle.registerColorScheme(disabledScheme, 0.5f,
				ComponentState.DISABLED_UNSELECTED);
		defaultSchemeBundle.registerColorScheme(selectedDisabledScheme, 0.5f,
				ComponentState.DISABLED_SELECTED);

		this.registerDecorationAreaSchemeBundle(defaultSchemeBundle,
				backgroundScheme, DecorationAreaType.NONE);

		this.registerAsDecorationArea(backgroundScheme,
				DecorationAreaType.PRIMARY_TITLE_PANE,
				DecorationAreaType.SECONDARY_TITLE_PANE,
				DecorationAreaType.HEADER);// , DecorationAreaType.TOOLBAR);

		this.setSelectedTabFadeStart(0.1);
		this.setSelectedTabFadeEnd(0.3);

		// add two overlay painters to create a bezel line between
		// menu bar and toolbars
		this.menuOverlayPainter = new BottomLineOverlayPainter(
				ColorSchemeSingleColorQuery.MID);
		this.toolbarOverlayPainter = new TopLineOverlayPainter(
				new ColorSchemeSingleColorQuery() {
					@Override
					public Color query(SubstanceColorScheme scheme) {
						return SubstanceColorUtilities.getAlphaColor(scheme
								.getForegroundColor(), 32);
					}
				});
		this.addOverlayPainter(this.menuOverlayPainter,
				DecorationAreaType.HEADER);
		this.addOverlayPainter(this.toolbarOverlayPainter,
				DecorationAreaType.TOOLBAR);

		this.buttonShaper = new ClassicButtonShaper();
		this.watermark = null;
		this.gradientPainter = new GlassGradientPainter();
		this.decorationPainter = new ArcDecorationPainter();
		this.highlightPainter = new GlassHighlightPainter();
		this.borderPainter = new CompositeBorderPainter("Raven Graphite Glass",
				new ClassicBorderPainter(), new DelegateBorderPainter(
						"Raven Graphite Glass Inner",
						new ClassicBorderPainter(), 0xA0FFFFFF, 0x60FFFFFF,
						0x60FFFFFF, new ColorSchemeTransform() {
							@Override
							public SubstanceColorScheme transform(
									SubstanceColorScheme scheme) {
								return scheme.tint(0.25f);
							}
						}));
		this.highlightBorderPainter = new ClassicBorderPainter();
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
