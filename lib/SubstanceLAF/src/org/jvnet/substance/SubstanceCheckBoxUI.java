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
package org.jvnet.substance;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicButtonListener;

import org.jvnet.lafwidget.animation.FadeKind;
import org.jvnet.lafwidget.animation.FadeState;
import org.jvnet.substance.api.*;
import org.jvnet.substance.painter.border.SubstanceBorderPainter;
import org.jvnet.substance.painter.gradient.SubstanceGradientPainter;
import org.jvnet.substance.utils.*;

/**
 * UI for check boxes in <b>Substance</b> look and feel.
 * 
 * @author Kirill Grouchnikov
 */
public class SubstanceCheckBoxUI extends SubstanceRadioButtonUI {
	/**
	 * Prefix for the checkbox-related properties in the {@link UIManager}.
	 */
	private final static String propertyPrefix = "CheckBox" + ".";

	// /**
	// * Listener for fade animations.
	// */
	// protected FadeStateListener substanceFadeStateListener;
	//
	// /**
	// * Property change listener. Listens on changes to
	// * {@link AbstractButton#MODEL_CHANGED_PROPERTY} property.
	// */
	// protected PropertyChangeListener substancePropertyListener;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.ComponentUI#createUI(javax.swing.JComponent)
	 */
	public static ComponentUI createUI(JComponent comp) {
		SubstanceCoreUtilities.testComponentCreationThreadingViolation(comp);
		return new SubstanceCheckBoxUI((JToggleButton) comp);
	}

	/**
	 * Hash map for storing icons.
	 */
	private static LazyResettableHashMap<Icon> icons = new LazyResettableHashMap<Icon>(
			"SubstanceCheckBoxUI");

	/**
	 * Simple constructor.
	 * 
	 * @param button
	 *            The associated button.
	 */
	public SubstanceCheckBoxUI(JToggleButton button) {
		super(button);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.basic.BasicRadioButtonUI#getPropertyPrefix()
	 */
	@Override
	protected String getPropertyPrefix() {
		return propertyPrefix;
	}

	// /*
	// * (non-Javadoc)
	// *
	// * @seejavax.swing.plaf.basic.BasicButtonUI#installListeners(javax.swing.
	// * AbstractButton)
	// */
	// @Override
	// protected void installListeners(final AbstractButton b) {
	// super.installListeners(b);
	// this.substanceFadeStateListener = new FadeStateListener(b,
	// b.getModel(), SubstanceCoreUtilities.getFadeCallback(b, false));
	// this.substanceFadeStateListener.registerListeners();
	//
	// this.substancePropertyListener = new PropertyChangeListener() {
	// public void propertyChange(PropertyChangeEvent evt) {
	// if (AbstractButton.MODEL_CHANGED_PROPERTY.equals(evt
	// .getPropertyName())) {
	// if (substanceFadeStateListener != null)
	// substanceFadeStateListener.unregisterListeners();
	// substanceFadeStateListener = new FadeStateListener(b, b
	// .getModel(), SubstanceCoreUtilities
	// .getFadeCallback(b, false));
	// substanceFadeStateListener.registerListeners();
	// }
	// }
	// };
	// b.addPropertyChangeListener(this.substancePropertyListener);
	// }
	//
	// /*
	// * (non-Javadoc)
	// *
	// *
	// @seejavax.swing.plaf.basic.BasicButtonUI#uninstallListeners(javax.swing.
	// * AbstractButton)
	// */
	// @Override
	// protected void uninstallListeners(AbstractButton b) {
	// this.substanceFadeStateListener.unregisterListeners();
	// this.substanceFadeStateListener = null;
	//
	// b.removePropertyChangeListener(this.substancePropertyListener);
	// this.substancePropertyListener = null;
	//
	// super.uninstallListeners(b);
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jvnet.substance.SubstanceRadioButtonUI#installDefaults(javax.swing
	 * .AbstractButton)
	 */
	@Override
	protected void installDefaults(AbstractButton b) {
		super.installDefaults(b);

		button.setRolloverEnabled(true);

		Border border = b.getBorder();
		if (border == null || border instanceof UIResource) {
			b.setBorder(SubstanceSizeUtils.getCheckBoxBorder(SubstanceSizeUtils
					.getComponentFontSize(b)));
		}
	}

	/**
	 * Returns the icon that matches the current and previous states of the
	 * checkbox.
	 * 
	 * @param button
	 *            Button (should be {@link JCheckBox}).
	 * @param currState
	 *            Current state of the checkbox.
	 * @param prevState
	 *            Previous state of the checkbox.
	 * @return Matching icon.
	 */
	private static Icon getIcon(JToggleButton button, ComponentState currState,
			ComponentState prevState) {
		float checkMarkVisibility = currState.isKindActive(FadeKind.SELECTION) ? 10
				: 0;
		boolean isCheckMarkFadingOut = false;

		SubstanceColorScheme currFillColorScheme = SubstanceColorSchemeUtilities
				.getColorScheme(button, ColorSchemeAssociationKind.FILL,
						currState);
		SubstanceColorScheme prevFillColorScheme = SubstanceColorSchemeUtilities
				.getColorScheme(button, ColorSchemeAssociationKind.FILL,
						prevState);

		SubstanceColorScheme currMarkColorScheme = SubstanceColorSchemeUtilities
				.getColorScheme(button, ColorSchemeAssociationKind.MARK,
						currState);
		SubstanceColorScheme prevMarkColorScheme = SubstanceColorSchemeUtilities
				.getColorScheme(button, ColorSchemeAssociationKind.MARK,
						prevState);

		SubstanceColorScheme currBorderColorScheme = SubstanceColorSchemeUtilities
				.getColorScheme(button, ColorSchemeAssociationKind.BORDER,
						currState);
		SubstanceColorScheme prevBorderColorScheme = SubstanceColorSchemeUtilities
				.getColorScheme(button, ColorSchemeAssociationKind.BORDER,
						prevState);

		float cyclePos = 0;
		FadeState fadeState = SubstanceFadeUtilities.getFadeState(button,
				FadeKind.SELECTION, FadeKind.ROLLOVER, FadeKind.PRESS);
		if (fadeState != null) {
			cyclePos = fadeState.getFadePosition();
			if (fadeState.isFadingIn())
				cyclePos = 1.0f - cyclePos;
			if (fadeState.fadeKind == FadeKind.SELECTION) {
				checkMarkVisibility = fadeState.getFadePosition();
				isCheckMarkFadingOut = !fadeState.isFadingIn();
			}
		}

		int fontSize = SubstanceSizeUtils.getComponentFontSize(button);
		int checkMarkSize = SubstanceSizeUtils.getCheckBoxMarkSize(fontSize);

		SubstanceGradientPainter fillPainter = SubstanceCoreUtilities
				.getGradientPainter(button);
		SubstanceBorderPainter borderPainter = SubstanceCoreUtilities
				.getBorderPainter(button);

		// System.out.println(prevState + " -> [" + cyclePos + "] " +
		// currState);
		// System.out.println("\t" + prevBorderColorScheme.getDisplayName()
		// + " -> " + currBorderColorScheme.getDisplayName());

		HashMapKey key = SubstanceCoreUtilities.getHashKey(fontSize,
				checkMarkSize, currState.name(), prevState.name(), fillPainter
						.getDisplayName(), borderPainter.getDisplayName(),
				currFillColorScheme.getDisplayName(), prevFillColorScheme
						.getDisplayName(),
				currMarkColorScheme.getDisplayName(), prevMarkColorScheme
						.getDisplayName(), currBorderColorScheme
						.getDisplayName(), prevBorderColorScheme
						.getDisplayName(), cyclePos, checkMarkVisibility,
				isCheckMarkFadingOut);

		Icon result = SubstanceCheckBoxUI.icons.get(key);
		if (result != null)
			return result;
		result = new ImageIcon(SubstanceImageCreator.getCheckBox(button,
				fillPainter, borderPainter, checkMarkSize, currState,
				prevState, currFillColorScheme, prevFillColorScheme,
				currMarkColorScheme, prevMarkColorScheme,
				currBorderColorScheme, prevBorderColorScheme, cyclePos,
				checkMarkVisibility, isCheckMarkFadingOut));

		SubstanceCheckBoxUI.icons.put(key, result);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.plaf.basic.BasicButtonUI#createButtonListener(javax.swing
	 * .AbstractButton)
	 */
	@Override
	protected BasicButtonListener createButtonListener(AbstractButton b) {
		return new RolloverButtonListener(b);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.basic.BasicRadioButtonUI#getDefaultIcon()
	 */
	@Override
	public Icon getDefaultIcon() {
		ComponentState currState = ComponentState.getState(this.button);
		ComponentState prevState = SubstanceCoreUtilities
				.getPrevComponentState(this.button);
		return SubstanceCheckBoxUI.getIcon(this.button, currState, prevState);
	}

	/**
	 * Returns memory usage string.
	 * 
	 * @return Memory usage string.
	 */
	public static String getMemoryUsage() {
		StringBuffer sb = new StringBuffer();
		sb.append("SubstanceCheckBox: \n");
		sb.append("\t" + SubstanceCheckBoxUI.icons.size() + " icons");
		return sb.toString();
	}
}
