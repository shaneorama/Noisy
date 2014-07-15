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
import java.util.*;

import org.jvnet.lafwidget.animation.FadeKind;
import org.jvnet.substance.colorscheme.BlendBiColorScheme;
import org.jvnet.substance.painter.decoration.DecorationAreaType;

/**
 * Color scheme bundle. Defines the visual appearance of a single decoration
 * area of a skin.
 * 
 * @author Kirill Grouchnikov
 * @see DecorationAreaType
 * @see ColorSchemeAssociationKind
 * @see SubstanceSkin
 */
@SubstanceApi
public class SubstanceColorSchemeBundle implements Cloneable {
	/**
	 * The active color scheme of this bundle.
	 */
	protected SubstanceColorScheme activeColorScheme;

	/**
	 * The default color scheme of this bundle.
	 */
	protected SubstanceColorScheme defaultColorScheme;

	/**
	 * The disabled color scheme of this bundle.
	 */
	protected SubstanceColorScheme disabledColorScheme;

	/**
	 * Maps from component state to the alpha channel applied on color scheme.
	 * This map doesn't have to contain entries for all {@link ComponentState}
	 * instances.
	 */
	protected Map<ComponentState, Float> stateAlphaMap;

	/**
	 * Maps from component state to the alpha channel applied on highlight color
	 * scheme. This map doesn't have to contain entries for all
	 * {@link ComponentState} instances.
	 */
	protected Map<ComponentState, Float> stateHighlightSchemeAlphaMap;

	/**
	 * If there is no explicitly registered color scheme for pressed component
	 * states, this field will contain a synthesized color scheme for a pressed
	 * state.
	 */
	protected SubstanceColorScheme pressedScheme;

	/**
	 * If there is no explicitly registered color scheme for the disabled
	 * selected component states, this field will contain a synthesized color
	 * scheme for the disabled selected state.
	 */
	protected SubstanceColorScheme disabledSelectedScheme;

	/**
	 * Maps from color scheme association kinds to the map of color schemes.
	 * Different visual parts of controls in the specific decoration are can be
	 * painted with different color schemes. For example, a rollover button can
	 * use a light orange scheme for the gradient fill and a dark gray scheme
	 * for the border. In this case, this map will have:
	 * 
	 * <ul>
	 * <li>An entry with key {@link ColorSchemeAssociationKind#FILL}. This entry
	 * has a map entry with key {@link ComponentState#SELECTED} and value that
	 * points to the light orange scheme.</li>
	 * <li>An entry with key {@link ColorSchemeAssociationKind#BORDER}. This
	 * entry has a map entry with key {@link ComponentState#SELECTED} and value
	 * that points to the dark gray scheme.</li>
	 * </ul>
	 */
	protected Map<ColorSchemeAssociationKind, Map<ComponentState, SubstanceColorScheme>> colorSchemeMap;

	/**
	 * Creates a new color scheme bundle.
	 * 
	 * @param activeColorScheme
	 *            The active color scheme of this bundle.
	 * @param defaultColorScheme
	 *            The default color scheme of this bundle.
	 * @param disabledColorScheme
	 *            The disabled color scheme of this bundle.
	 */
	public SubstanceColorSchemeBundle(SubstanceColorScheme activeColorScheme,
			SubstanceColorScheme defaultColorScheme,
			SubstanceColorScheme disabledColorScheme) {
		this.activeColorScheme = activeColorScheme;
		this.defaultColorScheme = defaultColorScheme;
		this.disabledColorScheme = disabledColorScheme;
		this.stateAlphaMap = new EnumMap<ComponentState, Float>(
				ComponentState.class);
		this.stateHighlightSchemeAlphaMap = new EnumMap<ComponentState, Float>(
				ComponentState.class);

		this.colorSchemeMap = new HashMap<ColorSchemeAssociationKind, Map<ComponentState, SubstanceColorScheme>>();
		for (ColorSchemeAssociationKind associationKind : ColorSchemeAssociationKind
				.values()) {
			this.colorSchemeMap.put(associationKind,
					new EnumMap<ComponentState, SubstanceColorScheme>(
							ComponentState.class));
		}
	}

	/**
	 * Registers a color scheme for the specific component state.
	 * 
	 * @param stateColorScheme
	 *            Color scheme for the specified component state.
	 * @param alpha
	 *            Alpha channel for the color scheme.
	 * @param states
	 *            Component states.
	 */
	public void registerColorScheme(SubstanceColorScheme stateColorScheme,
			float alpha, ComponentState... states) {
		if (states != null) {
			for (ComponentState state : states) {
				this.colorSchemeMap.get(ColorSchemeAssociationKind.FILL).put(
						state, stateColorScheme);
				this.stateAlphaMap.put(state, alpha);
			}
		}
	}

	/**
	 * Registers a color scheme for the specific component state.
	 * 
	 * @param stateColorScheme
	 *            Color scheme for the specified component state.
	 * @param states
	 *            Component states.
	 */
	public void registerColorScheme(SubstanceColorScheme stateColorScheme,
			ComponentState... states) {
		this.registerColorScheme(stateColorScheme, 1.0f, states);
	}

	/**
	 * Registers a highlight color scheme for the specific component state if
	 * the component state is not <code>null</code>, or a global highlight color
	 * scheme otherwise.
	 * 
	 * @param stateHighlightScheme
	 *            Highlight color scheme for the specified component state.
	 * @param states
	 *            Component states. If <code>null</code>, the specified color
	 *            scheme will be applied for all states left unspecified.
	 */
	public void registerHighlightColorScheme(
			SubstanceColorScheme stateHighlightScheme, ComponentState... states) {
		if ((states == null) || (states.length == 0)) {
			for (ComponentState state : ComponentState.values()) {
				if (this.colorSchemeMap.get(
						ColorSchemeAssociationKind.HIGHLIGHT)
						.containsKey(state))
					continue;
				if (!state.isKindActive(FadeKind.ENABLE))
					continue;
				if (state == ComponentState.DEFAULT)
					continue;
				// this.stateHighlightColorSchemeMap.put(state,
				// stateHighlightScheme);
				this.colorSchemeMap.get(ColorSchemeAssociationKind.HIGHLIGHT)
						.put(state, stateHighlightScheme);
			}
		} else {
			for (ComponentState state : states) {
				this.colorSchemeMap.get(ColorSchemeAssociationKind.HIGHLIGHT)
						.put(state, stateHighlightScheme);
			}
		}
	}

	/**
	 * Registers a highlight color scheme for the specific component state if
	 * the component state is not <code>null</code>, or a global highlight color
	 * scheme otherwise.
	 * 
	 * @param highlightScheme
	 *            Highlight color scheme for the specified component states.
	 * @param alpha
	 *            Alpha channel for the highlight color scheme.
	 * @param states
	 *            Component states. If <code>null</code>, the specified color
	 *            scheme will be applied for all states left unspecified.
	 */
	public void registerHighlightColorScheme(
			SubstanceColorScheme highlightScheme, float alpha,
			ComponentState... states) {
		if (highlightScheme == null) {
			throw new IllegalArgumentException("Cannot pass null color scheme");
		}

		if ((states == null) || (states.length == 0)) {
			for (ComponentState state : ComponentState.values()) {
				if (!state.isKindActive(FadeKind.ENABLE))
					continue;
				if (state == ComponentState.DEFAULT)
					continue;
				if (!this.colorSchemeMap.get(
						ColorSchemeAssociationKind.HIGHLIGHT)
						.containsKey(state))
					this.colorSchemeMap.get(
							ColorSchemeAssociationKind.HIGHLIGHT).put(state,
							highlightScheme);
				if (!this.stateHighlightSchemeAlphaMap.containsKey(state))
					this.stateHighlightSchemeAlphaMap.put(state, alpha);
			}
		} else {
			for (ComponentState state : states) {
				this.colorSchemeMap.get(ColorSchemeAssociationKind.HIGHLIGHT)
						.put(state, highlightScheme);
				this.stateHighlightSchemeAlphaMap.put(state, alpha);
			}
		}
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
	public SubstanceColorScheme getColorScheme(ComponentState componentState) {
		SubstanceColorScheme registered = this.colorSchemeMap.get(
				ColorSchemeAssociationKind.FILL).get(componentState);
		if (registered != null)
			return registered;

		if (componentState.isKindActive(FadeKind.PRESS)) {
			if (this.pressedScheme == null) {
				this.pressedScheme = this.activeColorScheme.shade(0.2)
						.saturate(0.1);
			}
			return this.pressedScheme;
		}
		if (componentState == ComponentState.DISABLED_SELECTED) {
			if (this.disabledSelectedScheme == null) {
				this.disabledSelectedScheme = new BlendBiColorScheme(
						this.activeColorScheme, this.disabledColorScheme, 0.25);
			}
			return this.disabledSelectedScheme;
		}

		if (componentState == ComponentState.DEFAULT)
			return this.defaultColorScheme;
		if (!componentState.isKindActive(FadeKind.ENABLE))
			return this.disabledColorScheme;
		return this.activeColorScheme;
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
		return this.getColorScheme(ColorSchemeAssociationKind.HIGHLIGHT,
				componentState);
		// SubstanceColorScheme registered = this.colorSchemeMap.get(
		// ColorSchemeAssociationKind.HIGHLIGHT).get(componentState);
		// if (registered != null)
		// return registered;
		//
		// return null;
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
	public float getHighlightAlpha(Component comp, ComponentState componentState) {
		Float registered = this.stateHighlightSchemeAlphaMap
				.get(componentState);
		if (registered != null)
			return registered.floatValue();

		return -1.0f;
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
	public float getAlpha(Component comp, ComponentState componentState) {
		Float registered = this.stateAlphaMap.get(componentState);
		if (registered != null)
			return registered.floatValue();

		return -1.0f;
	}

	/**
	 * Returns the active color scheme of this bundle.
	 * 
	 * @return The active color scheme of this bundle.
	 */
	public SubstanceColorScheme getActiveColorScheme() {
		return activeColorScheme;
	}

	/**
	 * Returns the default color scheme of this bundle.
	 * 
	 * @return The default color scheme of this bundle.
	 */
	public SubstanceColorScheme getDefaultColorScheme() {
		return defaultColorScheme;
	}

	/**
	 * Returns the disabled color scheme of this bundle.
	 * 
	 * @return The disabled color scheme of this bundle.
	 */
	public SubstanceColorScheme getDisabledColorScheme() {
		return disabledColorScheme;
	}

	/**
	 * Registers the color scheme to be used for the specified visual area of
	 * controls under the specified states. For example, if the light orange
	 * scheme has to be used for gradient fill of rollover selected and rollover
	 * controls, the parameters would be:
	 * 
	 * <ul>
	 * <li><code>scheme</code>=light orange scheme</li>
	 * <li>
	 * <code>associationKind</code>={@link ColorSchemeAssociationKind#FILL}</li>
	 * <li>
	 * <code>states</code>={@link ComponentState#ROLLOVER_SELECTED},
	 * {@link ComponentState#ROLLOVER_UNSELECTED}</li>
	 * </ul>
	 * 
	 * @param scheme
	 *            Color scheme.
	 * @param associationKind
	 *            Color scheme association kind that specifies the visual areas
	 *            of controls to be painted with this color scheme.
	 * @param states
	 *            Component states that further restrict the usage of the
	 *            specified color scheme.
	 * @since version 5.1
	 */
	public void registerColorScheme(SubstanceColorScheme scheme,
			ColorSchemeAssociationKind associationKind,
			ComponentState... states) {
		if (scheme == null) {
			throw new IllegalArgumentException("Cannot pass null color scheme");
		}

		if ((states == null) || (states.length == 0)) {
			for (ComponentState state : ComponentState.values()) {
				if (this.colorSchemeMap.get(associationKind).containsKey(state))
					continue;
				this.colorSchemeMap.get(associationKind).put(state, scheme);
			}
		} else {
			for (ComponentState state : states) {
				this.colorSchemeMap.get(associationKind).put(state, scheme);
			}
		}
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
	 * @see #registerColorScheme(SubstanceColorScheme, ComponentState...)
	 * @since version 5.1
	 */
	public SubstanceColorScheme getColorScheme(
			ColorSchemeAssociationKind associationKind,
			ComponentState componentState) {
		if (associationKind == ColorSchemeAssociationKind.FILL)
			return this.getColorScheme(componentState);

		SubstanceColorScheme registered = this.colorSchemeMap.get(
				associationKind).get(componentState);
		if (registered != null)
			return registered;

		ColorSchemeAssociationKind fallback = associationKind.getFallback();
		if (fallback == null)
			return null;

		return getColorScheme(fallback, componentState);
	}

	/**
	 * Registers a border color scheme for the specific component state if the
	 * component state is not <code>null</code>, or a global highlight color
	 * scheme otherwise.
	 * 
	 * @param scheme
	 *            Border color scheme for the specified component state.
	 * @param states
	 *            Component states. If <code>null</code>, the specified color
	 *            scheme will be applied for all states left unspecified.
	 * @deprecated Use
	 *             {@link #registerColorScheme(SubstanceColorScheme, ColorSchemeAssociationKind, ComponentState...)}
	 *             with {@link ColorSchemeAssociationKind#BORDER} instead. This
	 *             method will be removed in version 6.0.
	 */
	@Deprecated
	public void registerBorderColorScheme(SubstanceColorScheme scheme,
			ComponentState... states) {
		this.registerColorScheme(scheme, ColorSchemeAssociationKind.BORDER,
				states);
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
		return this.getColorScheme(ColorSchemeAssociationKind.BORDER,
				componentState);
		// SubstanceColorScheme registered = this.colorSchemeMap.get(
		// ColorSchemeAssociationKind.BORDER).get(componentState);
		// if (registered != null)
		// return registered;
		//
		// return this.getColorScheme(comp, componentState, true);
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		SubstanceColorSchemeBundle clone = new SubstanceColorSchemeBundle(
				this.activeColorScheme, this.defaultColorScheme,
				this.disabledColorScheme);

		for (Map.Entry<ColorSchemeAssociationKind, Map<ComponentState, SubstanceColorScheme>> entry : this.colorSchemeMap
				.entrySet()) {
			clone.colorSchemeMap.get(entry.getKey()).putAll(entry.getValue());
		}

		if (this.stateAlphaMap != null) {
			clone.stateAlphaMap = new EnumMap<ComponentState, Float>(
					this.stateAlphaMap);
		}
		if (this.stateHighlightSchemeAlphaMap != null) {
			clone.stateHighlightSchemeAlphaMap = new EnumMap<ComponentState, Float>(
					this.stateHighlightSchemeAlphaMap);
		}
		return clone;
	}

	/**
	 * Creates a new color scheme bundle that has the same settings as this
	 * color scheme bundle with the addition of applying the specified color
	 * scheme transformation on all the relevant color schemes
	 * 
	 * @param transform
	 *            Color scheme transformation.
	 * @return The new color scheme bundle.
	 */
	public SubstanceColorSchemeBundle transform(ColorSchemeTransform transform) {
		// transform the basic schemes
		SubstanceColorSchemeBundle result = new SubstanceColorSchemeBundle(
				transform.transform(this.activeColorScheme), transform
						.transform(this.defaultColorScheme), transform
						.transform(this.disabledColorScheme));

		for (Map.Entry<ColorSchemeAssociationKind, Map<ComponentState, SubstanceColorScheme>> entry : this.colorSchemeMap
				.entrySet()) {
			for (Map.Entry<ComponentState, SubstanceColorScheme> subEntry : entry
					.getValue().entrySet()) {
				result.colorSchemeMap.get(entry.getKey()).put(
						subEntry.getKey(),
						transform.transform(subEntry.getValue()));
			}
		}

		// alphas are the same
		if (this.stateAlphaMap != null) {
			result.stateAlphaMap = new EnumMap<ComponentState, Float>(
					this.stateAlphaMap);
		}

		// highlight alphas are the same
		if (this.stateHighlightSchemeAlphaMap != null) {
			result.stateHighlightSchemeAlphaMap = new EnumMap<ComponentState, Float>(
					this.stateHighlightSchemeAlphaMap);
		}
		return result;
	}

	/**
	 * Returns the set of all component states that have non-trivial alpha
	 * associated with them. Non-trivial alpha is a value that is strictly less
	 * than 1.0.
	 * 
	 * @return All component states that have associated non-trivial alpha
	 *         values.
	 */
	Set<ComponentState> getStatesWithAlpha() {
		Set<ComponentState> result = EnumSet.noneOf(ComponentState.class);
		for (Map.Entry<ComponentState, Float> alphaEntry : this.stateAlphaMap
				.entrySet()) {
			if (alphaEntry.getValue() < 1.0f) {
				result.add(alphaEntry.getKey());
			}
		}
		return result;
	}
}
