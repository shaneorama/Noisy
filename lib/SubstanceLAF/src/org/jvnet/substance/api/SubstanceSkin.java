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
package org.jvnet.substance.api;

import java.awt.Component;
import java.awt.Graphics2D;
import java.net.URL;
import java.util.*;

import javax.swing.JTabbedPane;
import javax.swing.UIDefaults;

import org.jvnet.lafwidget.animation.FadeKind;
import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.api.ComponentState.ColorSchemeKind;
import org.jvnet.substance.api.painter.overlay.SubstanceOverlayPainter;
import org.jvnet.substance.api.painter.overlay.TopShadowOverlayPainter;
import org.jvnet.substance.api.trait.SubstanceTrait;
import org.jvnet.substance.painter.border.SubstanceBorderPainter;
import org.jvnet.substance.painter.decoration.DecorationAreaType;
import org.jvnet.substance.painter.decoration.SubstanceDecorationPainter;
import org.jvnet.substance.painter.gradient.SubstanceGradientPainter;
import org.jvnet.substance.painter.highlight.SubstanceHighlightPainter;
import org.jvnet.substance.shaper.SubstanceButtonShaper;
import org.jvnet.substance.utils.SkinUtilities;
import org.jvnet.substance.utils.SubstanceColorSchemeUtilities;
import org.jvnet.substance.watermark.SubstanceWatermark;

/**
 * Base abstract class for Substance skins.
 * 
 * @author Kirill Grouchnikov
 */
@SubstanceApi
public abstract class SubstanceSkin implements SubstanceTrait, Cloneable {
	/**
	 * Maps decoration area type to the color scheme bundles. Must contain an
	 * entry for {@link DecorationAreaType#NONE}.
	 */
	protected Map<DecorationAreaType, SubstanceColorSchemeBundle> colorSchemeBundleMap;

	/**
	 * Maps decoration area type to the background color schemes.
	 */
	protected Map<DecorationAreaType, SubstanceColorScheme> backgroundColorSchemeMap;

	/**
	 * Maps decoration area type to the registered overlay painters. Each
	 * decoration area type can have more than one overlay painter.
	 */
	protected Map<DecorationAreaType, List<SubstanceOverlayPainter>> overlayPaintersMap;

	/**
	 * The watermark of <code>this</code> skin. May be <code>null</code> if
	 * <code>this</code> skin doesn't define a custom watermark.
	 */
	protected SubstanceWatermark watermark;

	/**
	 * The button shaper of <code>this</code> skin. Must be non-
	 * <code>null</code>.
	 */
	protected SubstanceButtonShaper buttonShaper;

	/**
	 * The gradient painter of <code>this</code> skin. Must be non-
	 * <code>null</code>.
	 */
	protected SubstanceGradientPainter gradientPainter;

	/**
	 * The border painter of <code>this</code> skin. Must be non-
	 * <code>null</code>.
	 */
	protected SubstanceBorderPainter borderPainter;

	/**
	 * The highlight border painter of <code>this</code> skin. Can be
	 * <code>null</code>.
	 */
	protected SubstanceBorderPainter highlightBorderPainter;

	/**
	 * The highlight painter of <code>this</code> skin. Must be non-
	 * <code>null</code>.
	 */
	protected SubstanceHighlightPainter highlightPainter;

	/**
	 * The decoration painter of <code>this</code> skin. Must be non-
	 * <code>null</code>.
	 */
	protected SubstanceDecorationPainter decorationPainter;

	/**
	 * Set of all decoration area types that are not explicitly registered in
	 * {@link #colorSchemeBundleMap} but still are considered as decoration
	 * areas in this skin. Controls lying in such areas will have their
	 * background painted by
	 * {@link SubstanceDecorationPainter#paintDecorationArea(Graphics2D, Component, DecorationAreaType, int, int, SubstanceSkin)}
	 * instead of a simple background fill.
	 */
	protected Set<DecorationAreaType> decoratedAreaSet;

	/**
	 * The start of fade effect on the selected tabs in {@link JTabbedPane}s.
	 * 
	 * @see #selectedTabFadeEnd
	 */
	protected double selectedTabFadeStart;

	/**
	 * The end of fade effect on the selected tabs in {@link JTabbedPane}s.
	 * 
	 * @see #selectedTabFadeStart
	 */
	protected double selectedTabFadeEnd;

	/**
	 * Contains the types of decoration areas that show a drop shadow on the few
	 * top pixels.
	 */
	// protected Set<DecorationAreaType> dropShadowsSet;

	/**
	 * Color scheme for watermarks.
	 */
	protected SubstanceColorScheme watermarkScheme;

	/**
	 * All component states that have associated non-trivial alpha values.
	 */
	Set<ComponentState> statesWithAlpha;

	/**
	 * Constructs the basic data structures for a skin.
	 */
	protected SubstanceSkin() {
		this.colorSchemeBundleMap = new EnumMap<DecorationAreaType, SubstanceColorSchemeBundle>(
				DecorationAreaType.class);
		this.backgroundColorSchemeMap = new EnumMap<DecorationAreaType, SubstanceColorScheme>(
				DecorationAreaType.class);
		this.overlayPaintersMap = new EnumMap<DecorationAreaType, List<SubstanceOverlayPainter>>(
				DecorationAreaType.class);

		this.decoratedAreaSet = EnumSet.of(
				DecorationAreaType.PRIMARY_TITLE_PANE,
				DecorationAreaType.SECONDARY_TITLE_PANE);

		// this.dropShadowsSet = EnumSet.noneOf(DecorationAreaType.class);

		this.selectedTabFadeStart = 0.1;
		this.selectedTabFadeEnd = 0.3;

		this.statesWithAlpha = EnumSet.noneOf(ComponentState.class);
	}

	/**
	 * Returns the main active color scheme for this skin. The result is the
	 * active color scheme for controls that do not lie in any decoration area.
	 * Custom painting code that needs to consult the colors of the specific
	 * component should use {@link #getColorScheme(Component, ComponentState)}
	 * method and various {@link SubstanceColorScheme} methods.
	 * 
	 * @return The main active color scheme for this skin.
	 * @see #getColorScheme(Component, ComponentState)
	 * @see #getMainActiveColorScheme(DecorationAreaType)
	 */
	public final SubstanceColorScheme getMainActiveColorScheme() {
		return this.colorSchemeBundleMap.get(DecorationAreaType.NONE)
				.getActiveColorScheme();
	}

	/**
	 * Returns the main default color scheme for this skin. The result is the
	 * default color scheme for controls that do not lie in any decoration area.
	 * Custom painting code that needs to consult the colors of the specific
	 * component should use {@link #getColorScheme(Component, ComponentState)}
	 * method and various {@link SubstanceColorScheme} methods.
	 * 
	 * @return The main default color scheme for this skin.
	 * @see #getColorScheme(Component, ComponentState)
	 * @see #getMainDefaultColorScheme(DecorationAreaType)
	 */
	public final SubstanceColorScheme getMainDefaultColorScheme() {
		return this.colorSchemeBundleMap.get(DecorationAreaType.NONE)
				.getDefaultColorScheme();
	}

	/**
	 * Returns the main disabled color scheme for this skin. The result is the
	 * disabled color scheme for controls that do not lie in any decoration
	 * area. Custom painting code that needs to consult the colors of the
	 * specific component should use
	 * {@link #getColorScheme(Component, ComponentState)} method and various
	 * {@link SubstanceColorScheme} methods.
	 * 
	 * @return The main disabled color scheme for this skin.
	 * @see #getColorScheme(Component, ComponentState)
	 * @see #getMainDisabledColorScheme(DecorationAreaType)
	 */
	public final SubstanceColorScheme getMainDisabledColorScheme() {
		return this.colorSchemeBundleMap.get(DecorationAreaType.NONE)
				.getDisabledColorScheme();
	}

	/**
	 * Returns the watermark of this skin.
	 * 
	 * @return The watermark of this skin. May be <code>null</code>.
	 */
	public final SubstanceWatermark getWatermark() {
		return this.watermark;
	}

	/**
	 * Returns the border painter of this skin.
	 * 
	 * @return The border painter of this skin. A valid skin cannot have a
	 *         <code>null</code> value returned from this method. Call
	 *         {@link #isValid()
	 * 	} to verify that the skin is valid.
	 * @see #isValid()
	 */
	public final SubstanceBorderPainter getBorderPainter() {
		return this.borderPainter;
	}

	/**
	 * Returns the highlight border painter of this skin.
	 * 
	 * @return The highlight border painter of this skin. The return value of
	 *         this method may be <code>null</code>. In this case, call
	 *         {@link #getBorderPainter()}.
	 */
	public final SubstanceBorderPainter getHighlightBorderPainter() {
		return this.highlightBorderPainter;
	}

	/**
	 * Returns the button shaper of this skin.
	 * 
	 * @return The button shaper of this skin. A valid skin cannot have a
	 *         <code>null</code> value returned from this method. Call
	 *         {@link #isValid()
	 * 	} to verify that the skin is valid.
	 * @see #isValid()
	 */
	public final SubstanceButtonShaper getButtonShaper() {
		return this.buttonShaper;
	}

	/**
	 * Returns the gradient painter of this skin.
	 * 
	 * @return The gradient painter of this skin. A valid skin cannot have a
	 *         <code>null</code> value returned from this method. Call
	 *         {@link #isValid()
	 * 	} to verify that the skin is valid.
	 * @see #isValid()
	 */
	public final SubstanceGradientPainter getGradientPainter() {
		return this.gradientPainter;
	}

	/**
	 * Returns the highlight painter of this skin.
	 * 
	 * @return The highlight painter of this skin. A valid skin cannot have a
	 *         <code>null</code> value returned from this method. Call
	 *         {@link #isValid()
	 * 	} to verify that the skin is valid.
	 * @see #isValid()
	 */
	public final SubstanceHighlightPainter getHighlightPainter() {
		return this.highlightPainter;
	}

	/**
	 * Returns the decoration painter of this skin.
	 * 
	 * @return The decoration painter of this skin. A valid skin cannot have a
	 *         <code>null</code> value returned from this method. Call
	 *         {@link #isValid()
	 * 	} to verify that the skin is valid.
	 * @see #isValid()
	 */
	public final SubstanceDecorationPainter getDecorationPainter() {
		return this.decorationPainter;
	}

	/**
	 * Adds skin-specific entries to the UI defaults table.
	 * 
	 * @param table
	 *            UI defaults table.
	 */
	public void addCustomEntriesToTable(UIDefaults table) {
		// Apparently this function is called with null table
		// when the application is run with -Dswing.defaultlaf
		// setting. In this case, this function will be called
		// second time with correct table.
		if (table == null) {
			return;
		}

		SkinUtilities.addCustomEntriesToTable(table, this);
	}

	/**
	 * Returns the color scheme of the specified component in the specified
	 * component state.
	 * 
	 * @param comp
	 *            Component.
	 * @param componentState
	 *            Component state.
	 * @return The color scheme of the component in the specified component
	 *         state.
	 */
	public final SubstanceColorScheme getColorScheme(Component comp,
			ComponentState componentState) {
		// small optimization - lookup the decoration area only if there
		// are decoration-specific scheme bundles.
		if (this.colorSchemeBundleMap.size() > 1) {
			DecorationAreaType decorationAreaType = SubstanceLookAndFeel
					.getDecorationType(comp);
			// if ((decorationAreaType == DecorationAreaType.NONE)
			// && (componentState == ComponentState.DEFAULT)) {
			// return this.defaultColorScheme;
			// }
			if (this.colorSchemeBundleMap.containsKey(decorationAreaType)) {
				SubstanceColorScheme registered = this.colorSchemeBundleMap
						.get(decorationAreaType).getColorScheme(componentState);
				if (registered != null)
					return registered;
			}
		}

		// if (componentState == ComponentState.DEFAULT)
		// return this.defaultColorScheme;

		SubstanceColorScheme registered = this.colorSchemeBundleMap.get(
				DecorationAreaType.NONE).getColorScheme(componentState);
		if (registered != null)
			return registered;

		if (componentState == ComponentState.DEFAULT)
			return this.getMainDefaultColorScheme();
		if (componentState.getColorSchemeKind() == ColorSchemeKind.DISABLED)
			return this.getMainDisabledColorScheme();
		return this.getMainActiveColorScheme();
	}

	/**
	 * Returns the highlight color scheme of the component.
	 * 
	 * @param comp
	 *            Component.
	 * @param componentState
	 *            Component state.
	 * @return Component highlight color scheme.
	 * @deprecated Use
	 *             {@link #getColorScheme(Component, ColorSchemeAssociationKind, ComponentState)}
	 *             with {@link ColorSchemeAssociationKind#HIGHLIGHT} instead.
	 *             This method will be removed in version 6.0.
	 */
	@Deprecated
	public SubstanceColorScheme getHighlightColorScheme(Component comp,
			ComponentState componentState) {
		return this.getColorScheme(comp, ColorSchemeAssociationKind.HIGHLIGHT,
				componentState);
	}

	/**
	 * Returns the alpha channel of the highlight color scheme of the component.
	 * 
	 * @param comp
	 *            Component.
	 * @param componentState
	 *            Component state.
	 * @return Highlight color scheme alpha channel.
	 */
	public final float getHighlightAlpha(Component comp,
			ComponentState componentState) {
		// small optimization - lookup the decoration area only if there
		// are decoration-specific scheme bundles.
		if (this.colorSchemeBundleMap.size() > 1) {
			DecorationAreaType decorationAreaType = SubstanceLookAndFeel
					.getDecorationType(comp);
			if (this.colorSchemeBundleMap.containsKey(decorationAreaType)) {
				Float registered = this.colorSchemeBundleMap.get(
						decorationAreaType).getHighlightAlpha(comp,
						componentState);
				if (registered >= 0.0)
					return registered;
			}
		}

		Float registered = this.colorSchemeBundleMap.get(
				DecorationAreaType.NONE)
				.getHighlightAlpha(comp, componentState);
		if (registered >= 0.0)
			return registered;

		boolean isRollover = componentState.isKindActive(FadeKind.ROLLOVER);
		boolean isSelected = componentState.isKindActive(FadeKind.SELECTION);
		boolean isArmed = componentState.isKindActive(FadeKind.ARM);

		if (isRollover && isSelected)
			return 0.9f;
		if (isRollover && isArmed)
			return 0.8f;
		if (isSelected)
			return 0.7f;
		if (isArmed)
			return 0.6f;
		if (isRollover)
			return 0.4f;
		return 0.0f;
	}

	/**
	 * Returns the alpha channel of the color scheme of the component.
	 * 
	 * @param comp
	 *            Component.
	 * @param componentState
	 *            Component state.
	 * @return Color scheme alpha channel.
	 */
	public final float getAlpha(Component comp, ComponentState componentState) {
		// optimization - if the state is not registered in any
		// scheme bundle with custom alpha, return 1.0
		if (!this.statesWithAlpha.contains(componentState))
			return 1.0f;

		// small optimization - lookup the decoration area only if there
		// are decoration-specific scheme bundles.
		if (this.colorSchemeBundleMap.size() > 1) {
			DecorationAreaType decorationAreaType = SubstanceLookAndFeel
					.getDecorationType(comp);
			if (this.colorSchemeBundleMap.containsKey(decorationAreaType)) {
				Float registered = this.colorSchemeBundleMap.get(
						decorationAreaType).getAlpha(comp, componentState);
				if (registered >= 0.0)
					return registered;
			}
		}

		Float registered = this.colorSchemeBundleMap.get(
				DecorationAreaType.NONE).getAlpha(comp, componentState);
		if (registered >= 0.0)
			return registered;

		return 1.0f;
	}

	/**
	 * Registers the specified color scheme bundle and background color scheme
	 * to be used on controls in decoration areas.
	 * 
	 * @param bundle
	 *            The color scheme bundle to use on controls in decoration
	 *            areas.
	 * @param backgroundColorScheme
	 *            The color scheme to use for background of controls in
	 *            decoration areas.
	 * @param areaTypes
	 *            Enumerates the area types that are affected by the parameters.
	 */
	public void registerDecorationAreaSchemeBundle(
			SubstanceColorSchemeBundle bundle,
			SubstanceColorScheme backgroundColorScheme,
			DecorationAreaType... areaTypes) {
		if (bundle == null)
			return;

		if (backgroundColorScheme == null) {
			throw new IllegalArgumentException(
					"Cannot pass null background color scheme");
		}

		for (DecorationAreaType areaType : areaTypes) {
			this.decoratedAreaSet.add(areaType);
			this.colorSchemeBundleMap.put(areaType, bundle);
			this.backgroundColorSchemeMap.put(areaType, backgroundColorScheme);

			// if (areaType == DecorationAreaType.NONE) {
			// this.defaultColorScheme = bundle.getDefaultColorScheme();
			// }
		}
		this.statesWithAlpha.addAll(bundle.getStatesWithAlpha());
	}

	/**
	 * Registers the specified color scheme bundle to be used on controls in
	 * decoration areas.
	 * 
	 * @param bundle
	 *            The color scheme bundle to use on controls in decoration
	 *            areas.
	 * @param areaTypes
	 *            Enumerates the area types that are affected by the parameters.
	 */
	public void registerDecorationAreaSchemeBundle(
			SubstanceColorSchemeBundle bundle, DecorationAreaType... areaTypes) {
		this.registerDecorationAreaSchemeBundle(bundle, bundle
				.getDefaultColorScheme(), areaTypes);
	}

	/**
	 * Registers the specified background color scheme to be used on controls in
	 * decoration areas.
	 * 
	 * @param backgroundColorScheme
	 *            The color scheme to use for background of controls in
	 *            decoration areas.
	 * @param areaTypes
	 *            Enumerates the area types that are affected by the parameters.
	 *            Each decoration area type will be painted by
	 *            {@link SubstanceDecorationPainter#paintDecorationArea(Graphics2D, Component, DecorationAreaType, int, int, SubstanceSkin)}
	 *            .
	 */
	public void registerAsDecorationArea(
			SubstanceColorScheme backgroundColorScheme,
			DecorationAreaType... areaTypes) {
		if (backgroundColorScheme == null) {
			throw new IllegalArgumentException(
					"Cannot pass null background color scheme");
		}
		for (DecorationAreaType areaType : areaTypes) {
			this.decoratedAreaSet.add(areaType);
			this.backgroundColorSchemeMap.put(areaType, backgroundColorScheme);
		}
	}

	/**
	 * Returns indication whether the specified decoration area type should have
	 * their background painted by
	 * {@link SubstanceDecorationPainter#paintDecorationArea(Graphics2D, Component, DecorationAreaType, int, int, SubstanceSkin)}
	 * instead of a simple background fill.
	 * 
	 * @param decorationType
	 *            Decoration area type.
	 * @return <code>true</code> if specified decoration area type should have
	 *         their background painted by
	 *         {@link SubstanceDecorationPainter#paintDecorationArea(Graphics2D, Component, DecorationAreaType, int, int, SubstanceSkin)}
	 *         , <code>false</code> otherwise.
	 */
	public boolean isRegisteredAsDecorationArea(
			DecorationAreaType decorationType) {
		return this.decoratedAreaSet.contains(decorationType);
	}

	/**
	 * Returns the color scheme to be used for painting the watermark. If no
	 * custom watermark color scheme is specified ({@link #watermarkScheme} is
	 * <code>null</code>), the main default color scheme of this skin is used.
	 * 
	 * @return The color scheme to be used for painting the watermark.
	 */
	public SubstanceColorScheme getWatermarkColorScheme() {
		if (this.watermarkScheme != null) {
			return this.watermarkScheme;
		}

		return this.getMainDefaultColorScheme();
	}

	/**
	 * Returns the main active color scheme for the specific decoration area
	 * type. Custom painting code that needs to consult the colors of the
	 * specific component should use
	 * {@link #getColorScheme(Component, ComponentState)} method and various
	 * {@link SubstanceColorScheme} methods.
	 * 
	 * @param decorationAreaType
	 *            Decoration area type.
	 * @return The main active color scheme for this skin.
	 * @see #getColorScheme(Component, ComponentState)
	 * @see #getMainActiveColorScheme()
	 */
	public final SubstanceColorScheme getMainActiveColorScheme(
			DecorationAreaType decorationAreaType) {
		if (this.colorSchemeBundleMap.containsKey(decorationAreaType))
			return this.colorSchemeBundleMap.get(decorationAreaType)
					.getActiveColorScheme();
		return this.colorSchemeBundleMap.get(DecorationAreaType.NONE)
				.getActiveColorScheme();
	}

	/**
	 * Returns the main default color scheme for the specific decoration area
	 * type. Custom painting code that needs to consult the colors of the
	 * specific component should use
	 * {@link #getColorScheme(Component, ComponentState)} method and various
	 * {@link SubstanceColorScheme} methods.
	 * 
	 * @param decorationAreaType
	 *            Decoration area type.
	 * @return The main default color scheme for this skin.
	 * @see #getColorScheme(Component, ComponentState)
	 * @see #getMainDefaultColorScheme()
	 */
	public final SubstanceColorScheme getMainDefaultColorScheme(
			DecorationAreaType decorationAreaType) {
		if (this.colorSchemeBundleMap.containsKey(decorationAreaType))
			return this.colorSchemeBundleMap.get(decorationAreaType)
					.getDefaultColorScheme();
		return this.colorSchemeBundleMap.get(DecorationAreaType.NONE)
				.getDefaultColorScheme();
	}

	/**
	 * Returns the main disabled color scheme for the specific decoration area
	 * type. Custom painting code that needs to consult the colors of the
	 * specific component should use
	 * {@link #getColorScheme(Component, ComponentState)} method and various
	 * {@link SubstanceColorScheme} methods.
	 * 
	 * @param decorationAreaType
	 *            Decoration area type.
	 * @return The main disabled color scheme for this skin.
	 * @see #getColorScheme(Component, ComponentState)
	 * @see #getMainDisabledColorScheme()
	 */
	public final SubstanceColorScheme getMainDisabledColorScheme(
			DecorationAreaType decorationAreaType) {
		if (this.colorSchemeBundleMap.containsKey(decorationAreaType))
			return this.colorSchemeBundleMap.get(decorationAreaType)
					.getDisabledColorScheme();
		return this.colorSchemeBundleMap.get(DecorationAreaType.NONE)
				.getDisabledColorScheme();
	}

	/**
	 * Returns the start of fade effect on the selected tabs in
	 * {@link JTabbedPane}s. This value can be used to create XP-like "headers"
	 * on the selected tabs.
	 * 
	 * @return The start of fade effect on the selected tabs in
	 *         {@link JTabbedPane}s.
	 * @see #getSelectedTabFadeEnd()
	 */
	public final double getSelectedTabFadeStart() {
		return this.selectedTabFadeStart;
	}

	/**
	 * Returns the end of fade effect on the selected tabs in
	 * {@link JTabbedPane
	 * }s. This value can be used to create XP-like "headers"
	 * on the selected tabs.
	 * 
	 * @return The end of fade effect on the selected tabs in
	 *         {@link JTabbedPane
	 * 	}s.
	 * @see #getSelectedTabFadeStart()
	 */
	public final double getSelectedTabFadeEnd() {
		return this.selectedTabFadeEnd;
	}

	/**
	 * Sets the end of fade effect on the selected tabs in {@link JTabbedPane}s.
	 * The value should be in 0.0-1.0 range.
	 * 
	 * @param selectedTabFadeEnd
	 *            The end of fade effect on the selected tabs in
	 *            {@link JTabbedPane}s. Should be in 0.0-1.0 range.
	 */
	public void setSelectedTabFadeEnd(double selectedTabFadeEnd) {
		if ((selectedTabFadeEnd < 0.0) || (selectedTabFadeEnd > 1.0)) {
			throw new IllegalArgumentException(
					"Value for selected tab fade end should be in 0.0-1.0 range");
		}
		this.selectedTabFadeEnd = selectedTabFadeEnd;
	}

	/**
	 * Sets the start of fade effect on the selected tabs in {@link JTabbedPane}
	 * s. The value should be in 0.0-1.0 range.
	 * 
	 * @param selectedTabFadeStart
	 *            The start of fade effect on the selected tabs in
	 *            {@link JTabbedPane} s. Should be in 0.0-1.0 range.
	 */
	public void setSelectedTabFadeStart(double selectedTabFadeStart) {
		if ((selectedTabFadeStart < 0.0) || (selectedTabFadeStart > 1.0)) {
			throw new IllegalArgumentException(
					"Value for selected tab fade start should be in 0.0-1.0 range");
		}
		this.selectedTabFadeStart = selectedTabFadeStart;
	}

	/**
	 * Adds the specified overlay painter to the end of the list of overlay
	 * painters associated with the specified decoration area types.
	 * 
	 * @param overlayPainter
	 *            Overlay painter to add to the end of the list of overlay
	 *            painters associated with the specified decoration area types.
	 * @param areaTypes
	 *            Decoration area types.
	 */
	public void addOverlayPainter(SubstanceOverlayPainter overlayPainter,
			DecorationAreaType... areaTypes) {
		for (DecorationAreaType areaType : areaTypes) {
			if (!this.overlayPaintersMap.containsKey(areaType))
				this.overlayPaintersMap.put(areaType,
						new ArrayList<SubstanceOverlayPainter>());
			this.overlayPaintersMap.get(areaType).add(overlayPainter);
		}
	}

	/**
	 * Removes the specified overlay painter from the list of overlay painters
	 * associated with the specified decoration area types.
	 * 
	 * @param overlayPainter
	 *            Overlay painter to remove from the list of overlay painters
	 *            associated with the specified decoration area types.
	 * @param areaTypes
	 *            Decoration area types.
	 */
	public void removeOverlayPainter(SubstanceOverlayPainter overlayPainter,
			DecorationAreaType... areaTypes) {
		for (DecorationAreaType areaType : areaTypes) {
			if (!this.overlayPaintersMap.containsKey(areaType))
				return;
			this.overlayPaintersMap.get(areaType).remove(overlayPainter);
			if (this.overlayPaintersMap.get(areaType).size() == 0)
				this.overlayPaintersMap.remove(areaType);
		}
	}

	/**
	 * Returns a non-null, non-modifiable list of overlay painters associated
	 * with the specified decoration area type.
	 * 
	 * @param decorationAreaType
	 *            Decoration area type.
	 * @return A non-null, non-modifiable list of overlay painters associated
	 *         with the specified decoration area type.
	 */
	public List<SubstanceOverlayPainter> getOverlayPainters(
			DecorationAreaType decorationAreaType) {
		if (!this.overlayPaintersMap.containsKey(decorationAreaType))
			return Collections.emptyList();
		return Collections.unmodifiableList(this.overlayPaintersMap
				.get(decorationAreaType));
	}

	/**
	 * Returns indication whether the specified decoration area type should
	 * paint drop shadows on the top few pixels.
	 * 
	 * @param decorationAreaType
	 *            Decoration area type.
	 * @return <code>true</code> if the specified decoration area type should
	 *         paint drop shadows on the top few pixels, <code>false</code>
	 *         otherwise.
	 * @see #setPaintingDropShadows(DecorationAreaType)
	 * @see #unsetPaintingDropShadows(DecorationAreaType)
	 * @deprecated Use {@link #getOverlayPainters(DecorationAreaType)} and test
	 *             for presence of {@link TopShadowOverlayPainter}. This method
	 *             will be removed in version 6.0.
	 */
	@Deprecated
	public boolean isPaintingDropShadows(DecorationAreaType decorationAreaType) {
		List<SubstanceOverlayPainter> overlayPainters = this
				.getOverlayPainters(decorationAreaType);
		return overlayPainters.contains(TopShadowOverlayPainter.getInstance());
	}

	/**
	 * Marks the specified area type to have drop shadows on the top few pixels.
	 * 
	 * @param decorationAreaType
	 *            Decoration areas of this type will have drop shadows on the
	 *            top few pixels.
	 * @see #unsetPaintingDropShadows(DecorationAreaType)
	 * @see #isPaintingDropShadows(DecorationAreaType)
	 * @deprecated Use
	 *             {@link #addOverlayPainter(SubstanceOverlayPainter, DecorationAreaType...)}
	 *             with an instance of {@link TopShadowOverlayPainter}. This
	 *             method will be removed in version 6.0.
	 */
	@Deprecated
	public void setPaintingDropShadows(DecorationAreaType decorationAreaType) {
		this.addOverlayPainter(TopShadowOverlayPainter.getInstance(),
				decorationAreaType);
	}

	/**
	 * Marks the specified area type to not have drop shadows on the top few
	 * pixels.
	 * 
	 * @param decorationAreaType
	 *            Decoration areas of this type will not have drop shadows on
	 *            the top few pixels.
	 * @see #setPaintingDropShadows(DecorationAreaType)
	 * @see #isPaintingDropShadows(DecorationAreaType)
	 * @deprecated Use
	 *             {@link #removeOverlayPainter(SubstanceOverlayPainter, DecorationAreaType...)}
	 *             with an instance of {@link TopShadowOverlayPainter}. This
	 *             method will be removed in version 6.0.
	 */
	@Deprecated
	public void unsetPaintingDropShadows(DecorationAreaType decorationAreaType) {
		this.removeOverlayPainter(TopShadowOverlayPainter.getInstance(),
				decorationAreaType);
	}

	/**
	 * Returns the color scheme to be used for painting the specified visual
	 * area of components in the specified decoration area.
	 * 
	 * @param decorationAreaType
	 *            Decoration area type.
	 * @param associationKind
	 *            Color scheme association kind.
	 * @param componentState
	 *            Component state.
	 * @return Color scheme to be used for painting the specified visual area of
	 *         components in the specified decoration area.
	 * @since version 5.3
	 */
	public final SubstanceColorScheme getColorScheme(
			DecorationAreaType decorationAreaType,
			ColorSchemeAssociationKind associationKind,
			ComponentState componentState) {
		if (this.colorSchemeBundleMap.size() > 1) {
			if (this.colorSchemeBundleMap.containsKey(decorationAreaType)) {
				return this.colorSchemeBundleMap.get(decorationAreaType)
						.getColorScheme(associationKind, componentState);
			}
		}
		return this.colorSchemeBundleMap.get(DecorationAreaType.NONE)
				.getColorScheme(associationKind, componentState);
	}

	/**
	 * Returns the color scheme to be used for painting the specified visual
	 * area of the component under the specified component state.
	 * 
	 * @param comp
	 *            Component.
	 * @param associationKind
	 *            Color scheme association kind.
	 * @param componentState
	 *            Component state.
	 * @return Color scheme to be used for painting the specified visual area of
	 *         the component under the specified component state.
	 * @since version 5.1
	 */
	public final SubstanceColorScheme getColorScheme(Component comp,
			ColorSchemeAssociationKind associationKind,
			ComponentState componentState) {
		// small optimization - lookup the decoration area only if there
		// are decoration-specific scheme bundles.
		if (this.colorSchemeBundleMap.size() > 1) {
			DecorationAreaType decorationAreaType = SubstanceLookAndFeel
					.getDecorationType(comp);
			if (this.colorSchemeBundleMap.containsKey(decorationAreaType)) {
				return this.colorSchemeBundleMap.get(decorationAreaType)
						.getColorScheme(associationKind, componentState);
			}
		}
		return this.colorSchemeBundleMap.get(DecorationAreaType.NONE)
				.getColorScheme(associationKind, componentState);
	}

	/**
	 * Returns the border color scheme of the component.
	 * 
	 * @param comp
	 *            Component.
	 * @param componentState
	 *            Component state.
	 * @return Component border color scheme.
	 * @deprecated Use
	 *             {@link #getColorScheme(Component, ColorSchemeAssociationKind, ComponentState)}
	 *             with {@link ColorSchemeAssociationKind#BORDER} instead. This
	 *             method will be removed in version 6.0.
	 */
	@Deprecated
	public SubstanceColorScheme getBorderColorScheme(Component comp,
			ComponentState componentState) {
		// small optimization - lookup the decoration area only if there
		// are decoration-specific scheme bundles.
		if (this.colorSchemeBundleMap.size() > 1) {
			DecorationAreaType decorationAreaType = SubstanceLookAndFeel
					.getDecorationType(comp);
			if (this.colorSchemeBundleMap.containsKey(decorationAreaType)) {
				return this.colorSchemeBundleMap.get(decorationAreaType)
						.getColorScheme(ColorSchemeAssociationKind.BORDER,
								componentState);
			}
		}
		return this.colorSchemeBundleMap.get(DecorationAreaType.NONE)
				.getColorScheme(ColorSchemeAssociationKind.BORDER,
						componentState);
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		final String myName = this.getDisplayName();
		SubstanceSkin clone = new SubstanceSkin() {
			@Override
			public String getDisplayName() {
				return "Clone " + myName;
			}
		};
		clone.borderPainter = this.borderPainter;
		clone.buttonShaper = this.buttonShaper;
		clone.decorationPainter = this.decorationPainter;
		clone.gradientPainter = this.gradientPainter;
		clone.highlightBorderPainter = this.highlightBorderPainter;
		clone.highlightPainter = this.highlightPainter;
		clone.watermark = this.watermark;
		clone.watermarkScheme = this.watermarkScheme;
		// clone.defaultColorScheme = this.defaultColorScheme;
		clone.selectedTabFadeEnd = this.selectedTabFadeEnd;
		clone.selectedTabFadeStart = this.selectedTabFadeStart;

		if (this.colorSchemeBundleMap != null) {
			clone.colorSchemeBundleMap = new EnumMap<DecorationAreaType, SubstanceColorSchemeBundle>(
					DecorationAreaType.class);
			for (Map.Entry<DecorationAreaType, SubstanceColorSchemeBundle> bundleEntry : this.colorSchemeBundleMap
					.entrySet()) {
				clone.colorSchemeBundleMap.put(bundleEntry.getKey(),
						(SubstanceColorSchemeBundle) bundleEntry.getValue()
								.clone());
			}
		}

		if (this.decoratedAreaSet != null) {
			clone.decoratedAreaSet = EnumSet.copyOf(this.decoratedAreaSet);
		}

		// if (this.dropShadowsSet != null) {
		// clone.dropShadowsSet = EnumSet.copyOf(this.dropShadowsSet);
		// }

		if (this.backgroundColorSchemeMap != null) {
			clone.backgroundColorSchemeMap = new EnumMap<DecorationAreaType, SubstanceColorScheme>(
					this.backgroundColorSchemeMap);
		}
		if (this.overlayPaintersMap != null) {
			clone.overlayPaintersMap = new EnumMap<DecorationAreaType, List<SubstanceOverlayPainter>>(
					this.overlayPaintersMap);
		}
		return clone;
	}

	/**
	 * Creates a new skin that has the same settings as this skin with the
	 * addition of applying the specified color scheme transformation on all the
	 * relevant color schemes.
	 * 
	 * @param transform
	 *            Color scheme transformation.
	 * @param name
	 *            The name of the new skin.
	 * @return The new skin.
	 */
	@SubstanceApi
	public SubstanceSkin transform(ColorSchemeTransform transform,
			final String name) {
		SubstanceSkin result = new SubstanceSkin() {
			@Override
			public String getDisplayName() {
				return name;
			}
		};
		// same painters
		result.borderPainter = this.borderPainter;
		result.buttonShaper = this.buttonShaper;
		result.decorationPainter = this.decorationPainter;
		result.gradientPainter = this.gradientPainter;
		result.highlightPainter = this.highlightPainter;
		result.highlightBorderPainter = this.highlightBorderPainter;
		// same watermark and transformed scheme
		result.watermark = this.watermark;
		if (this.watermarkScheme != null)
			result.watermarkScheme = transform.transform(this.watermarkScheme);
		// issue 428 - transform the default color scheme
		// result.defaultColorScheme = transform
		// .transform(this.defaultColorScheme);

		// same misc settings
		result.selectedTabFadeEnd = this.selectedTabFadeEnd;
		result.selectedTabFadeStart = this.selectedTabFadeStart;

		// transform the scheme bundles
		if (this.colorSchemeBundleMap != null) {
			result.colorSchemeBundleMap = new EnumMap<DecorationAreaType, SubstanceColorSchemeBundle>(
					DecorationAreaType.class);
			for (Map.Entry<DecorationAreaType, SubstanceColorSchemeBundle> bundleEntry : this.colorSchemeBundleMap
					.entrySet()) {
				result.colorSchemeBundleMap.put(bundleEntry.getKey(),
						bundleEntry.getValue().transform(transform));
			}
		}

		// same set of decoration areas
		if (this.decoratedAreaSet != null) {
			result.decoratedAreaSet = EnumSet.copyOf(this.decoratedAreaSet);
		}
		// // same set of drop shadow decorated areas
		// if (this.dropShadowsSet != null) {
		// result.dropShadowsSet = EnumSet.copyOf(this.dropShadowsSet);
		// }
		// transform the background schemes
		if (this.backgroundColorSchemeMap != null) {
			result.backgroundColorSchemeMap = new EnumMap<DecorationAreaType, SubstanceColorScheme>(
					DecorationAreaType.class);
			for (Map.Entry<DecorationAreaType, SubstanceColorScheme> entry : this.backgroundColorSchemeMap
					.entrySet()) {
				result.backgroundColorSchemeMap.put(entry.getKey(), transform
						.transform(entry.getValue()));
			}
		}
		// same map of overlay painters
		result.overlayPaintersMap = new EnumMap<DecorationAreaType, List<SubstanceOverlayPainter>>(
				this.overlayPaintersMap);
		return result;
	}

	/**
	 * Creates a clone of this skin with the specified watermark. This is mainly
	 * targeting sample applications and docrobot. Applications are encouraged
	 * to create a custom skin by either extending one of the core Substance
	 * skins, or by extending the {@link SubstanceSkin} directly.
	 * 
	 * @param watermark
	 *            Watermark for the new skin.
	 * @return A clone of this skin with the specified watermark.
	 */
	@SubstanceApi
	public SubstanceSkin withWatermark(SubstanceWatermark watermark) {
		try {
			SubstanceSkin clone = (SubstanceSkin) this.clone();
			clone.watermark = watermark;
			return clone;
		} catch (CloneNotSupportedException cnse) {
			return null;
		}
	}

	/**
	 * Returns the background color scheme for the specified decoration area
	 * type. This method is mainly for the internal use of
	 * {@link SubstanceDecorationPainter#paintDecorationArea(Graphics2D, Component, DecorationAreaType, int, int, SubstanceSkin)}
	 * , but can be used in applications that wish to provide custom overlay
	 * background painting (such as watermarks, for example).
	 * 
	 * @param decorationAreaType
	 *            Decoration area type.
	 * @return The background color scheme for the specified decoration area
	 *         type.
	 */
	public final SubstanceColorScheme getBackgroundColorScheme(
			DecorationAreaType decorationAreaType) {
		// 1 - check the registered background scheme for this specific area
		// type.
		if (this.backgroundColorSchemeMap.containsKey(decorationAreaType))
			return this.backgroundColorSchemeMap.get(decorationAreaType);
		// 2 - check the registered scheme bundle for this specific area type.
		if (this.colorSchemeBundleMap.containsKey(decorationAreaType)) {
			SubstanceColorScheme registered = this.colorSchemeBundleMap.get(
					decorationAreaType).getDefaultColorScheme();
			if (registered != null)
				return registered;
		}
		// 3 - return the background scheme for the default area type
		return this.backgroundColorSchemeMap.get(DecorationAreaType.NONE);
	}

	/**
	 * Checks whether this skin is valid. A valid skin must have a color scheme
	 * bundle for {@link DecorationAreaType#NONE} and non-<code>null</code>
	 * button shaper, gradient painter, border painter, highlight painter and
	 * decoration painter. If call to
	 * {@link SubstanceLookAndFeel#setSkin(String)} or
	 * {@link SubstanceLookAndFeel#setSkin(SubstanceSkin)} does not seem to have
	 * any visible effect (returning <code>false</code>), call this method to
	 * verify that your skin is valid.
	 * 
	 * @return <code>true</code> if this skin is valid, <code>false</code>
	 *         otherwise.
	 */
	public boolean isValid() {
		if (!this.colorSchemeBundleMap.containsKey(DecorationAreaType.NONE))
			return false;
		if (this.getButtonShaper() == null) {
			return false;
		}
		if (this.getGradientPainter() == null) {
			return false;
		}
		if (this.getBorderPainter() == null) {
			return false;
		}
		if (this.getHighlightPainter() == null) {
			return false;
		}
		if (this.getDecorationPainter() == null) {
			return false;
		}
		return true;
	}

	/**
	 * Contains information on color schemes loaded by the
	 * {@link SubstanceSkin#getColorSchemes(URL)} API. Note that the custom
	 * skins should only use the {@link #get(String)} API. The rest of the API
	 * is currently internal and is used in the <strong>Jitterbug</strong>
	 * visual editor.
	 * 
	 * @author Kirill Grouchnikov
	 */
	public static class ColorSchemes {
		/**
		 * List of color schemes of this object.
		 */
		private List<SubstanceColorScheme> schemes;

		/**
		 * Creates an object with empty list of color schemes. This method is
		 * for internal use only and should not be used in custom application
		 * skins.
		 */
		public ColorSchemes() {
			this.schemes = new ArrayList<SubstanceColorScheme>();
		}

		/**
		 * Creates an object based on the specified list of color schemes. This
		 * method is for internal use only and should not be used in custom
		 * application skins.
		 * 
		 * @param schemes
		 *            List of color schemes.
		 */
		public ColorSchemes(List<SubstanceColorScheme> schemes) {
			this();
			this.schemes.addAll(schemes);
		}

		/**
		 * Returns the number of color schemes in this object. This method is
		 * for internal use only and should not be used in custom application
		 * skins.
		 * 
		 * @return The number of color schemes in this object.
		 */
		public int size() {
			return this.schemes.size();
		}

		/**
		 * Returns the color scheme at the specified index. This method is for
		 * internal use only and should not be used in custom application skins.
		 * 
		 * @param index
		 *            Index.
		 * @return Color scheme at the specified index.
		 */
		public SubstanceColorScheme get(int index) {
			return this.schemes.get(index);
		}

		/**
		 * Returns the color scheme based on its display name. This method is
		 * the only API that is published for use in custom application skins.
		 * 
		 * @param displayName
		 *            Display name of a color scheme.
		 * @return The color scheme with the matching display name.
		 */
		public SubstanceColorScheme get(String displayName) {
			for (SubstanceColorScheme scheme : this.schemes) {
				if (scheme.getDisplayName().equals(displayName)) {
					return scheme;
				}
			}
			return null;
		}

		/**
		 * Returns the index of the color scheme that has the specified display
		 * name. This method is for internal use only and should not be used in
		 * custom application skins.
		 * 
		 * @param displayName
		 *            Display name of a color scheme.
		 * @return The index of the color scheme that has the specified display
		 *         name.
		 */
		private int indexOf(String displayName) {
			for (int i = 0; i < this.schemes.size(); i++) {
				SubstanceColorScheme curr = this.schemes.get(i);
				if (curr.getDisplayName().equals(displayName)) {
					return i;
				}
			}
			return -1;
		}

		/**
		 * Finds the index of the color scheme that has the specified display
		 * name and replaces it with (possibly another) color scheme. This
		 * method is for internal use only and should not be used in custom
		 * application skins.
		 * 
		 * @param displayName
		 *            Display name of a color scheme.
		 * @param scheme
		 *            Color scheme that will replace the existing color scheme
		 *            (based on the display name) at the same index in the list.
		 */
		public void replace(String displayName, SubstanceColorScheme scheme) {
			int index = this.indexOf(displayName);

			if (index >= 0) {
				this.schemes.remove(index);
				this.schemes.add(index, scheme);
			}
		}

		/**
		 * Deletes the color scheme that has the specified display name. This
		 * method is for internal use only and should not be used in custom
		 * application skins.
		 * 
		 * @param displayName
		 *            Display name of the color scheme to delete from the list.
		 */
		public void delete(String displayName) {
			int index = this.indexOf(displayName);
			if (index >= 0) {
				this.schemes.remove(index);
			}
		}

		/**
		 * Adds the specified color scheme to the end of the list. This method
		 * is for internal use only and should not be used in custom application
		 * skins.
		 * 
		 * @param scheme
		 *            Color scheme to add to the end of the list.
		 */
		public void add(SubstanceColorScheme scheme) {
			this.schemes.add(scheme);
		}

		/**
		 * Moves the color scheme with the specified display name one position
		 * towards the beginning of the list. This method is for internal use
		 * only and should not be used in custom application skins.
		 * 
		 * @param displayName
		 *            Display name of the color scheme to move one position
		 *            towards the beginning of the list.
		 */
		public void switchWithPrevious(String displayName) {
			int index = this.indexOf(displayName);

			if (index >= 0) {
				SubstanceColorScheme scheme = this.schemes.remove(index);
				this.schemes.add(index - 1, scheme);
			}
		}

		/**
		 * Moves the color scheme with the specified display name one position
		 * towards the end of the list. This method is for internal use only and
		 * should not be used in custom application skins.
		 * 
		 * @param displayName
		 *            Display name of the color scheme to move one position
		 *            towards the end of the list.
		 */
		public void switchWithNext(String displayName) {
			int index = this.indexOf(displayName);

			if (index >= 0) {
				SubstanceColorScheme scheme = this.schemes.remove(index);
				this.schemes.add(index + 1, scheme);
			}
		}
	}

	/**
	 * Returns the collection of color schemes in the specified URL.
	 * 
	 * @param url
	 *            URL that points to a resource containing the description of
	 *            Substance color schemes.
	 * @return The collection of color schemes in the specified URL.
	 * @since version 5.2
	 */
	public static ColorSchemes getColorSchemes(URL url) {
		return SubstanceColorSchemeUtilities.getColorSchemes(url);
	}
}
