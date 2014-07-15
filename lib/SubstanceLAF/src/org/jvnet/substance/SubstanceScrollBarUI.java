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

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.EnumSet;
import java.util.Set;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicScrollBarUI;

import org.jvnet.lafwidget.animation.*;
import org.jvnet.lafwidget.layout.TransitionLayout;
import org.jvnet.substance.api.*;
import org.jvnet.substance.api.SubstanceConstants.ScrollPaneButtonPolicyKind;
import org.jvnet.substance.api.SubstanceConstants.Side;
import org.jvnet.substance.painter.border.SimplisticSoftBorderPainter;
import org.jvnet.substance.painter.border.SubstanceBorderPainter;
import org.jvnet.substance.painter.gradient.SimplisticGradientPainter;
import org.jvnet.substance.painter.gradient.SubstanceGradientPainter;
import org.jvnet.substance.painter.utils.BackgroundPaintingUtils;
import org.jvnet.substance.shaper.ClassicButtonShaper;
import org.jvnet.substance.shaper.SubstanceButtonShaper;
import org.jvnet.substance.utils.*;
import org.jvnet.substance.utils.icon.ArrowButtonTransitionAwareIcon;
import org.jvnet.substance.utils.scroll.SubstanceScrollButton;

/**
 * UI for scroll bars in <b>Substance </b> look and feel.
 * 
 * @author Kirill Grouchnikov
 */
public class SubstanceScrollBarUI extends BasicScrollBarUI implements Trackable {
	/**
	 * The second decrease button. Is shown under
	 * {@link SubstanceConstants.ScrollPaneButtonPolicyKind#ADJACENT},
	 * {@link SubstanceConstants.ScrollPaneButtonPolicyKind#MULTIPLE} and
	 * {@link SubstanceConstants.ScrollPaneButtonPolicyKind#MULTIPLE_BOTH}
	 * modes.
	 * 
	 * @since version 3.1
	 */
	protected JButton mySecondDecreaseButton;

	/**
	 * The second increase button. Is shown only under
	 * {@link SubstanceConstants.ScrollPaneButtonPolicyKind#MULTIPLE_BOTH} mode.
	 * 
	 * @since version 3.1
	 */
	protected JButton mySecondIncreaseButton;

	/**
	 * Surrogate button model for tracking the thumb transitions.
	 */
	private ButtonModel thumbModel;

	/**
	 * Stores computed images for vertical thumbs.
	 */
	private static LazyResettableHashMap<BufferedImage> thumbVerticalMap = new LazyResettableHashMap<BufferedImage>(
			"SubstanceScrollBarUI.thumbVertical");

	/**
	 * Stores computed images for horizontal thumbs.
	 */
	private static LazyResettableHashMap<BufferedImage> thumbHorizontalMap = new LazyResettableHashMap<BufferedImage>(
			"SubstanceScrollBarUI.thumbHorizontal");

	/**
	 * Stores computed images for full vertical tracks under
	 * {@link DefaultControlBackgroundComposite}.
	 */
	private static LazyResettableHashMap<BufferedImage> trackFullVerticalMap = new LazyResettableHashMap<BufferedImage>(
			"SubstanceScrollBarUI.trackFullVertical");

	/**
	 * Stores computed images for full horizontal tracks under
	 * {@link DefaultControlBackgroundComposite}.
	 */
	private static LazyResettableHashMap<BufferedImage> trackFullHorizontalMap = new LazyResettableHashMap<BufferedImage>(
			"SubstanceScrollBarUI.trackFullHorizontal");

	/**
	 * Mouse listener on the associated scroll bar.
	 */
	private MouseListener substanceMouseListener;

	/**
	 * Listener for thumb fade animations.
	 */
	private RolloverControlListener substanceThumbRolloverListener;

	/**
	 * Listener for fade animations.
	 */
	protected FadeStateListener substanceFadeStateListener;

	/**
	 * Property change listener.
	 * 
	 */
	private PropertyChangeListener substancePropertyListener;

	/**
	 * Scroll bar width.
	 */
	protected int scrollBarWidth;

	/**
	 * Cache of images for horizontal tracks.
	 */
	private static LazyResettableHashMap<BufferedImage> trackHorizontalMap = new LazyResettableHashMap<BufferedImage>(
			"SubstanceScrollBarUI.trackHorizontal");

	/**
	 * Cache of images for vertical tracks.
	 */
	private static LazyResettableHashMap<BufferedImage> trackVerticalMap = new LazyResettableHashMap<BufferedImage>(
			"SubstanceScrollBarUI.trackVertical");

	/**
	 * Listener on adjustments made to the scrollbar model - this is for the
	 * overlay mode (see {@link SubstanceLookAndFeel#OVERLAY_PROPERTY} and
	 * repaiting both scrollbars with the viewport.
	 * 
	 * @since version 3.2
	 */
	protected AdjustmentListener substanceAdjustmentListener;

	/**
	 * Surrogate model to sync between rollover effects of scroll buttons and
	 * scroll track / scroll thumb.
	 * 
	 * @since version 3.2
	 */
	protected CompositeButtonModel compositeScrollTrackModel;

	/**
	 * Surrogate model to sync between rollover effects of scroll buttons and
	 * scroll track / scroll thumb.
	 * 
	 * @since version 3.2
	 */
	protected CompositeButtonModel compositeButtonsModel;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.ComponentUI#createUI(javax.swing.JComponent)
	 */
	public static ComponentUI createUI(JComponent comp) {
		SubstanceCoreUtilities.testComponentCreationThreadingViolation(comp);
		return new SubstanceScrollBarUI(comp);
	}

	/**
	 * Simple constructor.
	 * 
	 * @param b
	 *            Associated component.
	 */
	protected SubstanceScrollBarUI(JComponent b) {
		super();
		this.thumbModel = new DefaultButtonModel();
		this.thumbModel.setArmed(false);
		this.thumbModel.setSelected(false);
		this.thumbModel.setPressed(false);
		this.thumbModel.setRollover(false);

		b.setOpaque(false);
	}

	/**
	 * Creates a decrease button.
	 * 
	 * @param orientation
	 *            Button orientation.
	 * @param isRegular
	 *            if <code>true</code>, the regular (upper / left) decrease
	 *            button is created, if <code>false</code>, the additional
	 *            (lower / right) decrease button is created for
	 *            {@link SubstanceConstants.ScrollPaneButtonPolicyKind#ADJACENT}
	 *            ,
	 *            {@link SubstanceConstants.ScrollPaneButtonPolicyKind#MULTIPLE}
	 *            and
	 *            {@link SubstanceConstants.ScrollPaneButtonPolicyKind#MULTIPLE_BOTH}
	 *            kinds.
	 * @return Decrease button.
	 */
	protected JButton createGeneralDecreaseButton(final int orientation,
			boolean isRegular) {
		JButton result = new SubstanceScrollButton(orientation);
		result.setFont(this.scrollbar.getFont());
		Icon icon = new ArrowButtonTransitionAwareIcon(result, orientation);
		// new TransitionAwareIcon(result,
		// new TransitionAwareIcon.Delegate() {
		// public Icon getColorSchemeIcon(SubstanceColorScheme scheme) {
		// return SubstanceImageCreator.getArrowIcon(
		// SubstanceSizeUtils
		// .getComponentFontSize(scrollbar),
		// orientation, scheme);
		// }
		// });
		result.setIcon(icon);
		result.setFont(scrollbar.getFont());

		result.setPreferredSize(new Dimension(this.scrollBarWidth,
				this.scrollBarWidth));

		Set<Side> openSides = EnumSet.noneOf(Side.class);
		Set<Side> straightSides = EnumSet.noneOf(Side.class);
		switch (orientation) {
		case NORTH:
			openSides.add(Side.BOTTOM);
			if (!isRegular)
				openSides.add(Side.TOP);
			if (isRegular)
				straightSides.add(Side.TOP);
			break;
		case EAST:
			openSides.add(Side.LEFT);
			if (!isRegular)
				openSides.add(Side.RIGHT);
			if (isRegular)
				straightSides.add(Side.RIGHT);
			break;
		case WEST:
			openSides.add(Side.RIGHT);
			if (!isRegular)
				openSides.add(Side.LEFT);
			if (isRegular)
				straightSides.add(Side.LEFT);
			break;
		}
		result.putClientProperty(
				SubstanceLookAndFeel.BUTTON_OPEN_SIDE_PROPERTY, openSides);
		result.putClientProperty(SubstanceLookAndFeel.BUTTON_SIDE_PROPERTY,
				straightSides);

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.basic.BasicScrollBarUI#createDecreaseButton(int)
	 */
	@Override
	protected JButton createDecreaseButton(int orientation) {
		return this.createGeneralDecreaseButton(orientation, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.basic.BasicScrollBarUI#createIncreaseButton(int)
	 */
	@Override
	protected JButton createIncreaseButton(int orientation) {
		return this.createGeneralIncreaseButton(orientation, true);
	}

	/**
	 * Creates a increase button.
	 * 
	 * @param orientation
	 *            Button orientation.
	 * @param isRegular
	 *            if <code>true</code>, the regular (lower / right) increase
	 *            button is created, if <code>false</code>, the additional
	 *            (upper / left) increase button is created for
	 *            {@link SubstanceConstants.ScrollPaneButtonPolicyKind#MULTIPLE_BOTH}
	 *            kind.
	 * @return Increase button.
	 */
	protected JButton createGeneralIncreaseButton(final int orientation,
			boolean isRegular) {
		JButton result = new SubstanceScrollButton(orientation);
		result.setFont(this.scrollbar.getFont());
		Icon icon = new ArrowButtonTransitionAwareIcon(result, orientation);
		// Icon icon = new TransitionAwareIcon(result,
		// new TransitionAwareIcon.Delegate() {
		// public Icon getColorSchemeIcon(SubstanceColorScheme scheme) {
		// return SubstanceImageCreator.getArrowIcon(
		// SubstanceSizeUtils
		// .getComponentFontSize(scrollbar),
		// orientation, scheme);
		// }
		// });
		result.setIcon(icon);
		result.setFont(scrollbar.getFont());
		// JButton result = new SubstanceScrollBarButton(icon, orientation);
		result.setPreferredSize(new Dimension(this.scrollBarWidth,
				this.scrollBarWidth));

		Set<Side> openSides = EnumSet.noneOf(Side.class);
		Set<Side> straightSides = EnumSet.noneOf(Side.class);
		switch (orientation) {
		case SOUTH:
			openSides.add(Side.TOP);
			if (!isRegular)
				openSides.add(Side.BOTTOM);
			if (isRegular)
				straightSides.add(Side.BOTTOM);
			break;
		case EAST:
			openSides.add(Side.LEFT);
			if (!isRegular)
				openSides.add(Side.RIGHT);
			if (isRegular)
				straightSides.add(Side.RIGHT);
			break;
		case WEST:
			openSides.add(Side.RIGHT);
			if (!isRegular)
				openSides.add(Side.LEFT);
			if (isRegular)
				straightSides.add(Side.LEFT);
			break;
		}
		result.putClientProperty(
				SubstanceLookAndFeel.BUTTON_OPEN_SIDE_PROPERTY, openSides);
		result.putClientProperty(SubstanceLookAndFeel.BUTTON_SIDE_PROPERTY,
				straightSides);
		return result;
	}

	/**
	 * Returns the image for a horizontal track.
	 * 
	 * @param trackBounds
	 *            Track bounds.
	 * @param leftActiveButton
	 *            The closest left button in the scroll bar. May be
	 *            <code>null</code>.
	 * @param rightActiveButton
	 *            The closest right button in the scroll bar. May be
	 *            <code>null</code> .
	 * @return Horizontal track image.
	 */
	private BufferedImage getTrackHorizontal(Rectangle trackBounds,
			SubstanceScrollButton leftActiveButton,
			SubstanceScrollButton rightActiveButton) {
		int width = Math.max(1, trackBounds.width);
		int height = Math.max(1, trackBounds.height);

		ComponentState compLeftState = this.getState(leftActiveButton);
		ComponentState compRightState = this.getState(rightActiveButton);

		Component tracked = SubstanceFadeUtilities.getTracked(
				FadeKind.ROLLOVER, this.scrollbar, this.decrButton,
				this.incrButton, this.mySecondDecreaseButton,
				this.mySecondIncreaseButton);
		if (tracked != null) {

			ComponentState state = (tracked == this.scrollbar) ? ComponentState
					.getState(this.thumbModel, null) : ComponentState.getState(
					((AbstractButton) tracked).getModel(), null);

			float cyclePos = state.getCyclePosition();
			FadeState highest = SubstanceFadeUtilities
					.getFadeStateWithHighestFadeCycle(FadeKind.ROLLOVER,
							this.scrollbar, this.decrButton, this.incrButton,
							this.mySecondDecreaseButton,
							this.mySecondIncreaseButton);
			if (highest != null) {
				cyclePos = highest.getFadePosition();
				if (!highest.isFadingIn())
					cyclePos = 1.0f - cyclePos;
			}

			SubstanceButtonShaper shaper = SubstanceCoreUtilities
					.getButtonShaper(this.scrollbar);
			HashMapKey key = SubstanceCoreUtilities
					.getHashKey(cyclePos, width, height,
							((leftActiveButton == null) ? "null"
									: ComponentState.getState(
											leftActiveButton.getModel(),
											leftActiveButton).name()),
							((leftActiveButton == null) ? "null"
									: SubstanceCoreUtilities
											.getPrevComponentState(
													leftActiveButton).name()),
							((rightActiveButton == null) ? "null"
									: ComponentState.getState(
											rightActiveButton.getModel(),
											rightActiveButton).name()),
							((rightActiveButton == null) ? "null"
									: SubstanceCoreUtilities
											.getPrevComponentState(
													rightActiveButton).name()),
							((compLeftState == null) ? "null" : compLeftState
									.name()),
							((compRightState == null) ? "null" : compRightState
									.name()), ((compLeftState == null) ? "null"
									: SubstanceColorSchemeUtilities
											.getColorScheme(leftActiveButton,
													compLeftState)
											.getDisplayName()),
							((compRightState == null) ? "null"
									: SubstanceColorSchemeUtilities
											.getColorScheme(rightActiveButton,
													compRightState)
											.getDisplayName()),
							SubstanceColorSchemeUtilities.getColorScheme(
									this.scrollbar, ComponentState.DEFAULT)
									.getDisplayName(), shaper.getDisplayName(),
							SubstanceSizeUtils
									.getBorderStrokeWidth(SubstanceSizeUtils
											.getComponentFontSize(scrollbar)));
			// System.out.println(key);
			if (trackFullHorizontalMap.containsKey(key)) {
				// System.out.println("Cache hit");
				return trackFullHorizontalMap.get(key);
			}
			// System.out.println("Cache miss");

			// System.out.println("New image for horizontal track");
			BufferedImage imageBack = getTrackBackHorizontal(this.scrollbar,
					leftActiveButton, rightActiveButton, width, height);
			Graphics2D backGraphics = imageBack.createGraphics();

			BufferedImage imageDefault = getTrackHorizontal(this.scrollbar,
					compLeftState, compRightState, width, height);

			backGraphics.drawImage(imageDefault, 0, 0, null);
			BufferedImage imageActive = getTrackHorizontal(this.scrollbar,
					compLeftState, compRightState, width, height);
			backGraphics.setComposite(AlphaComposite.SrcOver.derive(cyclePos));
			// System.out.println("Painting " + cyclePos);
			backGraphics.drawImage(imageActive, 0, 0, null);

			trackFullHorizontalMap.put(key, imageBack);
			return imageBack;
		}

		SubstanceButtonShaper shaper = SubstanceCoreUtilities
				.getButtonShaper(this.scrollbar);
		HashMapKey key = SubstanceCoreUtilities
				.getHashKey(width, height, ((compLeftState == null) ? "null"
						: ComponentState.getState(leftActiveButton.getModel(),
								leftActiveButton).name()),
						((compLeftState == null) ? "null"
								: SubstanceCoreUtilities.getPrevComponentState(
										leftActiveButton).name()),
						((compRightState == null) ? "null" : ComponentState
								.getState(rightActiveButton.getModel(),
										rightActiveButton).name()),
						((compRightState == null) ? "null"
								: SubstanceCoreUtilities.getPrevComponentState(
										rightActiveButton).name()),
						((compLeftState == null) ? "null" : compLeftState
								.name()), ((compRightState == null) ? "null"
								: compRightState.name()),
						((compLeftState == null) ? "null"
								: SubstanceColorSchemeUtilities.getColorScheme(
										leftActiveButton, compLeftState)
										.getDisplayName()),
						((compRightState == null) ? "null"
								: SubstanceColorSchemeUtilities.getColorScheme(
										rightActiveButton, compRightState)
										.getDisplayName()),
						SubstanceColorSchemeUtilities.getColorScheme(
								this.scrollbar, ComponentState.DEFAULT)
								.getDisplayName(), shaper.getDisplayName(),
						SubstanceSizeUtils
								.getBorderStrokeWidth(SubstanceSizeUtils
										.getComponentFontSize(scrollbar)));

		// System.out.println(key);
		if (trackFullHorizontalMap.containsKey(key)) {
			// System.out.println("Cache hit");
			return trackFullHorizontalMap.get(key);
		}
		// System.out.println("Cache miss");

		// System.out.println("New image for horizontal track");
		BufferedImage trackBack = getTrackBackHorizontal(this.scrollbar,
				leftActiveButton, rightActiveButton, width, height);
		Graphics2D backGraphics = trackBack.createGraphics();

		BufferedImage scrollTrackImage = getTrackHorizontal(this.scrollbar,
				compLeftState, compRightState, width, height);
		backGraphics.drawImage(scrollTrackImage, 0, 0, null);
		backGraphics.dispose();

		// System.out.println("Cache update");
		trackFullHorizontalMap.put(key, trackBack);
		return trackBack;
	}

	/**
	 * Returns the image for a horizontal track.
	 * 
	 * @param scrollBar
	 *            Scroll bar.
	 * @param trackBounds
	 *            Track bounds.
	 * @param compLeftState
	 *            The state of the left button in the scroll bar.
	 * @param compRightState
	 *            The state of the closest right button in the scroll bar.
	 * @param width
	 *            Scroll track width.
	 * @param height
	 *            Scroll track height.
	 * @param graphicsComposite
	 *            Composite to apply before painting the track.
	 * @return Horizontal track image.
	 */
	private static BufferedImage getTrackHorizontal(JScrollBar scrollBar,
			ComponentState compLeftState, ComponentState compRightState,
			int width, int height) {
		SubstanceButtonShaper shaper = SubstanceCoreUtilities
				.getButtonShaper(scrollBar);
		SubstanceColorScheme mainScheme = SubstanceColorSchemeUtilities
				.getColorScheme(scrollBar,
						scrollBar.isEnabled() ? ComponentState.DEFAULT
								: ComponentState.DISABLED_UNSELECTED);
		SubstanceColorScheme mainBorderScheme = SubstanceColorSchemeUtilities
				.getColorScheme(scrollBar, ColorSchemeAssociationKind.BORDER,
						scrollBar.isEnabled() ? ComponentState.DEFAULT
								: ComponentState.DISABLED_UNSELECTED);
		HashMapKey key = SubstanceCoreUtilities.getHashKey(mainScheme
				.getDisplayName(), mainBorderScheme.getDisplayName(), width,
				height, ((compLeftState == null) ? "null" : compLeftState
						.name()), ((compRightState == null) ? "null"
						: compRightState.name()), shaper.getDisplayName());
		float radius = height / 2;
		if (shaper instanceof ClassicButtonShaper)
			radius = SubstanceSizeUtils
					.getClassicButtonCornerRadius(SubstanceSizeUtils
							.getComponentFontSize(scrollBar));

		int borderDelta = (int) Math.floor(SubstanceSizeUtils
				.getBorderStrokeWidth(SubstanceSizeUtils
						.getComponentFontSize(scrollBar)) / 2.0);
		Shape contour = SubstanceOutlineUtilities.getBaseOutline(width, height,
				radius, null, borderDelta);
		BufferedImage result = SubstanceScrollBarUI.trackHorizontalMap.get(key);
		if (result == null) {
			result = SubstanceCoreUtilities.getBlankImage(width, height);
			SimplisticGradientPainter.INSTANCE.paintContourBackground(result
					.createGraphics(), scrollBar, width, height, contour,
					false, mainScheme, mainScheme, 0, true, false);

			SubstanceBorderPainter borderPainter = new SimplisticSoftBorderPainter();
			borderPainter.paintBorder(result.getGraphics(), scrollBar, width,
					height, contour, null, mainBorderScheme, mainBorderScheme,
					0, false);

			SubstanceScrollBarUI.trackHorizontalMap.put(key, result);
		}
		return result;
	}

	/**
	 * Returns the image for a horizontal track.
	 * 
	 * @param scrollBar
	 *            Scroll bar.
	 * @param trackBounds
	 *            Track bounds.
	 * @param leftActiveButton
	 *            The closest left button in the scroll bar. May be
	 *            <code>null</code>.
	 * @param rightActiveButton
	 *            The closest right button in the scroll bar. May be
	 *            <code>null</code> .
	 * @param width
	 *            Scroll track width.
	 * @param height
	 *            Scroll track height.
	 * @param graphicsComposite
	 *            Composite to apply before painting the track.
	 * @return Horizontal track image.
	 */
	private static BufferedImage getTrackBackHorizontal(JScrollBar scrollBar,
			AbstractButton leftActiveButton, AbstractButton rightActiveButton,
			int width, int height) {
		SubstanceButtonShaper shaper = SubstanceCoreUtilities
				.getButtonShaper(scrollBar);
		int radius = height / 2;
		if (shaper instanceof ClassicButtonShaper)
			radius = 2;
		BufferedImage opaque = SubstanceImageCreator
				.getCompositeRoundedBackground(scrollBar, width, height,
						radius, leftActiveButton, rightActiveButton, false);
		return opaque;
	}

	/**
	 * Returns the image for a vertical track.
	 * 
	 * @param trackBounds
	 *            Track bounds.
	 * @param scrollBar
	 *            Scroll bar.
	 * @param topActiveButton
	 *            The closest top button in the scroll bar. May be
	 *            <code>null</code>.
	 * @param bottomActiveButton
	 *            The closest bottom button in the scroll bar. May be
	 *            <code>null</code>.
	 * @return Vertical track image.
	 */
	private BufferedImage getTrackVertical(Rectangle trackBounds,
			SubstanceScrollButton topActiveButton,
			SubstanceScrollButton bottomActiveButton) {

		int width = Math.max(1, trackBounds.width);
		int height = Math.max(1, trackBounds.height);

		ComponentState compTopState = this.getState(topActiveButton);
		ComponentState compBottomState = this.getState(bottomActiveButton);

		Component tracked = SubstanceFadeUtilities.getTracked(
				FadeKind.ROLLOVER, this.scrollbar, this.decrButton,
				this.incrButton, this.mySecondDecreaseButton,
				this.mySecondIncreaseButton);

		if (tracked != null) {
			ComponentState state = (tracked == this.scrollbar) ? ComponentState
					.getState(this.thumbModel, null) : ComponentState.getState(
					((AbstractButton) tracked).getModel(), null);

			float cyclePos = state.getCyclePosition();
			FadeState highest = SubstanceFadeUtilities
					.getFadeStateWithHighestFadeCycle(FadeKind.ROLLOVER,
							this.scrollbar, this.decrButton, this.incrButton,
							this.mySecondDecreaseButton,
							this.mySecondIncreaseButton);
			if (highest != null) {
				cyclePos = highest.getFadePosition();
				if (!highest.isFadingIn())
					cyclePos = 1.0f - cyclePos;
			}

			SubstanceButtonShaper shaper = SubstanceCoreUtilities
					.getButtonShaper(this.scrollbar);
			HashMapKey key = SubstanceCoreUtilities.getHashKey(cyclePos, width,
					height, ((topActiveButton == null) ? "null"
							: ComponentState
									.getState(topActiveButton.getModel(),
											topActiveButton).name()),
					((topActiveButton == null) ? "null"
							: SubstanceCoreUtilities.getPrevComponentState(
									topActiveButton).name()),
					((bottomActiveButton == null) ? "null" : ComponentState
							.getState(bottomActiveButton.getModel(),
									bottomActiveButton).name()),
					((bottomActiveButton == null) ? "null"
							: SubstanceCoreUtilities.getPrevComponentState(
									bottomActiveButton).name()),
					((compTopState == null) ? "null" : compTopState.name()),
					((compBottomState == null) ? "null" : compBottomState
							.name()), ((compBottomState == null) ? "null"
							: SubstanceColorSchemeUtilities.getColorScheme(
									bottomActiveButton, compBottomState)
									.getDisplayName()),
					((compTopState == null) ? "null"
							: SubstanceColorSchemeUtilities.getColorScheme(
									topActiveButton, compTopState)
									.getDisplayName()),
					SubstanceColorSchemeUtilities.getColorScheme(
							this.scrollbar, ComponentState.DEFAULT)
							.getDisplayName(), shaper.getDisplayName(),
					SubstanceSizeUtils.getBorderStrokeWidth(SubstanceSizeUtils
							.getComponentFontSize(scrollbar)));

			// System.out.println(key);
			if (trackFullVerticalMap.containsKey(key)) {
				// System.out.println("Cache hit");
				return trackFullVerticalMap.get(key);
			}
			// System.out.println("Cache miss");

			// System.out.println("New image for vertical track");
			BufferedImage imageBack = getTrackBackVertical(this.scrollbar,
					topActiveButton, bottomActiveButton, width, height);
			Graphics2D backGraphics = imageBack.createGraphics();

			BufferedImage imageDefault = getTrackVertical(this.scrollbar,
					compTopState, compBottomState, width, height);
			backGraphics.drawImage(imageDefault, 0, 0, null);
			BufferedImage imageActive = getTrackVertical(this.scrollbar,
					compTopState, compBottomState, width, height);
			backGraphics.setComposite(AlphaComposite.SrcOver.derive(cyclePos));
			// System.out.println("Painting " + cyclePos);
			backGraphics.drawImage(imageActive, 0, 0, null);

			// System.out.println("Cache update");
			trackFullVerticalMap.put(key, imageBack);
			return imageBack;
		}

		SubstanceButtonShaper shaper = SubstanceCoreUtilities
				.getButtonShaper(this.scrollbar);
		HashMapKey key = SubstanceCoreUtilities
				.getHashKey(
						width,
						height,
						((compTopState == null) ? "null" : ComponentState
								.getState(topActiveButton.getModel(),
										topActiveButton).name()),
						((compTopState == null) ? "null"
								: SubstanceCoreUtilities.getPrevComponentState(
										topActiveButton).name()),
						((compBottomState == null) ? "null" : ComponentState
								.getState(bottomActiveButton.getModel(),
										bottomActiveButton).name()),
						((compBottomState == null) ? "null"
								: SubstanceCoreUtilities.getPrevComponentState(
										bottomActiveButton).name()),
						((compTopState == null) ? "null" : compTopState.name()),
						((compBottomState == null) ? "null" : compBottomState
								.name()), ((compBottomState == null) ? "null"
								: SubstanceColorSchemeUtilities.getColorScheme(
										bottomActiveButton, compBottomState)
										.getDisplayName()),
						((compTopState == null) ? "null"
								: SubstanceColorSchemeUtilities.getColorScheme(
										topActiveButton, compTopState)
										.getDisplayName()),
						SubstanceColorSchemeUtilities.getColorScheme(
								this.scrollbar, ComponentState.DEFAULT)
								.getDisplayName(), shaper.getDisplayName(),
						SubstanceSizeUtils
								.getBorderStrokeWidth(SubstanceSizeUtils
										.getComponentFontSize(scrollbar)));
		if (trackFullVerticalMap.containsKey(key)) {
			// System.out.println("Cache hit");
			return trackFullVerticalMap.get(key);
		}

		// System.out.println("New image for vertical track");
		BufferedImage trackBack = getTrackBackVertical(this.scrollbar,
				topActiveButton, bottomActiveButton, width, height);
		Graphics2D backGraphics = trackBack.createGraphics();

		BufferedImage scrollTrackImage = getTrackVertical(this.scrollbar,
				compTopState, compBottomState, width, height);
		backGraphics.drawImage(scrollTrackImage, 0, 0, null);
		backGraphics.dispose();

		trackFullVerticalMap.put(key, trackBack);
		// System.out.println("Cache update");
		return trackBack;
	}

	/**
	 * Returns the image for a vertical track.
	 * 
	 * @param trackBounds
	 *            Track bounds.
	 * @param scrollBar
	 *            Scroll bar.
	 * @param compTopState
	 *            The state of the top button in the scroll bar.
	 * @param compBottomState
	 *            The state of the closest bottom button in the scroll bar.
	 * @param width
	 *            Scroll track width.
	 * @param height
	 *            Scroll track height.
	 * @param graphicsComposite
	 *            Composite to apply before painting the track.
	 * @return Vertical track image.
	 */
	private static BufferedImage getTrackVertical(JScrollBar scrollBar,
			ComponentState compTopState, ComponentState compBottomState,
			int width, int height) {
		SubstanceButtonShaper shaper = SubstanceCoreUtilities
				.getButtonShaper(scrollBar);
		SubstanceColorScheme mainScheme = SubstanceColorSchemeUtilities
				.getColorScheme(scrollBar,
						scrollBar.isEnabled() ? ComponentState.DEFAULT
								: ComponentState.DISABLED_UNSELECTED);
		SubstanceColorScheme mainBorderScheme = SubstanceColorSchemeUtilities
				.getColorScheme(scrollBar, ColorSchemeAssociationKind.BORDER,
						scrollBar.isEnabled() ? ComponentState.DEFAULT
								: ComponentState.DISABLED_UNSELECTED);
		HashMapKey key = SubstanceCoreUtilities.getHashKey(mainScheme
				.getDisplayName(), mainBorderScheme.getDisplayName(), width,
				height,
				((compTopState == null) ? "null" : compTopState.name()),
				((compBottomState == null) ? "null" : compBottomState.name()),
				shaper.getDisplayName());
		BufferedImage result = SubstanceScrollBarUI.trackVerticalMap.get(key);
		if (result == null) {
			float radius = width / 2;
			if (shaper instanceof ClassicButtonShaper)
				radius = SubstanceSizeUtils
						.getClassicButtonCornerRadius(SubstanceSizeUtils
								.getComponentFontSize(scrollBar));

			int borderDelta = (int) Math.floor(SubstanceSizeUtils
					.getBorderStrokeWidth(SubstanceSizeUtils
							.getComponentFontSize(scrollBar)) / 2.0);
			Shape contour = SubstanceOutlineUtilities.getBaseOutline(height,
					width, radius, null, borderDelta);

			result = SubstanceCoreUtilities.getBlankImage(height, width);
			SimplisticGradientPainter.INSTANCE.paintContourBackground(result
					.createGraphics(), scrollBar, height, width, contour,
					false, mainScheme, mainScheme, 0, true, false);

			SubstanceBorderPainter borderPainter = new SimplisticSoftBorderPainter();
			borderPainter.paintBorder(result.getGraphics(), scrollBar, height,
					width, contour, null, mainBorderScheme, mainBorderScheme,
					0, false);
			result = SubstanceImageCreator.getRotated(result, 3);

			SubstanceScrollBarUI.trackVerticalMap.put(key, result);
		}
		return result;
	}

	/**
	 * Returns the image for a vertical track.
	 * 
	 * @param trackBounds
	 *            Track bounds.
	 * @param scrollBar
	 *            Scroll bar.
	 * @param topActiveButton
	 *            The closest top button in the scroll bar. May be
	 *            <code>null</code>.
	 * @param bottomActiveButton
	 *            The closest bottom button in the scroll bar. May be
	 *            <code>null</code>.
	 * @param width
	 *            Scroll track width.
	 * @param height
	 *            Scroll track height.
	 * @param graphicsComposite
	 *            Composite to apply before painting the track.
	 * @return Vertical track image.
	 */
	private static BufferedImage getTrackBackVertical(JScrollBar scrollBar,
			AbstractButton topActiveButton, AbstractButton bottomActiveButton,
			int width, int height) {
		SubstanceButtonShaper shaper = SubstanceCoreUtilities
				.getButtonShaper(scrollBar);
		int radius = width / 2;
		if (shaper instanceof ClassicButtonShaper)
			radius = 2;
		BufferedImage opaque = SubstanceImageCreator.getRotated(
				SubstanceImageCreator.getCompositeRoundedBackground(scrollBar,
						height, width, radius, topActiveButton,
						bottomActiveButton, true), 3);

		return opaque;
	}

	/**
	 * Retrieves image for vertical thumb.
	 * 
	 * @param thumbBounds
	 *            Thumb bounding rectangle.
	 * @return Image for vertical thumb.
	 */
	private BufferedImage getThumbVertical(Rectangle thumbBounds) {
		int width = Math.max(1, thumbBounds.width);
		int height = Math.max(1, thumbBounds.height);

		// System.out.println(ComponentState.getState(buttonModel, null)
		// .getColorSchemeKind().name());

		Component tracked = SubstanceFadeUtilities.getTracked(
				FadeKind.ROLLOVER, this.scrollbar, this.decrButton,
				this.incrButton, this.mySecondDecreaseButton,
				this.mySecondIncreaseButton);
		ComponentState state = ComponentState.getState(
				this.compositeScrollTrackModel, null);
		if (state.isKindActive(FadeKind.PRESS))
			tracked = null;

		if (tracked != null) {
			ComponentState trackedState = (tracked == this.scrollbar) ? ComponentState
					.getState(this.thumbModel, null)
					: ComponentState.getState(((AbstractButton) tracked)
							.getModel(), null);
			ComponentState prevState = SubstanceCoreUtilities
					.getPrevComponentState(this.scrollbar);
			// enabled scroll bar is always painted as active
			if (trackedState == ComponentState.DEFAULT)
				trackedState = ComponentState.ACTIVE;
			if (prevState == ComponentState.DEFAULT)
				prevState = ComponentState.ACTIVE;

			float cyclePos = trackedState.getCyclePosition();
			FadeState highest = SubstanceFadeUtilities
					.getFadeStateWithHighestFadeCycle(FadeKind.ROLLOVER,
							this.scrollbar, this.decrButton, this.incrButton,
							this.mySecondDecreaseButton,
							this.mySecondIncreaseButton);
			if (highest != null) {
				cyclePos = highest.getFadePosition();
				if (!highest.isFadingIn())
					cyclePos = 1.0f - cyclePos;
			}

			SubstanceColorScheme scheme2 = SubstanceColorSchemeUtilities
					.getColorScheme(this.scrollbar, trackedState);
			SubstanceColorScheme scheme1 = SubstanceColorSchemeUtilities
					.getColorScheme(this.scrollbar, prevState);
			SubstanceColorScheme borderScheme2 = SubstanceColorSchemeUtilities
					.getColorScheme(this.scrollbar,
							ColorSchemeAssociationKind.BORDER, trackedState);
			SubstanceColorScheme borderScheme1 = SubstanceColorSchemeUtilities
					.getColorScheme(this.scrollbar,
							ColorSchemeAssociationKind.BORDER, prevState);

			float borderCyclePos = cyclePos;
			if (scheme1 == scheme2) {
				// special case for smooth rollover animations on skins
				// that have the same ROLLOVER and ACTIVE schemes
				cyclePos = 0.5f - Math.abs(0.5f - cyclePos);
			}
			if (borderScheme1 == borderScheme2) {
				// special case for smooth rollover animations on skins
				// that have the same ROLLOVER and ACTIVE border schemes
				borderCyclePos = 0.5f - Math.abs(0.5f - cyclePos);
			}
			// System.out.println(prevState.name() + " -> [" + cyclePos + "] + "
			// + trackedState.name());
			// System.out.println("\t" + borderScheme1.getDisplayName() + " -> "
			// + borderScheme2.getDisplayName());
			return getThumbVertical(this.scrollbar, width, height, cyclePos,
					scheme1, scheme2, borderCyclePos, borderScheme1,
					borderScheme2);
		}

		ComponentState prevState = SubstanceCoreUtilities
				.getPrevComponentState(this.scrollbar);

		if (state == ComponentState.DEFAULT)
			state = ComponentState.ACTIVE;
		if (prevState == ComponentState.DEFAULT)
			prevState = ComponentState.ACTIVE;
		float cyclePos = state.getCyclePosition();

		SubstanceColorScheme colorScheme = SubstanceColorSchemeUtilities
				.getColorScheme(this.scrollbar, state);
		SubstanceColorScheme borderScheme = SubstanceColorSchemeUtilities
				.getColorScheme(this.scrollbar,
						ColorSchemeAssociationKind.BORDER, state);
		SubstanceColorScheme colorScheme2 = colorScheme;
		SubstanceColorScheme borderScheme2 = borderScheme;
		FadeTracker fadeTracker = FadeTracker.getInstance();
		FadeState fadeState = fadeTracker.getFadeState(this.scrollbar,
				FadeKind.PRESS);
		if (fadeState != null) {
			colorScheme2 = SubstanceColorSchemeUtilities.getColorScheme(
					this.scrollbar, prevState);
			borderScheme2 = SubstanceColorSchemeUtilities.getColorScheme(
					this.scrollbar, ColorSchemeAssociationKind.BORDER,
					prevState);
			cyclePos = fadeState.getFadePosition();
			if (fadeState.isFadingIn()) {
				cyclePos = 1.0f - cyclePos;
			}
		} else {
			cyclePos = 0.0f;
		}
		// System.out.println(colorScheme.getDisplayName() + "->"
		// + colorScheme2.getDisplayName() + ":" + cyclePos);
		return getThumbVertical(this.scrollbar, width, height, cyclePos,
				colorScheme, colorScheme2, cyclePos, borderScheme,
				borderScheme2);
	}

	/**
	 * Retrieves image for vertical thumb.
	 * 
	 * @param scrollBar
	 *            Scroll bar.
	 * @param width
	 *            Thumb width.
	 * @param height
	 *            Thumb height.
	 * @param kind
	 *            Color scheme kind.
	 * @param cyclePos
	 *            Cycle position.
	 * @param scheme
	 *            The first color scheme.
	 * @param scheme2
	 *            The second color scheme.
	 * @param borderScheme
	 *            The first border color scheme.
	 * @param borderScheme2
	 *            The second border color scheme.
	 * @return Image for vertical thumb.
	 */
	private static BufferedImage getThumbVertical(JScrollBar scrollBar,
			int width, int height, float cyclePos, SubstanceColorScheme scheme,
			SubstanceColorScheme scheme2, float borderCyclePos,
			SubstanceColorScheme borderScheme,
			SubstanceColorScheme borderScheme2) {
		SubstanceGradientPainter painter = SubstanceCoreUtilities
				.getGradientPainter(scrollBar);
		SubstanceButtonShaper shaper = SubstanceCoreUtilities
				.getButtonShaper(scrollBar);
		SubstanceBorderPainter borderPainter = SubstanceCoreUtilities
				.getBorderPainter(scrollBar);
		HashMapKey key = SubstanceCoreUtilities.getHashKey(width, height,
				scheme.getDisplayName(), scheme2.getDisplayName(), borderScheme
						.getDisplayName(), borderScheme2.getDisplayName(),
				cyclePos, borderCyclePos, painter.getDisplayName(), shaper
						.getDisplayName(), borderPainter.getDisplayName());
		BufferedImage result = SubstanceScrollBarUI.thumbVerticalMap.get(key);
		if (result == null) {
			// System.out.println("Cache miss - computing");
			// System.out.println("New image for vertical thumb");
			float radius = width / 2;
			if (shaper instanceof ClassicButtonShaper)
				radius = SubstanceSizeUtils
						.getClassicButtonCornerRadius(SubstanceSizeUtils
								.getComponentFontSize(scrollBar));

			int borderDelta = (int) Math.floor(SubstanceSizeUtils
					.getBorderStrokeWidth(SubstanceSizeUtils
							.getComponentFontSize(scrollBar)) / 2.0);
			GeneralPath contour = SubstanceOutlineUtilities.getBaseOutline(
					height, width, radius, null, borderDelta);

			result = SubstanceCoreUtilities.getBlankImage(height, width);
			painter.paintContourBackground(result.createGraphics(), scrollBar,
					height, width, contour, false, scheme, scheme2, cyclePos,
					true, scheme != scheme2);

			// int borderThickness = (int) SubstanceSizeUtils
			// .getBorderStrokeWidth(SubstanceSizeUtils
			// .getComponentFontSize(scrollBar));
			// GeneralPath contourInner = SubstanceOutlineUtilities
			// .getBaseOutline(height, width, radius, null,
			// borderThickness + borderDelta);
			borderPainter.paintBorder(result.getGraphics(), scrollBar, height,
					width, contour, null, borderScheme, borderScheme2,
					borderCyclePos, borderScheme != borderScheme2);
			result = SubstanceImageCreator.getRotated(result, 3);
			// System.out.println(key);
			SubstanceScrollBarUI.thumbVerticalMap.put(key, result);
		}

		return result;
	}

	/**
	 * Retrieves image for horizontal thumb.
	 * 
	 * @param thumbBounds
	 *            Thumb bounding rectangle.
	 * @return Image for horizontal thumb.
	 */
	private BufferedImage getThumbHorizontal(Rectangle thumbBounds) {
		int width = Math.max(1, thumbBounds.width);
		int height = Math.max(1, thumbBounds.height);

		Component tracked = SubstanceFadeUtilities.getTracked(
				FadeKind.ROLLOVER, this.scrollbar, this.decrButton,
				this.incrButton, this.mySecondDecreaseButton,
				this.mySecondIncreaseButton);
		ComponentState state = ComponentState.getState(
				this.compositeScrollTrackModel, null);
		if (state.isKindActive(FadeKind.PRESS))
			tracked = null;

		if (tracked != null) {
			ComponentState trackedState = (tracked == this.scrollbar) ? ComponentState
					.getState(this.thumbModel, null)
					: ComponentState.getState(((AbstractButton) tracked)
							.getModel(), null);
			ComponentState prevState = SubstanceCoreUtilities
					.getPrevComponentState(this.scrollbar);
			if (trackedState == ComponentState.DEFAULT)
				trackedState = ComponentState.ACTIVE;
			if (prevState == ComponentState.DEFAULT)
				prevState = ComponentState.ACTIVE;

			float cyclePos = trackedState.getCyclePosition();
			FadeState highest = SubstanceFadeUtilities
					.getFadeStateWithHighestFadeCycle(FadeKind.ROLLOVER,
							this.scrollbar, this.decrButton, this.incrButton,
							this.mySecondDecreaseButton,
							this.mySecondIncreaseButton);
			if (highest != null) {
				cyclePos = highest.getFadePosition();
				if (!highest.isFadingIn())
					cyclePos = 1.0f - cyclePos;
			}

			SubstanceColorScheme scheme2 = SubstanceColorSchemeUtilities
					.getColorScheme(this.scrollbar, trackedState);
			SubstanceColorScheme scheme1 = SubstanceColorSchemeUtilities
					.getColorScheme(this.scrollbar, prevState);
			SubstanceColorScheme borderScheme2 = SubstanceColorSchemeUtilities
					.getColorScheme(this.scrollbar,
							ColorSchemeAssociationKind.BORDER, trackedState);
			SubstanceColorScheme borderScheme1 = SubstanceColorSchemeUtilities
					.getColorScheme(this.scrollbar,
							ColorSchemeAssociationKind.BORDER, prevState);

			float borderCyclePos = cyclePos;
			if (scheme1 == scheme2) {
				// special case for smooth rollover animations on skins
				// that have the same ROLLOVER and ACTIVE schemes
				cyclePos = 0.5f - Math.abs(0.5f - cyclePos);
			}
			if (borderScheme1 == borderScheme2) {
				// special case for smooth rollover animations on skins
				// that have the same ROLLOVER and ACTIVE border schemes
				borderCyclePos = 0.5f - Math.abs(0.5f - cyclePos);
			}
			// System.out.println(prevState.name() + " -> [" + cyclePos + "] + "
			// + trackedState.name());
			// System.out.println("\t" + borderScheme1.getDisplayName() + " -> "
			// + borderScheme2.getDisplayName());
			return getThumbHorizontal(this.scrollbar, width, height, cyclePos,
					scheme1, scheme2, borderCyclePos, borderScheme1,
					borderScheme2);
		}

		ComponentState prevState = SubstanceCoreUtilities
				.getPrevComponentState(this.scrollbar);
		float cyclePos = state.getCyclePosition();

		if (state == ComponentState.DEFAULT)
			state = ComponentState.ACTIVE;
		if (prevState == ComponentState.DEFAULT)
			prevState = ComponentState.ACTIVE;

		SubstanceColorScheme colorScheme = SubstanceColorSchemeUtilities
				.getColorScheme(this.scrollbar, state);
		SubstanceColorScheme colorScheme2 = colorScheme;
		SubstanceColorScheme borderScheme = SubstanceColorSchemeUtilities
				.getColorScheme(this.scrollbar,
						ColorSchemeAssociationKind.BORDER, state);
		SubstanceColorScheme borderScheme2 = borderScheme;
		FadeTracker fadeTracker = FadeTracker.getInstance();
		FadeState fadeState = fadeTracker.getFadeState(this.scrollbar,
				FadeKind.PRESS);
		if (fadeState != null) {
			colorScheme2 = SubstanceColorSchemeUtilities.getColorScheme(
					this.scrollbar, prevState);
			borderScheme2 = SubstanceColorSchemeUtilities.getColorScheme(
					this.scrollbar, ColorSchemeAssociationKind.BORDER,
					prevState);
			cyclePos = fadeState.getFadePosition();
			if (fadeState.isFadingIn()) {
				cyclePos = 1.0f - cyclePos;
			}
		} else {
			cyclePos = 0.0f;
		}
		return getThumbHorizontal(this.scrollbar, width, height, cyclePos,
				colorScheme, colorScheme2, cyclePos, borderScheme,
				borderScheme2);
	}

	/**
	 * Retrieves image for horizontal thumb.
	 * 
	 * @param scrollBar
	 *            Scroll bar.
	 * @param width
	 *            Thumb width.
	 * @param height
	 *            Thumb height.
	 * @param kind
	 *            Color scheme kind.
	 * @param cyclePos
	 *            Cycle position.
	 * @param scheme
	 *            The first color scheme.
	 * @param scheme2
	 *            The second color scheme.
	 * @param borderScheme
	 *            The first border color scheme.
	 * @param borderScheme2
	 *            The second border color scheme.
	 * @return Image for horizontal thumb.
	 */
	private static BufferedImage getThumbHorizontal(JScrollBar scrollBar,
			int width, int height, float cyclePos, SubstanceColorScheme scheme,
			SubstanceColorScheme scheme2, float borderCyclePos,
			SubstanceColorScheme borderScheme,
			SubstanceColorScheme borderScheme2) {
		SubstanceGradientPainter painter = SubstanceCoreUtilities
				.getGradientPainter(scrollBar);
		SubstanceButtonShaper shaper = SubstanceCoreUtilities
				.getButtonShaper(scrollBar);
		SubstanceBorderPainter borderPainter = SubstanceCoreUtilities
				.getBorderPainter(scrollBar);
		// GripPainter gripPainter = SubstanceCoreUtilities.getGripPainter(
		// scrollBar, null);
		// System.out.println(state.name());
		HashMapKey key = SubstanceCoreUtilities.getHashKey(width, height,
				scheme.getDisplayName(), scheme2.getDisplayName(), borderScheme
						.getDisplayName(), borderScheme2.getDisplayName(),
				cyclePos, borderCyclePos, painter.getDisplayName(), shaper
						.getDisplayName(), borderPainter.getDisplayName());
		// + ":"
		// + ((gripPainter != null) ? gripPainter.getDisplayName()
		// : "null");

		float radius = height / 2;
		if (shaper instanceof ClassicButtonShaper)
			radius = SubstanceSizeUtils
					.getClassicButtonCornerRadius(SubstanceSizeUtils
							.getComponentFontSize(scrollBar));
		int borderDelta = (int) Math.floor(SubstanceSizeUtils
				.getBorderStrokeWidth(SubstanceSizeUtils
						.getComponentFontSize(scrollBar)) / 2.0);
		GeneralPath contour = SubstanceOutlineUtilities.getBaseOutline(width,
				height, radius, null, borderDelta);
		BufferedImage opaque = SubstanceScrollBarUI.thumbHorizontalMap.get(key);
		if (opaque == null) {
			// System.out.println("New image for horizontal thumb");

			opaque = SubstanceCoreUtilities.getBlankImage(width, height);
			painter.paintContourBackground(opaque.createGraphics(), scrollBar,
					width, height, contour, false, scheme, scheme2, cyclePos,
					true, scheme != scheme2);

			// int borderThickness = (int) SubstanceSizeUtils
			// .getBorderStrokeWidth(SubstanceSizeUtils
			// .getComponentFontSize(scrollBar));
			// GeneralPath contourInner = SubstanceOutlineUtilities
			// .getBaseOutline(width, height, radius, null,
			// borderThickness + borderDelta);
			borderPainter.paintBorder(opaque.getGraphics(), scrollBar, width,
					height, contour, null, borderScheme, borderScheme2,
					borderCyclePos, borderScheme != borderScheme2);
			SubstanceScrollBarUI.thumbHorizontalMap.put(key, opaque);
		}

		return opaque;
	}

	/**
	 * Returns the scroll button state.
	 * 
	 * @param scrollButton
	 *            Scroll button.
	 * @return Scroll button state.
	 */
	protected ComponentState getState(JButton scrollButton) {
		if (scrollButton == null)
			return null;

		ComponentState result = ComponentState.getState(scrollButton);
		if ((result == ComponentState.DEFAULT)
				&& SubstanceCoreUtilities.hasFlatAppearance(this.scrollbar,
						false)) {
			result = null;
		}
		if (SubstanceCoreUtilities.isButtonNeverPainted(scrollButton)) {
			result = null;
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.plaf.basic.BasicScrollBarUI#paintTrack(java.awt.Graphics,
	 * javax.swing.JComponent, java.awt.Rectangle)
	 */
	@Override
	protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
		Graphics2D graphics = (Graphics2D) g.create();

		// System.out.println("Track");
		ScrollPaneButtonPolicyKind buttonPolicy = SubstanceCoreUtilities
				.getScrollPaneButtonsPolicyKind(this.scrollbar);
		SubstanceScrollButton compTopState = null;
		SubstanceScrollButton compBottomState = null;
		if (this.decrButton.isShowing() && this.incrButton.isShowing()
				&& this.mySecondDecreaseButton.isShowing()
				&& this.mySecondIncreaseButton.isShowing()) {
			switch (buttonPolicy) {
			case OPPOSITE:
				compTopState = (SubstanceScrollButton) this.decrButton;
				compBottomState = (SubstanceScrollButton) this.incrButton;
				break;
			case ADJACENT:
				compBottomState = (SubstanceScrollButton) this.mySecondDecreaseButton;
				break;
			case MULTIPLE:
				compTopState = (SubstanceScrollButton) this.decrButton;
				compBottomState = (SubstanceScrollButton) this.mySecondDecreaseButton;
				break;
			case MULTIPLE_BOTH:
				compTopState = (SubstanceScrollButton) this.mySecondIncreaseButton;
				compBottomState = (SubstanceScrollButton) this.mySecondDecreaseButton;
				break;
			}
		}

		if (this.scrollbar.getOrientation() == Adjustable.VERTICAL) {
			BufferedImage bi = this.getTrackVertical(trackBounds, compTopState,
					compBottomState);
			graphics.drawImage(bi, trackBounds.x, trackBounds.y, null);
		} else {
			BufferedImage bi = this.scrollbar.getComponentOrientation()
					.isLeftToRight() ? this.getTrackHorizontal(trackBounds,
					compTopState, compBottomState) : this.getTrackHorizontal(
					trackBounds, compBottomState, compTopState);
			graphics.drawImage(bi, trackBounds.x, trackBounds.y, null);
		}

		graphics.dispose();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.plaf.basic.BasicScrollBarUI#paintThumb(java.awt.Graphics,
	 * javax.swing.JComponent, java.awt.Rectangle)
	 */
	@Override
	protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
		// System.out.println("Thumb");
		Graphics2D graphics = (Graphics2D) g.create();
		// ControlBackgroundComposite composite = SubstanceCoreUtilities
		// .getControlBackgroundComposite(this.scrollbar);

		// JScrollBar scrollBar = (JScrollBar) c;
		this.thumbModel.setSelected(this.thumbModel.isSelected()
				|| this.isDragging);
		this.thumbModel.setEnabled(c.isEnabled());
		boolean isVertical = (this.scrollbar.getOrientation() == Adjustable.VERTICAL);
		if (isVertical) {
			Rectangle adjustedBounds = new Rectangle(thumbBounds.x,
					thumbBounds.y, thumbBounds.width, thumbBounds.height);
			BufferedImage thumbImage = this.getThumbVertical(adjustedBounds);
			graphics.drawImage(thumbImage, adjustedBounds.x, adjustedBounds.y,
					null);
		} else {
			Rectangle adjustedBounds = new Rectangle(thumbBounds.x,
					thumbBounds.y, thumbBounds.width, thumbBounds.height);
			BufferedImage thumbImage = this.getThumbHorizontal(adjustedBounds);
			graphics.drawImage(thumbImage, adjustedBounds.x, adjustedBounds.y,
					null);
		}
		graphics.dispose();
	}

	@Override
	public void paint(Graphics g, JComponent c) {
		Graphics2D graphics = (Graphics2D) g.create();
		BackgroundPaintingUtils.update(graphics, c, false);
		float alpha = SubstanceColorSchemeUtilities.getAlpha(this.scrollbar,
				ComponentState.getState(this.thumbModel, this.scrollbar));
		graphics.setComposite(TransitionLayout.getAlphaComposite(c, alpha, g));
		super.paint(graphics, c);
		graphics.dispose();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.basic.BasicScrollBarUI#installDefaults()
	 */
	@Override
	protected void installDefaults() {
		super.installDefaults();
		this.scrollBarWidth = SubstanceSizeUtils
				.getScrollBarWidth(SubstanceSizeUtils
						.getComponentFontSize(this.scrollbar));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.basic.BasicScrollBarUI#installComponents()
	 */
	@Override
	protected void installComponents() {
		super.installComponents();
		switch (this.scrollbar.getOrientation()) {
		case JScrollBar.VERTICAL:
			this.mySecondDecreaseButton = this.createGeneralDecreaseButton(
					NORTH, false);
			this.mySecondIncreaseButton = this.createGeneralIncreaseButton(
					SOUTH, false);
			break;

		case JScrollBar.HORIZONTAL:
			if (this.scrollbar.getComponentOrientation().isLeftToRight()) {
				this.mySecondDecreaseButton = this.createGeneralDecreaseButton(
						WEST, false);
				this.mySecondIncreaseButton = this.createGeneralIncreaseButton(
						EAST, false);
			} else {
				this.mySecondDecreaseButton = this.createGeneralDecreaseButton(
						EAST, false);
				this.mySecondIncreaseButton = this.createGeneralIncreaseButton(
						WEST, false);
			}
			break;
		}
		this.scrollbar.add(this.mySecondDecreaseButton);
		this.scrollbar.add(this.mySecondIncreaseButton);

		this.compositeScrollTrackModel = new CompositeButtonModel(
				this.thumbModel, this.incrButton, this.decrButton,
				this.mySecondDecreaseButton, this.mySecondIncreaseButton);
		this.compositeButtonsModel = new CompositeButtonModel(
				new DefaultButtonModel(), this.incrButton, this.decrButton,
				this.mySecondDecreaseButton, this.mySecondIncreaseButton);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.basic.BasicScrollBarUI#uninstallComponents()
	 */
	@Override
	protected void uninstallComponents() {
		this.scrollbar.remove(this.mySecondDecreaseButton);
		this.scrollbar.remove(this.mySecondIncreaseButton);
		super.uninstallComponents();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.basic.BasicScrollBarUI#installListeners()
	 */
	@Override
	protected void installListeners() {
		super.installListeners();
		this.substanceMouseListener = new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				SubstanceScrollBarUI.this.scrollbar.repaint();
			}

			@Override
			public void mouseExited(MouseEvent e) {
				SubstanceScrollBarUI.this.scrollbar.repaint();
			}

			@Override
			public void mousePressed(MouseEvent e) {
				SubstanceScrollBarUI.this.scrollbar.repaint();
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				SubstanceScrollBarUI.this.scrollbar.repaint();
			}
		};

		this.incrButton.addMouseListener(this.substanceMouseListener);
		this.decrButton.addMouseListener(this.substanceMouseListener);
		this.mySecondDecreaseButton
				.addMouseListener(this.substanceMouseListener);
		this.mySecondIncreaseButton
				.addMouseListener(this.substanceMouseListener);

		this.substanceThumbRolloverListener = new RolloverControlListener(this,
				this.thumbModel);
		this.scrollbar.addMouseListener(this.substanceThumbRolloverListener);
		this.scrollbar
				.addMouseMotionListener(this.substanceThumbRolloverListener);

		this.substanceFadeStateListener = new FadeStateListener(this.scrollbar,
				this.thumbModel, SubstanceCoreUtilities.getFadeCallback(
						this.scrollbar, this.thumbModel, false, false,
						this.scrollbar));
		this.substanceFadeStateListener.registerListeners(false);

		this.substancePropertyListener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if ("font".equals(evt.getPropertyName())) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							scrollbar.updateUI();
						}
					});
				}
				if ("background".equals(evt.getPropertyName())) {
					// propagate application-specific background color to the
					// scroll buttons.
					Color newBackgr = (Color) evt.getNewValue();
					if (!(newBackgr instanceof UIResource)) {
						if (mySecondDecreaseButton != null) {
							if (mySecondDecreaseButton.getBackground() instanceof UIResource) {
								mySecondDecreaseButton.setBackground(newBackgr);
							}
						}
						if (mySecondIncreaseButton != null) {
							if (mySecondIncreaseButton.getBackground() instanceof UIResource) {
								mySecondIncreaseButton.setBackground(newBackgr);
							}
						}
						if (incrButton != null) {
							if (incrButton.getBackground() instanceof UIResource) {
								incrButton.setBackground(newBackgr);
							}
						}
						if (decrButton != null) {
							if (decrButton.getBackground() instanceof UIResource) {
								decrButton.setBackground(newBackgr);
							}
						}
					}
				}
			}
		};
		this.scrollbar
				.addPropertyChangeListener(this.substancePropertyListener);

		this.mySecondDecreaseButton.addMouseListener(this.buttonListener);
		this.mySecondIncreaseButton.addMouseListener(this.buttonListener);

		this.substanceAdjustmentListener = new AdjustmentListener() {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				SubstanceCoreUtilities
						.testComponentStateChangeThreadingViolation(scrollbar);
				Component parent = SubstanceScrollBarUI.this.scrollbar
						.getParent();
				if (parent instanceof JScrollPane) {
					JScrollPane jsp = (JScrollPane) parent;
					JScrollBar hor = jsp.getHorizontalScrollBar();
					JScrollBar ver = jsp.getVerticalScrollBar();

					JScrollBar other = null;
					if (SubstanceScrollBarUI.this.scrollbar == hor) {
						other = ver;
					}
					if (SubstanceScrollBarUI.this.scrollbar == ver) {
						other = hor;
					}

					if ((other != null) && other.isVisible())
						other.repaint();
					SubstanceScrollBarUI.this.scrollbar.repaint();
				}
			}
		};
		this.scrollbar.addAdjustmentListener(this.substanceAdjustmentListener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.basic.BasicScrollBarUI#uninstallListeners()
	 */
	@Override
	protected void uninstallListeners() {
		// fix for defect 109 - memory leak on changing skin
		this.incrButton.removeMouseListener(this.substanceMouseListener);
		this.decrButton.removeMouseListener(this.substanceMouseListener);
		this.mySecondDecreaseButton
				.removeMouseListener(this.substanceMouseListener);
		this.mySecondIncreaseButton
				.removeMouseListener(this.substanceMouseListener);
		this.substanceMouseListener = null;

		this.scrollbar.removeMouseListener(this.substanceThumbRolloverListener);
		this.scrollbar
				.removeMouseMotionListener(this.substanceThumbRolloverListener);
		this.substanceThumbRolloverListener = null;

		this.substanceFadeStateListener.unregisterListeners();
		this.substanceFadeStateListener = null;

		this.scrollbar
				.removePropertyChangeListener(this.substancePropertyListener);
		this.substancePropertyListener = null;

		this.mySecondDecreaseButton.removeMouseListener(this.buttonListener);
		this.mySecondIncreaseButton.removeMouseListener(this.buttonListener);

		this.scrollbar
				.removeAdjustmentListener(this.substanceAdjustmentListener);
		this.substanceAdjustmentListener = null;

		super.uninstallListeners();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jvnet.substance.Trackable#isInside(java.awt.event.MouseEvent)
	 */
	public boolean isInside(MouseEvent me) {
		// Rectangle thumbB = this.getThumbBounds();
		Rectangle trackB = this.getTrackBounds();
		if (trackB == null)
			return false;
		return trackB.contains(me.getX(), me.getY());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.basic.BasicScrollBarUI#scrollByBlock(int)
	 */
	@Override
	public void scrollByBlock(int direction) {
		// This method is called from SubstanceScrollPaneUI to implement wheel
		// scrolling.
		int oldValue = this.scrollbar.getValue();
		int blockIncrement = this.scrollbar.getBlockIncrement(direction);
		int delta = blockIncrement * ((direction > 0) ? +1 : -1);
		int newValue = oldValue + delta;

		// Check for overflow.
		if ((delta > 0) && (newValue < oldValue)) {
			newValue = this.scrollbar.getMaximum();
		} else if ((delta < 0) && (newValue > oldValue)) {
			newValue = this.scrollbar.getMinimum();
		}

		this.scrollbar.setValue(newValue);
	}

	/**
	 * Scrolls the associated scroll bar.
	 * 
	 * @param direction
	 *            Direction.
	 * @param units
	 *            Scroll units.
	 */
	public void scrollByUnits(int direction, int units) {
		// This method is called from SubstanceScrollPaneUI to implement wheel
		// scrolling.
		int delta;

		for (int i = 0; i < units; i++) {
			if (direction > 0) {
				delta = this.scrollbar.getUnitIncrement(direction);
			} else {
				delta = -this.scrollbar.getUnitIncrement(direction);
			}

			int oldValue = this.scrollbar.getValue();
			int newValue = oldValue + delta;

			// Check for overflow.
			if ((delta > 0) && (newValue < oldValue)) {
				newValue = this.scrollbar.getMaximum();
			} else if ((delta < 0) && (newValue > oldValue)) {
				newValue = this.scrollbar.getMinimum();
			}
			if (oldValue == newValue) {
				break;
			}
			this.scrollbar.setValue(newValue);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.plaf.basic.BasicScrollBarUI#layoutVScrollbar(javax.swing.
	 * JScrollBar)
	 */
	@Override
	protected void layoutVScrollbar(JScrollBar sb) {
		ScrollPaneButtonPolicyKind buttonPolicy = SubstanceCoreUtilities
				.getScrollPaneButtonsPolicyKind(this.scrollbar);
		this.mySecondDecreaseButton.setBounds(0, 0, 0, 0);
		this.mySecondIncreaseButton.setBounds(0, 0, 0, 0);
		switch (buttonPolicy) {
		case OPPOSITE:
			super.layoutVScrollbar(sb);
			break;
		case NONE:
			this.layoutVScrollbarNone(sb);
			break;
		case ADJACENT:
			this.layoutVScrollbarAdjacent(sb);
			break;
		case MULTIPLE:
			this.layoutVScrollbarMultiple(sb);
			break;
		case MULTIPLE_BOTH:
			this.layoutVScrollbarMultipleBoth(sb);
			break;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.plaf.basic.BasicScrollBarUI#layoutHScrollbar(javax.swing.
	 * JScrollBar)
	 */
	@Override
	protected void layoutHScrollbar(JScrollBar sb) {
		this.mySecondDecreaseButton.setBounds(0, 0, 0, 0);
		this.mySecondIncreaseButton.setBounds(0, 0, 0, 0);
		ScrollPaneButtonPolicyKind buttonPolicy = SubstanceCoreUtilities
				.getScrollPaneButtonsPolicyKind(this.scrollbar);
		switch (buttonPolicy) {
		case OPPOSITE:
			super.layoutHScrollbar(sb);
			break;
		case NONE:
			this.layoutHScrollbarNone(sb);
			break;
		case ADJACENT:
			this.layoutHScrollbarAdjacent(sb);
			break;
		case MULTIPLE:
			this.layoutHScrollbarMultiple(sb);
			break;
		case MULTIPLE_BOTH:
			this.layoutHScrollbarMultipleBoth(sb);
			break;
		}
	}

	/**
	 * Lays out the vertical scroll bar when the button policy is
	 * {@link SubstanceConstants.ScrollPaneButtonPolicyKind#ADJACENT}.
	 * 
	 * @param sb
	 *            Scroll bar.
	 */
	protected void layoutVScrollbarAdjacent(JScrollBar sb) {
		Dimension sbSize = sb.getSize();
		Insets sbInsets = sb.getInsets();

		/*
		 * Width and left edge of the buttons and thumb.
		 */
		int itemW = sbSize.width - (sbInsets.left + sbInsets.right);
		int itemX = sbInsets.left;

		/*
		 * Nominal locations of the buttons, assuming their preferred size will
		 * fit.
		 */
		int incrButtonH = itemW;
		int incrButtonY = sbSize.height - (sbInsets.bottom + incrButtonH);

		int decrButton2H = itemW;
		int decrButton2Y = incrButtonY - decrButton2H;

		/*
		 * The thumb must fit within the height left over after we subtract the
		 * preferredSize of the buttons and the insets.
		 */
		int sbInsetsH = sbInsets.top + sbInsets.bottom;
		int sbButtonsH = decrButton2H + incrButtonH;
		float trackH = sbSize.height - (sbInsetsH + sbButtonsH);

		/*
		 * Compute the height and origin of the thumb. The case where the thumb
		 * is at the bottom edge is handled specially to avoid numerical
		 * problems in computing thumbY. Enforce the thumbs min/max dimensions.
		 * If the thumb doesn't fit in the track (trackH) we'll hide it later.
		 */
		float min = sb.getMinimum();
		float extent = sb.getVisibleAmount();
		float range = sb.getMaximum() - min;
		float value = sb.getValue();

		int thumbH = (range <= 0) ? this.getMaximumThumbSize().height
				: (int) (trackH * (extent / range));
		thumbH = Math.max(thumbH, this.getMinimumThumbSize().height);
		thumbH = Math.min(thumbH, this.getMaximumThumbSize().height);

		int thumbY = decrButton2Y - thumbH;
		if (value < (sb.getMaximum() - sb.getVisibleAmount())) {
			float thumbRange = trackH - thumbH;
			thumbY = (int) (0.5f + (thumbRange * ((value - min) / (range - extent))));
		}

		/*
		 * If the buttons don't fit, allocate half of the available space to
		 * each and move the lower one (incrButton) down.
		 */
		int sbAvailButtonH = (sbSize.height - sbInsetsH);
		if (sbAvailButtonH < sbButtonsH) {
			incrButtonH = decrButton2H = sbAvailButtonH / 2;
			incrButtonY = sbSize.height - (sbInsets.bottom + incrButtonH);
		}
		this.mySecondIncreaseButton.setBounds(0, 0, 0, 0);
		this.decrButton.setBounds(0, 0, 0, 0);
		this.mySecondDecreaseButton.setBounds(itemX,
				incrButtonY - decrButton2H, itemW, decrButton2H);
		this.incrButton.setBounds(itemX, incrButtonY - 1, itemW,
				incrButtonH + 1);

		/*
		 * Update the trackRect field.
		 */
		int itrackY = 0;
		int itrackH = decrButton2Y - itrackY;
		this.trackRect.setBounds(itemX, itrackY, itemW, itrackH);

		/*
		 * If the thumb isn't going to fit, zero it's bounds. Otherwise make
		 * sure it fits between the buttons. Note that setting the thumbs bounds
		 * will cause a repaint.
		 */
		if (thumbH >= (int) trackH) {
			this.setThumbBounds(0, 0, 0, 0);
		} else {
			if ((thumbY + thumbH) > decrButton2Y) {
				thumbY = decrButton2Y - thumbH;
			}
			if (thumbY < 0) {
				thumbY = 0;
			}
			this.setThumbBounds(itemX, thumbY, itemW, thumbH);
		}
	}

	/**
	 * Lays out the vertical scroll bar when the button policy is
	 * {@link SubstanceConstants.ScrollPaneButtonPolicyKind#ADJACENT}.
	 * 
	 * @param sb
	 *            Scroll bar.
	 */
	protected void layoutVScrollbarNone(JScrollBar sb) {
		Dimension sbSize = sb.getSize();
		Insets sbInsets = sb.getInsets();

		/*
		 * Width and left edge of the buttons and thumb.
		 */
		int itemW = sbSize.width - (sbInsets.left + sbInsets.right);
		int itemX = sbInsets.left;

		/*
		 * Nominal locations of the buttons, assuming their preferred size will
		 * fit.
		 */
		int incrButtonH = 0;
		int incrButtonY = sbSize.height - (sbInsets.bottom + incrButtonH);

		int decrButton2H = 0;
		int decrButton2Y = incrButtonY - decrButton2H;

		/*
		 * The thumb must fit within the height left over after we subtract the
		 * preferredSize of the buttons and the insets.
		 */
		int sbInsetsH = sbInsets.top + sbInsets.bottom;
		int sbButtonsH = decrButton2H + incrButtonH;
		float trackH = sbSize.height - (sbInsetsH + sbButtonsH);

		/*
		 * Compute the height and origin of the thumb. The case where the thumb
		 * is at the bottom edge is handled specially to avoid numerical
		 * problems in computing thumbY. Enforce the thumbs min/max dimensions.
		 * If the thumb doesn't fit in the track (trackH) we'll hide it later.
		 */
		float min = sb.getMinimum();
		float extent = sb.getVisibleAmount();
		float range = sb.getMaximum() - min;
		float value = sb.getValue();

		int thumbH = (range <= 0) ? this.getMaximumThumbSize().height
				: (int) (trackH * (extent / range));
		thumbH = Math.max(thumbH, this.getMinimumThumbSize().height);
		thumbH = Math.min(thumbH, this.getMaximumThumbSize().height);

		int thumbY = decrButton2Y - thumbH;
		if (value < (sb.getMaximum() - sb.getVisibleAmount())) {
			float thumbRange = trackH - thumbH;
			thumbY = (int) (0.5f + (thumbRange * ((value - min) / (range - extent))));
		}

		/*
		 * If the buttons don't fit, allocate half of the available space to
		 * each and move the lower one (incrButton) down.
		 */
		int sbAvailButtonH = (sbSize.height - sbInsetsH);
		if (sbAvailButtonH < sbButtonsH) {
			incrButtonH = 0;// decrButton2H = 0;
			// incrButtonY = sbSize.height - (sbInsets.bottom + incrButtonH);
		}
		this.decrButton.setBounds(0, 0, 0, 0);
		this.mySecondDecreaseButton.setBounds(0, 0, 0, 0);
		this.incrButton.setBounds(0, 0, 0, 0);
		this.mySecondIncreaseButton.setBounds(0, 0, 0, 0);

		/*
		 * Update the trackRect field.
		 */
		int itrackY = 0;
		int itrackH = decrButton2Y - itrackY;
		this.trackRect.setBounds(itemX, itrackY, itemW, itrackH);

		/*
		 * If the thumb isn't going to fit, zero it's bounds. Otherwise make
		 * sure it fits between the buttons. Note that setting the thumbs bounds
		 * will cause a repaint.
		 */
		if (thumbH >= (int) trackH) {
			this.setThumbBounds(0, 0, 0, 0);
		} else {
			if ((thumbY + thumbH) > decrButton2Y) {
				thumbY = decrButton2Y - thumbH;
			}
			if (thumbY < 0) {
				thumbY = 0;
			}
			this.setThumbBounds(itemX, thumbY, itemW, thumbH);
		}
	}

	/**
	 * Lays out the vertical scroll bar when the button policy is
	 * {@link SubstanceConstants.ScrollPaneButtonPolicyKind#MULTIPLE}.
	 * 
	 * @param sb
	 *            Scroll bar.
	 */
	protected void layoutVScrollbarMultiple(JScrollBar sb) {
		Dimension sbSize = sb.getSize();
		Insets sbInsets = sb.getInsets();

		/*
		 * Width and left edge of the buttons and thumb.
		 */
		int itemW = sbSize.width - (sbInsets.left + sbInsets.right);
		int itemX = sbInsets.left;

		/*
		 * Nominal locations of the buttons, assuming their preferred size will
		 * fit.
		 */
		int incrButtonH = itemW;
		int incrButtonY = sbSize.height - (sbInsets.bottom + incrButtonH);

		int decrButton2H = itemW;
		int decrButton2Y = incrButtonY - decrButton2H;

		int decrButtonH = itemW;
		int decrButtonY = sbInsets.top;

		/*
		 * The thumb must fit within the height left over after we subtract the
		 * preferredSize of the buttons and the insets.
		 */
		int sbInsetsH = sbInsets.top + sbInsets.bottom;
		int sbButtonsH = decrButton2H + incrButtonH + decrButtonH;
		float trackH = sbSize.height - (sbInsetsH + sbButtonsH);

		/*
		 * Compute the height and origin of the thumb. The case where the thumb
		 * is at the bottom edge is handled specially to avoid numerical
		 * problems in computing thumbY. Enforce the thumbs min/max dimensions.
		 * If the thumb doesn't fit in the track (trackH) we'll hide it later.
		 */
		float min = sb.getMinimum();
		float extent = sb.getVisibleAmount();
		float range = sb.getMaximum() - min;
		float value = sb.getValue();

		int thumbH = (range <= 0) ? this.getMaximumThumbSize().height
				: (int) (trackH * (extent / range));
		thumbH = Math.max(thumbH, this.getMinimumThumbSize().height);
		thumbH = Math.min(thumbH, this.getMaximumThumbSize().height);

		int thumbY = decrButton2Y - thumbH;
		if (value < (sb.getMaximum() - sb.getVisibleAmount())) {
			float thumbRange = trackH - thumbH;
			thumbY = (int) (0.5f + (thumbRange * ((value - min) / (range - extent))));
			thumbY += decrButtonY + decrButtonH;
		}

		/*
		 * If the buttons don't fit, allocate half of the available space to
		 * each and move the lower one (incrButton) down.
		 */
		int sbAvailButtonH = (sbSize.height - sbInsetsH);
		if (sbAvailButtonH < sbButtonsH) {
			incrButtonH = decrButton2H = decrButtonH = sbAvailButtonH / 2;
			incrButtonY = sbSize.height - (sbInsets.bottom + incrButtonH);
		}
		this.decrButton.setBounds(itemX, decrButtonY, itemW, decrButtonH);
		this.mySecondDecreaseButton.setBounds(itemX,
				incrButtonY - decrButton2H, itemW, decrButton2H);
		this.incrButton.setBounds(itemX, incrButtonY - 1, itemW,
				incrButtonH + 1);
		this.mySecondIncreaseButton.setBounds(0, 0, 0, 0);

		/*
		 * Update the trackRect field.
		 */
		int itrackY = decrButtonY + decrButtonH;
		int itrackH = decrButton2Y - itrackY;
		this.trackRect.setBounds(itemX, itrackY, itemW, itrackH);

		/*
		 * If the thumb isn't going to fit, zero it's bounds. Otherwise make
		 * sure it fits between the buttons. Note that setting the thumbs bounds
		 * will cause a repaint.
		 */
		if (thumbH >= (int) trackH) {
			this.setThumbBounds(0, 0, 0, 0);
		} else {
			if ((thumbY + thumbH) > decrButton2Y) {
				thumbY = decrButton2Y - thumbH;
			}
			if (thumbY < (decrButtonY + decrButtonH)) {
				thumbY = decrButtonY + decrButtonH + 1;
			}
			this.setThumbBounds(itemX, thumbY, itemW, thumbH);
		}
	}

	/**
	 * Lays out the vertical scroll bar when the button policy is
	 * {@link SubstanceConstants.ScrollPaneButtonPolicyKind#MULTIPLE_BOTH}.
	 * 
	 * @param sb
	 *            Scroll bar.
	 */
	protected void layoutVScrollbarMultipleBoth(JScrollBar sb) {
		Dimension sbSize = sb.getSize();
		Insets sbInsets = sb.getInsets();

		/*
		 * Width and left edge of the buttons and thumb.
		 */
		int itemW = sbSize.width - (sbInsets.left + sbInsets.right);
		int itemX = sbInsets.left;

		/*
		 * Nominal locations of the buttons, assuming their preferred size will
		 * fit.
		 */
		int incrButtonH = itemW;
		int incrButtonY = sbSize.height - (sbInsets.bottom + incrButtonH);

		int decrButton2H = itemW;
		int decrButton2Y = incrButtonY - decrButton2H;

		int decrButtonH = itemW;
		int decrButtonY = sbInsets.top;

		int incrButton2H = itemW;
		int incrButton2Y = decrButtonY + decrButtonH;

		/*
		 * The thumb must fit within the height left over after we subtract the
		 * preferredSize of the buttons and the insets.
		 */
		int sbInsetsH = sbInsets.top + sbInsets.bottom;
		int sbButtonsH = decrButton2H + incrButtonH + decrButtonH
				+ incrButton2H;
		float trackH = sbSize.height - (sbInsetsH + sbButtonsH);

		/*
		 * Compute the height and origin of the thumb. The case where the thumb
		 * is at the bottom edge is handled specially to avoid numerical
		 * problems in computing thumbY. Enforce the thumbs min/max dimensions.
		 * If the thumb doesn't fit in the track (trackH) we'll hide it later.
		 */
		float min = sb.getMinimum();
		float extent = sb.getVisibleAmount();
		float range = sb.getMaximum() - min;
		float value = sb.getValue();

		int thumbH = (range <= 0) ? this.getMaximumThumbSize().height
				: (int) (trackH * (extent / range));
		thumbH = Math.max(thumbH, this.getMinimumThumbSize().height);
		thumbH = Math.min(thumbH, this.getMaximumThumbSize().height);

		int thumbY = decrButton2Y - thumbH;
		if (value < (sb.getMaximum() - sb.getVisibleAmount())) {
			float thumbRange = trackH - thumbH;
			thumbY = (int) (0.5f + (thumbRange * ((value - min) / (range - extent))));
			thumbY += incrButton2Y + incrButton2H;
		}

		/*
		 * If the buttons don't fit, allocate half of the available space to
		 * each and move the lower one (incrButton) down.
		 */
		int sbAvailButtonH = (sbSize.height - sbInsetsH);
		if (sbAvailButtonH < sbButtonsH) {
			incrButtonH = decrButton2H = decrButtonH = incrButton2H = sbAvailButtonH / 4;
			incrButtonY = sbSize.height - (sbInsets.bottom + incrButtonH);
		}
		this.decrButton.setBounds(itemX, decrButtonY, itemW, decrButtonH);
		this.mySecondDecreaseButton.setBounds(itemX,
				incrButtonY - decrButton2H, itemW, decrButton2H);
		this.incrButton.setBounds(itemX, incrButtonY - 1, itemW,
				incrButtonH + 1);
		this.mySecondIncreaseButton.setBounds(itemX, decrButtonY + decrButtonH
				- 1, itemW, incrButton2H + 1);

		/*
		 * Update the trackRect field.
		 */
		int itrackY = incrButton2Y + incrButton2H;
		int itrackH = decrButton2Y - itrackY;
		this.trackRect.setBounds(itemX, itrackY, itemW, itrackH);

		/*
		 * If the thumb isn't going to fit, zero it's bounds. Otherwise make
		 * sure it fits between the buttons. Note that setting the thumbs bounds
		 * will cause a repaint.
		 */
		if (thumbH >= (int) trackH) {
			this.setThumbBounds(0, 0, 0, 0);
		} else {
			if ((thumbY + thumbH) > decrButton2Y) {
				thumbY = decrButton2Y - thumbH;
			}
			if (thumbY < (incrButton2Y + incrButton2H)) {
				thumbY = incrButton2Y + incrButton2H + 1;
			}
			this.setThumbBounds(itemX, thumbY, itemW, thumbH);
		}
	}

	/**
	 * Lays out the horizontal scroll bar when the button policy is
	 * {@link SubstanceConstants.ScrollPaneButtonPolicyKind#ADJACENT}.
	 * 
	 * @param sb
	 *            Scroll bar.
	 */
	protected void layoutHScrollbarAdjacent(JScrollBar sb) {
		Dimension sbSize = sb.getSize();
		Insets sbInsets = sb.getInsets();

		/*
		 * Height and top edge of the buttons and thumb.
		 */
		int itemH = sbSize.height - (sbInsets.top + sbInsets.bottom);
		int itemY = sbInsets.top;

		boolean ltr = sb.getComponentOrientation().isLeftToRight();

		/*
		 * Nominal locations of the buttons, assuming their preferred size will
		 * fit.
		 */
		int decrButton2W = itemH;
		int incrButtonW = itemH;
		int incrButtonX = ltr ? sbSize.width - (sbInsets.right + incrButtonW)
				: sbInsets.left;
		int decrButton2X = ltr ? incrButtonX - decrButton2W : incrButtonX
				+ decrButton2W;

		/*
		 * The thumb must fit within the width left over after we subtract the
		 * preferredSize of the buttons and the insets.
		 */
		int sbInsetsW = sbInsets.left + sbInsets.right;
		int sbButtonsW = decrButton2W + incrButtonW;
		float trackW = sbSize.width - (sbInsetsW + sbButtonsW);

		/*
		 * Compute the width and origin of the thumb. Enforce the thumbs min/max
		 * dimensions. The case where the thumb is at the right edge is handled
		 * specially to avoid numerical problems in computing thumbX. If the
		 * thumb doesn't fit in the track (trackH) we'll hide it later.
		 */
		float min = sb.getMinimum();
		float max = sb.getMaximum();
		float extent = sb.getVisibleAmount();
		float range = max - min;
		float value = sb.getValue();

		int thumbW = (range <= 0) ? this.getMaximumThumbSize().width
				: (int) (trackW * (extent / range));
		thumbW = Math.max(thumbW, this.getMinimumThumbSize().width);
		thumbW = Math.min(thumbW, this.getMaximumThumbSize().width);

		int thumbX = ltr ? decrButton2X - thumbW : sbInsets.left;
		if (value < (max - sb.getVisibleAmount())) {
			float thumbRange = trackW - thumbW;
			if (ltr) {
				thumbX = (int) (0.5f + (thumbRange * ((value - min) / (range - extent))));
			} else {
				thumbX = (int) (0.5f + (thumbRange * ((max - extent - value) / (range - extent))));
				thumbX += decrButton2X + decrButton2W;
			}
		}

		/*
		 * If the buttons don't fit, allocate half of the available space to
		 * each and move the right one over.
		 */
		int sbAvailButtonW = (sbSize.width - sbInsetsW);
		if (sbAvailButtonW < sbButtonsW) {
			incrButtonW = decrButton2W = sbAvailButtonW / 2;
			incrButtonX = ltr ? sbSize.width - (sbInsets.right + incrButtonW)
					: sbInsets.left;
		}

		this.mySecondDecreaseButton.setBounds(decrButton2X + (ltr ? 0 : -1),
				itemY, decrButton2W + 1, itemH);
		this.incrButton.setBounds(incrButtonX, itemY, incrButtonW, itemH);
		this.decrButton.setBounds(0, 0, 0, 0);
		this.mySecondIncreaseButton.setBounds(0, 0, 0, 0);

		/*
		 * Update the trackRect field.
		 */
		if (ltr) {
			int itrackX = sbInsets.left;
			int itrackW = decrButton2X - itrackX;
			this.trackRect.setBounds(itrackX, itemY, itrackW, itemH);
		} else {
			int itrackX = decrButton2X + decrButton2W;
			int itrackW = sbSize.width - itrackX;
			this.trackRect.setBounds(itrackX, itemY, itrackW, itemH);
		}

		/*
		 * Make sure the thumb fits between the buttons. Note that setting the
		 * thumbs bounds causes a repaint.
		 */
		if (thumbW >= (int) trackW) {
			this.setThumbBounds(0, 0, 0, 0);
		} else {
			if (ltr) {
				if (thumbX + thumbW > decrButton2X) {
					thumbX = decrButton2X - thumbW;
				}
				if (thumbX < 0) {
					thumbX = 1;
				}
			} else {
				if (thumbX + thumbW > (sbSize.width - sbInsets.left)) {
					thumbX = sbSize.width - sbInsets.left - thumbW;
				}
				if (thumbX < (decrButton2X + decrButton2W)) {
					thumbX = decrButton2X + decrButton2W + 1;
				}
			}
			this.setThumbBounds(thumbX, itemY, thumbW, itemH);
		}
	}

	/**
	 * Lays out the horizontal scroll bar when the button policy is
	 * {@link SubstanceConstants.ScrollPaneButtonPolicyKind#NONE}.
	 * 
	 * @param sb
	 *            Scroll bar.
	 */
	protected void layoutHScrollbarNone(JScrollBar sb) {
		Dimension sbSize = sb.getSize();
		Insets sbInsets = sb.getInsets();

		/*
		 * Height and top edge of the buttons and thumb.
		 */
		int itemH = sbSize.height - (sbInsets.top + sbInsets.bottom);
		int itemY = sbInsets.top;

		boolean ltr = sb.getComponentOrientation().isLeftToRight();

		/*
		 * Nominal locations of the buttons, assuming their preferred size will
		 * fit.
		 */
		int decrButton2W = 0;
		int incrButtonW = 0;
		int incrButtonX = ltr ? sbSize.width - (sbInsets.right + incrButtonW)
				: sbInsets.left;
		int decrButton2X = ltr ? incrButtonX - decrButton2W : incrButtonX
				+ decrButton2W;

		/*
		 * The thumb must fit within the width left over after we subtract the
		 * preferredSize of the buttons and the insets.
		 */
		int sbInsetsW = sbInsets.left + sbInsets.right;
		int sbButtonsW = decrButton2W + incrButtonW;
		float trackW = sbSize.width - (sbInsetsW + sbButtonsW);

		/*
		 * Compute the width and origin of the thumb. Enforce the thumbs min/max
		 * dimensions. The case where the thumb is at the right edge is handled
		 * specially to avoid numerical problems in computing thumbX. If the
		 * thumb doesn't fit in the track (trackH) we'll hide it later.
		 */
		float min = sb.getMinimum();
		float max = sb.getMaximum();
		float extent = sb.getVisibleAmount();
		float range = max - min;
		float value = sb.getValue();

		int thumbW = (range <= 0) ? this.getMaximumThumbSize().width
				: (int) (trackW * (extent / range));
		thumbW = Math.max(thumbW, this.getMinimumThumbSize().width);
		thumbW = Math.min(thumbW, this.getMaximumThumbSize().width);

		int thumbX = ltr ? decrButton2X - thumbW : sbInsets.left;
		if (value < (max - sb.getVisibleAmount())) {
			float thumbRange = trackW - thumbW;
			if (ltr) {
				thumbX = (int) (0.5f + (thumbRange * ((value - min) / (range - extent))));
			} else {
				thumbX = (int) (0.5f + (thumbRange * ((max - extent - value) / (range - extent))));
				thumbX += decrButton2X + decrButton2W;
			}
		}

		/*
		 * If the buttons don't fit, allocate half of the available space to
		 * each and move the right one over.
		 */
		int sbAvailButtonW = (sbSize.width - sbInsetsW);
		if (sbAvailButtonW < sbButtonsW) {
			incrButtonW = decrButton2W = 0;
			// incrButtonX = ltr ? sbSize.width - (sbInsets.right + incrButtonW)
			// : sbInsets.left;
		}

		this.incrButton.setBounds(0, 0, 0, 0);
		this.decrButton.setBounds(0, 0, 0, 0);
		this.mySecondIncreaseButton.setBounds(0, 0, 0, 0);
		this.mySecondDecreaseButton.setBounds(0, 0, 0, 0);

		/*
		 * Update the trackRect field.
		 */
		if (ltr) {
			int itrackX = sbInsets.left;
			int itrackW = decrButton2X - itrackX;
			this.trackRect.setBounds(itrackX, itemY, itrackW, itemH);
		} else {
			int itrackX = decrButton2X + decrButton2W;
			int itrackW = sbSize.width - itrackX;
			this.trackRect.setBounds(itrackX, itemY, itrackW, itemH);
		}

		/*
		 * Make sure the thumb fits between the buttons. Note that setting the
		 * thumbs bounds causes a repaint.
		 */
		if (thumbW >= (int) trackW) {
			this.setThumbBounds(0, 0, 0, 0);
		} else {
			if (ltr) {
				if (thumbX + thumbW > decrButton2X) {
					thumbX = decrButton2X - thumbW;
				}
				if (thumbX < 0) {
					thumbX = 1;
				}
			} else {
				if (thumbX + thumbW > (sbSize.width - sbInsets.left)) {
					thumbX = sbSize.width - sbInsets.left - thumbW;
				}
				if (thumbX < (decrButton2X + decrButton2W)) {
					thumbX = decrButton2X + decrButton2W + 1;
				}
			}
			this.setThumbBounds(thumbX, itemY, thumbW, itemH);
		}
	}

	/**
	 * Lays out the horizontal scroll bar when the button policy is
	 * {@link SubstanceConstants.ScrollPaneButtonPolicyKind#MULTIPLE}.
	 * 
	 * @param sb
	 *            Scroll bar.
	 */
	protected void layoutHScrollbarMultiple(JScrollBar sb) {
		Dimension sbSize = sb.getSize();
		Insets sbInsets = sb.getInsets();

		/*
		 * Height and top edge of the buttons and thumb.
		 */
		int itemH = sbSize.height - (sbInsets.top + sbInsets.bottom);
		int itemY = sbInsets.top;

		boolean ltr = sb.getComponentOrientation().isLeftToRight();

		/*
		 * Nominal locations of the buttons, assuming their preferred size will
		 * fit.
		 */
		int decrButton2W = itemH;
		int decrButtonW = itemH;
		int incrButtonW = itemH;
		int incrButtonX = ltr ? sbSize.width - (sbInsets.right + incrButtonW)
				: sbInsets.left;
		int decrButton2X = ltr ? incrButtonX - decrButton2W : incrButtonX
				+ decrButton2W;
		int decrButtonX = ltr ? sbInsets.left : sbSize.width - sbInsets.right
				- decrButtonW;

		/*
		 * The thumb must fit within the width left over after we subtract the
		 * preferredSize of the buttons and the insets.
		 */
		int sbInsetsW = sbInsets.left + sbInsets.right;
		int sbButtonsW = decrButton2W + incrButtonW + decrButtonW;
		float trackW = sbSize.width - (sbInsetsW + sbButtonsW);

		/*
		 * Compute the width and origin of the thumb. Enforce the thumbs min/max
		 * dimensions. The case where the thumb is at the right edge is handled
		 * specially to avoid numerical problems in computing thumbX. If the
		 * thumb doesn't fit in the track (trackH) we'll hide it later.
		 */
		float min = sb.getMinimum();
		float max = sb.getMaximum();
		float extent = sb.getVisibleAmount();
		float range = max - min;
		float value = sb.getValue();

		int thumbW = (range <= 0) ? this.getMaximumThumbSize().width
				: (int) (trackW * (extent / range));
		thumbW = Math.max(thumbW, this.getMinimumThumbSize().width);
		thumbW = Math.min(thumbW, this.getMaximumThumbSize().width);

		int thumbX = ltr ? decrButton2X - thumbW : sbInsets.left;
		if (value < (max - sb.getVisibleAmount())) {
			float thumbRange = trackW - thumbW;
			if (ltr) {
				thumbX = (int) (0.5f + (thumbRange * ((value - min) / (range - extent))));
				thumbX += decrButtonX + decrButtonW;
			} else {
				thumbX = (int) (0.5f + (thumbRange * ((max - extent - value) / (range - extent))));
				thumbX += decrButton2X + decrButton2W;
			}
		}

		/*
		 * If the buttons don't fit, allocate half of the available space to
		 * each and move the right one over.
		 */
		int sbAvailButtonW = (sbSize.width - sbInsetsW);
		if (sbAvailButtonW < sbButtonsW) {
			incrButtonW = decrButton2W = decrButtonW = sbAvailButtonW / 2;
			incrButtonX = ltr ? sbSize.width - (sbInsets.right + incrButtonW)
					: sbInsets.left;
		}

		this.mySecondDecreaseButton.setBounds(decrButton2X + (ltr ? 0 : -1),
				itemY, decrButton2W + 1, itemH);
		this.incrButton.setBounds(incrButtonX, itemY, incrButtonW, itemH);
		this.decrButton.setBounds(decrButtonX, itemY, decrButtonW, itemH);
		this.mySecondIncreaseButton.setBounds(0, 0, 0, 0);

		/*
		 * Update the trackRect field.
		 */
		if (ltr) {
			int itrackX = decrButtonX + decrButtonW;
			int itrackW = decrButton2X - itrackX;
			this.trackRect.setBounds(itrackX, itemY, itrackW, itemH);
		} else {
			int itrackX = decrButton2X + decrButton2W;
			int itrackW = decrButtonX - itrackX;
			this.trackRect.setBounds(itrackX, itemY, itrackW, itemH);
		}

		/*
		 * Make sure the thumb fits between the buttons. Note that setting the
		 * thumbs bounds causes a repaint.
		 */
		if (thumbW >= (int) trackW) {
			this.setThumbBounds(0, 0, 0, 0);
		} else {
			if (ltr) {
				if (thumbX + thumbW > decrButton2X) {
					thumbX = decrButton2X - thumbW;
				}
				if (thumbX < (decrButtonX + decrButtonW)) {
					thumbX = decrButtonX + decrButtonW + 1;
				}
			} else {
				if (thumbX + thumbW > decrButtonX) {
					thumbX = decrButtonX - thumbW;
				}
				if (thumbX < (decrButton2X + decrButton2W)) {
					thumbX = decrButton2X + decrButton2W + 1;
				}
			}
			this.setThumbBounds(thumbX, itemY, thumbW, itemH);
		}
	}

	/**
	 * Lays out the horizontal scroll bar when the button policy is
	 * {@link SubstanceConstants.ScrollPaneButtonPolicyKind#MULTIPLE}.
	 * 
	 * @param sb
	 *            Scroll bar.
	 */
	protected void layoutHScrollbarMultipleBoth(JScrollBar sb) {
		Dimension sbSize = sb.getSize();
		Insets sbInsets = sb.getInsets();

		/*
		 * Height and top edge of the buttons and thumb.
		 */
		int itemH = sbSize.height - (sbInsets.top + sbInsets.bottom);
		int itemY = sbInsets.top;

		boolean ltr = sb.getComponentOrientation().isLeftToRight();

		/*
		 * Nominal locations of the buttons, assuming their preferred size will
		 * fit.
		 */
		int decrButton2W = itemH;
		int incrButton2W = itemH;
		int decrButtonW = itemH;
		int incrButtonW = itemH;

		int incrButtonX = ltr ? sbSize.width - (sbInsets.right + incrButtonW)
				: sbInsets.left;
		int decrButton2X = ltr ? incrButtonX - decrButton2W : incrButtonX
				+ decrButton2W;
		int decrButtonX = ltr ? sbInsets.left : sbSize.width - sbInsets.right
				- decrButtonW;
		int incrButton2X = ltr ? decrButtonX + decrButtonW : decrButtonX
				- incrButton2W;

		/*
		 * The thumb must fit within the width left over after we subtract the
		 * preferredSize of the buttons and the insets.
		 */
		int sbInsetsW = sbInsets.left + sbInsets.right;
		int sbButtonsW = decrButton2W + incrButtonW + decrButtonW
				+ incrButton2W;
		float trackW = sbSize.width - (sbInsetsW + sbButtonsW);

		/*
		 * Compute the width and origin of the thumb. Enforce the thumbs min/max
		 * dimensions. The case where the thumb is at the right edge is handled
		 * specially to avoid numerical problems in computing thumbX. If the
		 * thumb doesn't fit in the track (trackH) we'll hide it later.
		 */
		float min = sb.getMinimum();
		float max = sb.getMaximum();
		float extent = sb.getVisibleAmount();
		float range = max - min;
		float value = sb.getValue();

		int thumbW = (range <= 0) ? this.getMaximumThumbSize().width
				: (int) (trackW * (extent / range));
		thumbW = Math.max(thumbW, this.getMinimumThumbSize().width);
		thumbW = Math.min(thumbW, this.getMaximumThumbSize().width);

		int thumbX = ltr ? decrButton2X - thumbW : sbInsets.left;
		if (value < (max - sb.getVisibleAmount())) {
			float thumbRange = trackW - thumbW;
			if (ltr) {
				thumbX = (int) (0.5f + (thumbRange * ((value - min) / (range - extent))));
				thumbX += incrButton2X + incrButton2W;
			} else {
				thumbX = (int) (0.5f + (thumbRange * ((max - extent - value) / (range - extent))));
				thumbX += decrButton2X + decrButton2W;
			}
		}

		/*
		 * If the buttons don't fit, allocate half of the available space to
		 * each and move the right one over.
		 */
		int sbAvailButtonW = (sbSize.width - sbInsetsW);
		if (sbAvailButtonW < sbButtonsW) {
			incrButtonW = decrButton2W = decrButtonW = incrButton2W = sbAvailButtonW / 4;
			incrButtonX = ltr ? sbSize.width - (sbInsets.right + incrButtonW)
					: sbInsets.left;
		}

		this.mySecondDecreaseButton.setBounds(decrButton2X + (ltr ? 0 : -1),
				itemY, decrButton2W + 1, itemH);
		this.mySecondIncreaseButton.setBounds(incrButton2X + (ltr ? -1 : 0),
				itemY, incrButton2W + 1, itemH);
		this.incrButton.setBounds(incrButtonX, itemY, incrButtonW, itemH);
		this.decrButton.setBounds(decrButtonX, itemY, decrButtonW, itemH);

		/*
		 * Update the trackRect field.
		 */
		if (ltr) {
			int itrackX = incrButton2X + incrButton2W;
			int itrackW = decrButton2X - itrackX;
			this.trackRect.setBounds(itrackX, itemY, itrackW, itemH);
		} else {
			int itrackX = decrButton2X + decrButton2W;
			int itrackW = incrButton2X - itrackX;
			this.trackRect.setBounds(itrackX, itemY, itrackW, itemH);
		}

		/*
		 * Make sure the thumb fits between the buttons. Note that setting the
		 * thumbs bounds causes a repaint.
		 */
		if (thumbW >= (int) trackW) {
			this.setThumbBounds(0, 0, 0, 0);
		} else {
			if (ltr) {
				if (thumbX + thumbW > decrButton2X) {
					thumbX = decrButton2X - thumbW;
				}
				if (thumbX < (incrButton2X + incrButton2W)) {
					thumbX = incrButton2X + incrButton2W + 1;
				}
			} else {
				if (thumbX + thumbW > incrButton2X) {
					thumbX = incrButton2X - thumbW;
				}
				if (thumbX < (decrButton2X + decrButton2W)) {
					thumbX = decrButton2X + decrButton2W + 1;
				}
			}
			this.setThumbBounds(thumbX, itemY, thumbW, itemH);
		}
	}

	/**
	 * Returns the memory usage string.
	 * 
	 * @return The memory usage string.
	 */
	public static String getMemoryUsage() {
		StringBuffer sb = new StringBuffer();
		sb.append("SubstanceScrollBarUI: \n");
		sb.append("\t" + thumbHorizontalMap.size() + " thumb horizontal, "
				+ thumbVerticalMap.size() + " thumb vertical");
		sb.append("\t" + trackHorizontalMap.size() + " track horizontal, "
				+ trackVerticalMap.size() + " track vertical");
		sb.append("\t" + trackFullHorizontalMap.size()
				+ " track full horizontal, " + trackFullVerticalMap.size()
				+ " track full vertical");
		return sb.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.basic.BasicScrollBarUI#createTrackListener()
	 */
	@Override
	protected TrackListener createTrackListener() {
		return new SubstanceTrackListener();
	}

	/**
	 * Track mouse drags. Had to take this one from BasicScrollBarUI since the
	 * setValueForm method is private.
	 */
	protected class SubstanceTrackListener extends TrackListener {
		/**
		 * Current scroll direction.
		 */
		private transient int direction = +1;

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * javax.swing.plaf.basic.BasicScrollBarUI$TrackListener#mouseReleased
		 * (java.awt.event.MouseEvent)
		 */
		@Override
		public void mouseReleased(MouseEvent e) {
			if (SubstanceScrollBarUI.this.isDragging) {
				SubstanceScrollBarUI.this.updateThumbState(e.getX(), e.getY());
			}
			if (SwingUtilities.isRightMouseButton(e)
					|| (!SubstanceScrollBarUI.this
							.getSupportsAbsolutePositioning() && SwingUtilities
							.isMiddleMouseButton(e)))
				return;
			if (!SubstanceScrollBarUI.this.scrollbar.isEnabled())
				return;

			Rectangle r = SubstanceScrollBarUI.this.getTrackBounds();
			SubstanceScrollBarUI.this.scrollbar.repaint(r.x, r.y, r.width,
					r.height);

			SubstanceScrollBarUI.this.trackHighlight = NO_HIGHLIGHT;
			SubstanceScrollBarUI.this.isDragging = false;
			this.offset = 0;
			SubstanceScrollBarUI.this.scrollTimer.stop();
			SubstanceScrollBarUI.this.scrollbar.setValueIsAdjusting(false);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * javax.swing.plaf.basic.BasicScrollBarUI$TrackListener#mousePressed
		 * (java.awt.event.MouseEvent)
		 */
		@Override
		public void mousePressed(MouseEvent e) {
			// If the mouse is pressed above the "thumb" component then reduce
			// the scrollbars value by one page ("page up"), otherwise increase
			// it by one page. If there is no thumb then page up if the mouse is
			// in the upper half of the track.
			if (SwingUtilities.isRightMouseButton(e)
					|| (!SubstanceScrollBarUI.this
							.getSupportsAbsolutePositioning() && SwingUtilities
							.isMiddleMouseButton(e)))
				return;
			if (!SubstanceScrollBarUI.this.scrollbar.isEnabled())
				return;

			if (!SubstanceScrollBarUI.this.scrollbar.hasFocus()
					&& SubstanceScrollBarUI.this.scrollbar
							.isRequestFocusEnabled()) {
				SubstanceScrollBarUI.this.scrollbar.requestFocus();
			}

			SubstanceScrollBarUI.this.scrollbar.setValueIsAdjusting(true);

			this.currentMouseX = e.getX();
			this.currentMouseY = e.getY();

			// Clicked in the Thumb area?
			if (SubstanceScrollBarUI.this.getThumbBounds().contains(
					this.currentMouseX, this.currentMouseY)) {
				switch (SubstanceScrollBarUI.this.scrollbar.getOrientation()) {
				case JScrollBar.VERTICAL:
					this.offset = this.currentMouseY
							- SubstanceScrollBarUI.this.getThumbBounds().y;
					break;
				case JScrollBar.HORIZONTAL:
					this.offset = this.currentMouseX
							- SubstanceScrollBarUI.this.getThumbBounds().x;
					break;
				}
				SubstanceScrollBarUI.this.isDragging = true;
				return;
			} else if (SubstanceScrollBarUI.this
					.getSupportsAbsolutePositioning()
					&& SwingUtilities.isMiddleMouseButton(e)) {
				switch (SubstanceScrollBarUI.this.scrollbar.getOrientation()) {
				case JScrollBar.VERTICAL:
					this.offset = SubstanceScrollBarUI.this.getThumbBounds().height / 2;
					break;
				case JScrollBar.HORIZONTAL:
					this.offset = SubstanceScrollBarUI.this.getThumbBounds().width / 2;
					break;
				}
				SubstanceScrollBarUI.this.isDragging = true;
				this.setValueFrom(e);
				return;
			}
			SubstanceScrollBarUI.this.isDragging = false;

			Dimension sbSize = SubstanceScrollBarUI.this.scrollbar.getSize();
			this.direction = +1;

			switch (SubstanceScrollBarUI.this.scrollbar.getOrientation()) {
			case JScrollBar.VERTICAL:
				if (SubstanceScrollBarUI.this.getThumbBounds().isEmpty()) {
					int scrollbarCenter = sbSize.height / 2;
					this.direction = (this.currentMouseY < scrollbarCenter) ? -1
							: +1;
				} else {
					int thumbY = SubstanceScrollBarUI.this.getThumbBounds().y;
					this.direction = (this.currentMouseY < thumbY) ? -1 : +1;
				}
				break;
			case JScrollBar.HORIZONTAL:
				if (SubstanceScrollBarUI.this.getThumbBounds().isEmpty()) {
					int scrollbarCenter = sbSize.width / 2;
					this.direction = (this.currentMouseX < scrollbarCenter) ? -1
							: +1;
				} else {
					int thumbX = SubstanceScrollBarUI.this.getThumbBounds().x;
					this.direction = (this.currentMouseX < thumbX) ? -1 : +1;
				}
				if (!SubstanceScrollBarUI.this.scrollbar
						.getComponentOrientation().isLeftToRight()) {
					this.direction = -this.direction;
				}
				break;
			}
			SubstanceScrollBarUI.this.scrollByBlock(this.direction);

			SubstanceScrollBarUI.this.scrollTimer.stop();
			SubstanceScrollBarUI.this.scrollListener
					.setDirection(this.direction);
			SubstanceScrollBarUI.this.scrollListener.setScrollByBlock(true);
			this.startScrollTimerIfNecessary();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * javax.swing.plaf.basic.BasicScrollBarUI$TrackListener#mouseDragged
		 * (java.awt.event.MouseEvent)
		 */
		@Override
		public void mouseDragged(MouseEvent e) {
			// Set the models value to the position of the thumb's top of
			// Vertical scrollbar, or the left/right of Horizontal scrollbar in
			// LTR / RTL scrollbar relative to the origin of
			// the track.
			if (SwingUtilities.isRightMouseButton(e)
					|| (!SubstanceScrollBarUI.this
							.getSupportsAbsolutePositioning() && SwingUtilities
							.isMiddleMouseButton(e)))
				return;
			if (!SubstanceScrollBarUI.this.scrollbar.isEnabled()
					|| SubstanceScrollBarUI.this.getThumbBounds().isEmpty()) {
				return;
			}
			if (SubstanceScrollBarUI.this.isDragging) {
				this.setValueFrom(e);
			} else {
				this.currentMouseX = e.getX();
				this.currentMouseY = e.getY();
				SubstanceScrollBarUI.this.updateThumbState(this.currentMouseX,
						this.currentMouseY);
				this.startScrollTimerIfNecessary();
			}
		}

		/**
		 * Sets the scrollbar value based on the specified mouse event.
		 * 
		 * @param e
		 *            Mouse event.
		 */
		private void setValueFrom(MouseEvent e) {
			boolean active = SubstanceScrollBarUI.this.isThumbRollover();
			BoundedRangeModel model = SubstanceScrollBarUI.this.scrollbar
					.getModel();
			Rectangle thumbR = SubstanceScrollBarUI.this.getThumbBounds();
			int thumbMin = 0, thumbMax = 0, thumbPos;

			ScrollPaneButtonPolicyKind buttonPolicy = SubstanceCoreUtilities
					.getScrollPaneButtonsPolicyKind(SubstanceScrollBarUI.this.scrollbar);

			if (SubstanceScrollBarUI.this.scrollbar.getOrientation() == JScrollBar.VERTICAL) {
				switch (buttonPolicy) {
				case OPPOSITE:
					thumbMin = SubstanceScrollBarUI.this.decrButton.getY()
							+ SubstanceScrollBarUI.this.decrButton.getHeight();
					thumbMax = SubstanceScrollBarUI.this.incrButton.getY()
							- thumbR.height;
					break;
				case ADJACENT:
					thumbMin = 0;
					thumbMax = SubstanceScrollBarUI.this.mySecondDecreaseButton
							.getY()
							- thumbR.height;
					break;
				case NONE:
					thumbMin = 0;
					thumbMax = SubstanceScrollBarUI.this.scrollbar.getSize().height
							- SubstanceScrollBarUI.this.scrollbar.getInsets().bottom
							- thumbR.height;
					break;
				case MULTIPLE:
					thumbMin = SubstanceScrollBarUI.this.decrButton.getY()
							+ SubstanceScrollBarUI.this.decrButton.getHeight();
					thumbMax = SubstanceScrollBarUI.this.mySecondDecreaseButton
							.getY()
							- thumbR.height;
					break;
				case MULTIPLE_BOTH:
					thumbMin = SubstanceScrollBarUI.this.mySecondIncreaseButton
							.getY()
							+ SubstanceScrollBarUI.this.mySecondIncreaseButton
									.getHeight();
					thumbMax = SubstanceScrollBarUI.this.mySecondDecreaseButton
							.getY()
							- thumbR.height;
					break;
				}

				thumbPos = Math.min(thumbMax, Math.max(thumbMin,
						(e.getY() - this.offset)));
				SubstanceScrollBarUI.this.setThumbBounds(thumbR.x, thumbPos,
						thumbR.width, thumbR.height);
			} else {
				if (SubstanceScrollBarUI.this.scrollbar
						.getComponentOrientation().isLeftToRight()) {
					switch (buttonPolicy) {
					case OPPOSITE:
						thumbMin = SubstanceScrollBarUI.this.decrButton.getX()
								+ SubstanceScrollBarUI.this.decrButton
										.getWidth();
						thumbMax = SubstanceScrollBarUI.this.incrButton.getX()
								- thumbR.width;
						break;
					case ADJACENT:
						thumbMin = 0;
						thumbMax = SubstanceScrollBarUI.this.mySecondDecreaseButton
								.getX()
								- thumbR.width;
						break;
					case MULTIPLE:
						thumbMin = SubstanceScrollBarUI.this.decrButton.getX()
								+ SubstanceScrollBarUI.this.decrButton
										.getWidth();
						thumbMax = SubstanceScrollBarUI.this.mySecondDecreaseButton
								.getX()
								- thumbR.width;
						break;
					case MULTIPLE_BOTH:
						thumbMin = SubstanceScrollBarUI.this.mySecondIncreaseButton
								.getX()
								+ SubstanceScrollBarUI.this.mySecondIncreaseButton
										.getWidth();
						thumbMax = SubstanceScrollBarUI.this.mySecondDecreaseButton
								.getX()
								- thumbR.width;
						break;
					case NONE:
						thumbMin = 0;
						thumbMax = SubstanceScrollBarUI.this.scrollbar
								.getSize().width
								- SubstanceScrollBarUI.this.scrollbar
										.getInsets().right - thumbR.width;
						break;
					}
				} else {
					switch (buttonPolicy) {
					case OPPOSITE:
						thumbMin = SubstanceScrollBarUI.this.incrButton.getX()
								+ SubstanceScrollBarUI.this.incrButton
										.getWidth();
						thumbMax = SubstanceScrollBarUI.this.decrButton.getX()
								- thumbR.width;
						break;
					case ADJACENT:
						thumbMin = SubstanceScrollBarUI.this.mySecondDecreaseButton
								.getX()
								+ SubstanceScrollBarUI.this.mySecondDecreaseButton
										.getWidth();
						thumbMax = SubstanceScrollBarUI.this.scrollbar
								.getSize().width
								- SubstanceScrollBarUI.this.scrollbar
										.getInsets().right - thumbR.width;
						break;
					case MULTIPLE:
						thumbMin = SubstanceScrollBarUI.this.mySecondDecreaseButton
								.getX()
								+ SubstanceScrollBarUI.this.mySecondDecreaseButton
										.getWidth();
						thumbMax = SubstanceScrollBarUI.this.decrButton.getX()
								- thumbR.width;
						break;
					case MULTIPLE_BOTH:
						thumbMin = SubstanceScrollBarUI.this.mySecondDecreaseButton
								.getX()
								+ SubstanceScrollBarUI.this.mySecondDecreaseButton
										.getWidth();
						thumbMax = SubstanceScrollBarUI.this.mySecondIncreaseButton
								.getX()
								- thumbR.width;
						break;
					case NONE:
						thumbMin = 0;
						thumbMax = SubstanceScrollBarUI.this.scrollbar
								.getSize().width
								- SubstanceScrollBarUI.this.scrollbar
										.getInsets().right - thumbR.width;
						break;
					}
				}
				// System.out.println(thumbMin + " : " + thumbMax + " : "
				// + (e.getX() - offset));
				thumbPos = Math.min(thumbMax, Math.max(thumbMin,
						(e.getX() - this.offset)));
				SubstanceScrollBarUI.this.setThumbBounds(thumbPos, thumbR.y,
						thumbR.width, thumbR.height);
			}

			/*
			 * Set the scrollbars value. If the thumb has reached the end of the
			 * scrollbar, then just set the value to its maximum. Otherwise
			 * compute the value as accurately as possible.
			 */
			if (thumbPos == thumbMax) {
				if (SubstanceScrollBarUI.this.scrollbar.getOrientation() == JScrollBar.VERTICAL
						|| SubstanceScrollBarUI.this.scrollbar
								.getComponentOrientation().isLeftToRight()) {
					SubstanceScrollBarUI.this.scrollbar.setValue(model
							.getMaximum()
							- model.getExtent());
				} else {
					SubstanceScrollBarUI.this.scrollbar.setValue(model
							.getMinimum());
				}
			} else {
				float valueMax = model.getMaximum() - model.getExtent();
				float valueRange = valueMax - model.getMinimum();
				float thumbValue = thumbPos - thumbMin;
				float thumbRange = thumbMax - thumbMin;
				int value;
				if (SubstanceScrollBarUI.this.scrollbar.getOrientation() == JScrollBar.VERTICAL
						|| SubstanceScrollBarUI.this.scrollbar
								.getComponentOrientation().isLeftToRight()) {
					value = (int) (0.5 + ((thumbValue / thumbRange) * valueRange));
				} else {
					value = (int) (0.5 + (((thumbMax - thumbPos) / thumbRange) * valueRange));
				}

				SubstanceScrollBarUI.this.scrollbar.setValue(value
						+ model.getMinimum());
			}
			SubstanceScrollBarUI.this.setThumbRollover(active);
		}

		/**
		 * If necessary, starts the scroll timer.
		 */
		private void startScrollTimerIfNecessary() {
			if (SubstanceScrollBarUI.this.scrollTimer.isRunning()) {
				return;
			}
			switch (SubstanceScrollBarUI.this.scrollbar.getOrientation()) {
			case JScrollBar.VERTICAL:
				if (this.direction > 0) {
					if (SubstanceScrollBarUI.this.getThumbBounds().y
							+ SubstanceScrollBarUI.this.getThumbBounds().height < ((SubstanceTrackListener) SubstanceScrollBarUI.this.trackListener).currentMouseY) {
						SubstanceScrollBarUI.this.scrollTimer.start();
					}
				} else if (SubstanceScrollBarUI.this.getThumbBounds().y > ((SubstanceTrackListener) SubstanceScrollBarUI.this.trackListener).currentMouseY) {
					SubstanceScrollBarUI.this.scrollTimer.start();
				}
				break;
			case JScrollBar.HORIZONTAL:
				if (this.direction > 0) {
					if (SubstanceScrollBarUI.this.getThumbBounds().x
							+ SubstanceScrollBarUI.this.getThumbBounds().width < ((SubstanceTrackListener) SubstanceScrollBarUI.this.trackListener).currentMouseX) {
						SubstanceScrollBarUI.this.scrollTimer.start();
					}
				} else if (SubstanceScrollBarUI.this.getThumbBounds().x > ((SubstanceTrackListener) SubstanceScrollBarUI.this.trackListener).currentMouseX) {
					SubstanceScrollBarUI.this.scrollTimer.start();
				}
				break;
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * javax.swing.plaf.basic.BasicScrollBarUI$TrackListener#mouseMoved(
		 * java.awt.event.MouseEvent)
		 */
		@Override
		public void mouseMoved(MouseEvent e) {
			if (!SubstanceScrollBarUI.this.isDragging) {
				SubstanceScrollBarUI.this.updateThumbState(e.getX(), e.getY());
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * javax.swing.plaf.basic.BasicScrollBarUI$TrackListener#mouseExited
		 * (java.awt.event.MouseEvent)
		 */
		@Override
		public void mouseExited(MouseEvent e) {
			if (!SubstanceScrollBarUI.this.isDragging) {
				SubstanceScrollBarUI.this.setThumbRollover(false);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.basic.BasicScrollBarUI#createArrowButtonListener()
	 */
	@Override
	protected ArrowButtonListener createArrowButtonListener() {
		return new SubstanceArrowButtonListener();
	}

	/**
	 * Listener on arrow buttons. Need to override the super implementation for
	 * the {@link ScrollPaneButtonPolicyKind#MULTIPLE_BOTH} policy.
	 * 
	 * @author Kirill Grouchnikov
	 */
	protected class SubstanceArrowButtonListener extends ArrowButtonListener {
		/**
		 * Because we are handling both mousePressed and Actions we need to make
		 * sure we don't fire under both conditions. (keyfocus on scrollbars
		 * causes action without mousePress
		 */
		boolean handledEvent;

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * javax.swing.plaf.basic.BasicScrollBarUI$ArrowButtonListener#mousePressed
		 * (java.awt.event.MouseEvent)
		 */
		@Override
		public void mousePressed(MouseEvent e) {
			if (!SubstanceScrollBarUI.this.scrollbar.isEnabled()) {
				return;
			}
			// not an unmodified left mouse button
			// if(e.getModifiers() != InputEvent.BUTTON1_MASK) {return; }
			if (!SwingUtilities.isLeftMouseButton(e)) {
				return;
			}

			int direction = ((e.getSource() == SubstanceScrollBarUI.this.incrButton) || (e
					.getSource() == SubstanceScrollBarUI.this.mySecondIncreaseButton)) ? 1
					: -1;

			SubstanceScrollBarUI.this.scrollByUnit(direction);
			SubstanceScrollBarUI.this.scrollTimer.stop();
			SubstanceScrollBarUI.this.scrollListener.setDirection(direction);
			SubstanceScrollBarUI.this.scrollListener.setScrollByBlock(false);
			SubstanceScrollBarUI.this.scrollTimer.start();

			this.handledEvent = true;
			if (!SubstanceScrollBarUI.this.scrollbar.hasFocus()
					&& SubstanceScrollBarUI.this.scrollbar
							.isRequestFocusEnabled()) {
				SubstanceScrollBarUI.this.scrollbar.requestFocus();
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * javax.swing.plaf.basic.BasicScrollBarUI$ArrowButtonListener#mouseReleased
		 * (java.awt.event.MouseEvent)
		 */
		@Override
		public void mouseReleased(MouseEvent e) {
			SubstanceScrollBarUI.this.scrollTimer.stop();
			this.handledEvent = false;
			SubstanceScrollBarUI.this.scrollbar.setValueIsAdjusting(false);
		}
	}

	/**
	 * Updates the thumb state based on the coordinates.
	 * 
	 * @param x
	 *            X coordinate.
	 * @param y
	 *            Y coordinate.
	 */
	private void updateThumbState(int x, int y) {
		Rectangle rect = this.getThumbBounds();

		this.setThumbRollover(rect.contains(x, y));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.plaf.basic.BasicScrollBarUI#getPreferredSize(javax.swing.
	 * JComponent)
	 */
	@Override
	public Dimension getPreferredSize(JComponent c) {
		if (scrollbar.getOrientation() == JScrollBar.VERTICAL) {
			return new Dimension(scrollBarWidth, Math.max(48,
					5 * scrollBarWidth));
		} else {
			return new Dimension(Math.max(48, 5 * scrollBarWidth),
					scrollBarWidth);
		}
	}
}