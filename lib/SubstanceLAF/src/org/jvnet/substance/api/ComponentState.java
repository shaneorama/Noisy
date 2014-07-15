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

import java.util.LinkedList;
import java.util.List;

import javax.swing.*;

import org.jvnet.lafwidget.animation.FadeKind;
import org.jvnet.substance.colorscheme.*;

/**
 * <p>
 * This enum is used in order to provide uniform transition effects on mouse
 * events. The effects include different visual appearance of the corresponding
 * control when the mouse hovers over it (rollover), when it's pressed or
 * selected, disabled etc.
 * </p>
 * 
 * <p>
 * Each enum value represents a single state and contains information that is
 * used by the UI delegates in order to correctly paint the corresponding
 * controls.
 * </p>
 * 
 * @author Kirill Grouchnikov
 */
@SubstanceApi
public enum ComponentState {
	/**
	 * Disabled active. Used for disabled buttons that have been marked as
	 * <code>default</code>.
	 */
	DISABLED_ACTIVE(ColorSchemeKind.DISABLED, 0.0f),

	/**
	 * Active. Used for enabled buttons that have been marked as
	 * <code>default</code>.
	 */
	ACTIVE(ColorSchemeKind.CURRENT, 0.0f, FadeKind.ENABLE),

	/**
	 * Disabled selected.
	 */
	DISABLED_SELECTED(ColorSchemeKind.DISABLED, 1.0f, FadeKind.SELECTION),

	/**
	 * Disabled and not selected.
	 */
	DISABLED_UNSELECTED(ColorSchemeKind.DISABLED, 0.0f),

	/**
	 * Pressed selected.
	 */
	PRESSED_SELECTED(ColorSchemeKind.CURRENT, 0.5f, FadeKind.ENABLE,
			FadeKind.SELECTION, FadeKind.PRESS),

	/**
	 * Pressed and not selected.
	 */
	PRESSED_UNSELECTED(ColorSchemeKind.CURRENT, 0.8f, FadeKind.ENABLE,
			FadeKind.PRESS),

	/**
	 * Selected.
	 */
	SELECTED(ColorSchemeKind.CURRENT, 0.8f, FadeKind.ENABLE, FadeKind.SELECTION),

	/**
	 * Selected and rolled over.
	 */
	ROLLOVER_SELECTED(ColorSchemeKind.CURRENT, 0.4f, FadeKind.ENABLE,
			FadeKind.ROLLOVER, FadeKind.SELECTION),

	/**
	 * Armed.
	 */
	ARMED(ColorSchemeKind.CURRENT, 0.5f, FadeKind.ENABLE, FadeKind.ARM),

	/**
	 * Armed and rolled over.
	 */
	ROLLOVER_ARMED(ColorSchemeKind.CURRENT, 0.7f, FadeKind.ENABLE,
			FadeKind.ROLLOVER, FadeKind.ARM),

	/**
	 * Not selected and rolled over.
	 */
	ROLLOVER_UNSELECTED(ColorSchemeKind.CURRENT, 0.3f, FadeKind.ENABLE,
			FadeKind.ROLLOVER),

	/**
	 * Default state.
	 */
	DEFAULT(ColorSchemeKind.REGULAR, 0.0f, FadeKind.ENABLE);

	/**
	 * Enum for color scheme kind. Is used in order to decouple the actual
	 * current color scheme and the decision on whether to use it.
	 * 
	 * @author Kirill Grouchnikov
	 */
	public static enum ColorSchemeKind {
		/**
		 * Current color scheme (e.g. {@link AquaColorScheme}).
		 */
		CURRENT,

		/**
		 * Regular color scheme (usually {@link MetallicColorScheme}).
		 */
		REGULAR,

		/**
		 * Disabled color scheme (usually {@link LightGrayColorScheme}).
		 */
		DISABLED
	}

	/**
	 * The corresponding color scheme kind.
	 */
	private ColorSchemeKind colorSchemeKind;

	/**
	 * The corresponding cycle count. Should be a number between 0.0 and 1.0.
	 * This number is used to compute the foreground color of some component.
	 * The color is interpolated between two values (0.0 corresponds to usual
	 * color, 1.0 corresponds to very light version of the usual color).
	 */
	private float cyclePos;

	/**
	 * Active fade kinds for this state. For example, {@link #ROLLOVER_SELECTED}
	 * contains {@link FadeKind#ROLLOVER} and {@link FadeKind#SELECTION}.
	 */
	private transient FadeKind[] activeKinds;

	/**
	 * Simple constructor.
	 * 
	 * @param kind
	 *            The corresponding color scheme kind.
	 * @param cyclePos
	 *            The corresponding cycle count.
	 * @param activeKinds
	 *            Indicates active fade kinds for this state. For example,
	 *            {@link #ROLLOVER_SELECTED} should pass both
	 *            {@link FadeKind#ROLLOVER} and {@link FadeKind#SELECTION}.
	 */
	ComponentState(ColorSchemeKind kind, float cyclePos,
			FadeKind... activeKinds) {
		colorSchemeKind = kind;
		this.cyclePos = cyclePos;
		this.activeKinds = activeKinds;
	}

	/**
	 * Returns the corresponding color scheme kind
	 * 
	 * @return Corresponding color scheme kind
	 */
	public ColorSchemeKind getColorSchemeKind() {
		return colorSchemeKind;
	}

	/**
	 * Returns the corresponding cycle count.
	 * 
	 * @return Corresponding cycle count.
	 */
	public float getCyclePosition() {
		return this.cyclePos;
	}

	/**
	 * Returns indication whether <code>this</code> component state is "active"
	 * under the specified fade kind. For example, {@link #ROLLOVER_SELECTED}
	 * will return <code>true</code> for both {@link FadeKind#ROLLOVER} and
	 * {@link FadeKind#SELECTION}.
	 * 
	 * @param fadeKind
	 *            Fade kind.
	 * @return <code>true</code> if <code>this</code> component state is
	 *         "active" under the specified fade kind (for example,
	 *         {@link #ROLLOVER_SELECTED} will return <code>true</code> for both
	 *         {@link FadeKind#ROLLOVER} and {@link FadeKind#SELECTION}),
	 *         <code>false</code> otherwise.
	 */
	public boolean isKindActive(FadeKind fadeKind) {
		if (activeKinds == null)
			return false;
		for (FadeKind fk : activeKinds)
			if (fadeKind == fk)
				return true;
		return false;
	}

	/**
	 * Returns all active component states.
	 * 
	 * @return All active component states.
	 */
	public static ComponentState[] getActiveStates() {
		List<ComponentState> states = new LinkedList<ComponentState>();
		for (ComponentState state : ComponentState.values()) {
			if (state == ComponentState.DEFAULT)
				continue;
			if (!state.isKindActive(FadeKind.ENABLE))
				continue;
			states.add(state);
		}
		return states.toArray(new ComponentState[0]);
	}

	/**
	 * Retrieves component state based on the button model (required parameter)
	 * and component itself (optional parameter).
	 * 
	 * @param model
	 *            Button model (required).
	 * @param component
	 *            Component (optional).
	 * @return The matching component state.
	 */
	public static ComponentState getState(ButtonModel model,
			JComponent component) {
		return getState(model, component, false);
	}

	/**
	 * Returns the state of the specified button.
	 * 
	 * @param button
	 *            Button.
	 * @return The state of the specified button.
	 */
	public static ComponentState getState(AbstractButton button) {
		return getState(button.getModel(), button, false);
	}

	/**
	 * Retrieves component state based on the button model (required parameter)
	 * and button itself (optional parameter).
	 * 
	 * @param model
	 *            Button model (required).
	 * @param component
	 *            Component (optional).
	 * @param toIgnoreSelection
	 *            If <code>true</code>, the {@link ButtonModel#isSelected()}
	 *            will not be checked. This can be used for tracking transitions
	 *            on menu items that use <code>armed</code> state instead, when
	 *            we don't want to use different rollover themes for selected
	 *            and unselected checkbox and radio button menu items (to
	 *            preserve consistent visual appearence of highlights).
	 * @return The matching component state.
	 */
	public static ComponentState getState(ButtonModel model,
			JComponent component, boolean toIgnoreSelection) {
		// if (!SwingUtilities.isEventDispatchThread())
		// throw new IllegalArgumentException("Accessing outside EDT");

		boolean isRollover = model.isRollover();

		// fix for defect 103 - no rollover effects on menu items
		// that are not in the selected menu path
		if (component instanceof MenuElement) {
			MenuElement[] selectedMenuPath = MenuSelectionManager
					.defaultManager().getSelectedPath();
			for (MenuElement elem : selectedMenuPath) {
				if (elem == component) {
					isRollover = true;
					break;
				}
			}
		}

		if (component != null) {
			if (component instanceof JButton) {
				JButton jb = (JButton) component;
				if (jb.isDefaultButton()) {
					if (model.isEnabled()) {
						// check for rollover
						if (jb.isRolloverEnabled()
								&& jb.getModel().isRollover()) {
							if (model.isSelected())
								return ROLLOVER_SELECTED;
							else
								return ROLLOVER_UNSELECTED;
						}
						if ((!model.isPressed()) && (!model.isArmed()))
							return ACTIVE;
					} else
						return DISABLED_ACTIVE;
				}
			}
		}

		boolean isRolloverEnabled = true;
		if (component instanceof AbstractButton)
			isRolloverEnabled = ((AbstractButton) component)
					.isRolloverEnabled();
		if (!model.isEnabled()) {
			if (model.isSelected())
				return DISABLED_SELECTED;
			return DISABLED_UNSELECTED;
		} else if (model.isArmed() && model.isPressed()) {
			if (model.isSelected())
				return PRESSED_SELECTED;
			return PRESSED_UNSELECTED;
		} else if (!toIgnoreSelection && model.isSelected()) {
			if (((component == null) || isRolloverEnabled) && isRollover)
				return ROLLOVER_SELECTED;
			return SELECTED;
		} else if (model.isArmed()) {
			if (((component == null) || isRolloverEnabled) && isRollover)
				return ROLLOVER_ARMED;
			return ARMED;
		} else if (((component == null) || isRolloverEnabled) && isRollover)
			return ROLLOVER_UNSELECTED;

		return DEFAULT;
	}

	/**
	 * Returns the component state that matches the specified parameters.
	 * 
	 * @param isEnabled
	 *            Enabled flag.
	 * @param isRollover
	 *            Rollover flag.
	 * @param isSelected
	 *            Selected flag.
	 * @return The component state that matches the specified parameters.
	 */
	public static ComponentState getState(boolean isEnabled,
			boolean isRollover, boolean isSelected) {
		if (!isEnabled) {
			if (isSelected)
				return DISABLED_SELECTED;
			return DISABLED_UNSELECTED;
		}
		if (isSelected) {
			if (isRollover)
				return ROLLOVER_SELECTED;
			return SELECTED;
		}
		if (isRollover)
			return ROLLOVER_UNSELECTED;
		return DEFAULT;
	}

}
