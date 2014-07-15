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
package org.jvnet.substance.utils.icon;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.util.*;

import javax.swing.*;
import javax.swing.plaf.SliderUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicSliderUI;

import org.jvnet.lafwidget.animation.*;
import org.jvnet.substance.*;
import org.jvnet.substance.api.*;
import org.jvnet.substance.painter.border.SubstanceBorderPainter;
import org.jvnet.substance.painter.gradient.SubstanceGradientPainter;
import org.jvnet.substance.utils.*;

/**
 * Icon factory for dynamically-changing icons. This class is <b>for internal
 * use only</b>.
 * 
 * @author Kirill Grouchnikov
 */
public class SubstanceIconFactory {
	/**
	 * Icons for horizontal slider in {@link SubstanceSliderUI}.
	 */
	private static LazyResettableHashMap<Icon> sliderHorizontalIcons = new LazyResettableHashMap<Icon>(
			"SubstanceIconFactory.sliderHorizontalIcon");

	/**
	 * Icons for horizontal slider in {@link SubstanceSliderUI}.
	 */
	private static LazyResettableHashMap<Icon> sliderRoundIcons = new LazyResettableHashMap<Icon>(
			"SubstanceIconFactory.sliderRoundIcon");

	/**
	 * Icons for vertical slider in {@link SubstanceSliderUI}.
	 */
	private static LazyResettableHashMap<Icon> sliderVerticalIcons = new LazyResettableHashMap<Icon>(
			"SubstanceIconFactory.sliderVerticalIcon");

	/**
	 * Icons for tree collapse / expand in {@link SubstanceTreeUI}.
	 */
	private static LazyResettableHashMap<Icon> treeIcons = new LazyResettableHashMap<Icon>(
			"SubstanceIconFactory.treeIcon");

	/**
	 * Retrieves icon for horizontal slider in {@link SubstanceSliderUI}.
	 * 
	 * @param size
	 *            The size of the icon to retrieve.
	 * @param isMirrorred
	 *            Indication whether the icon should be mirrored.
	 * @return Icon for horizontal slider in {@link SubstanceSliderUI}.
	 */
	public static Icon getSliderHorizontalIcon(int size, boolean isMirrorred) {
		HashMapKey key = SubstanceCoreUtilities.getHashKey(size, isMirrorred);
		if (SubstanceIconFactory.sliderHorizontalIcons.get(key) == null) {
			Icon icon = new SliderHorizontalIcon(size, isMirrorred);
			SubstanceIconFactory.sliderHorizontalIcons.put(key, icon);
		}
		return SubstanceIconFactory.sliderHorizontalIcons.get(key);
	}

	/**
	 * Retrieves round icon for slider in {@link SubstanceSliderUI}.
	 * 
	 * @param size
	 *            The size of the icon to retrieve.
	 * @return Round icon for slider in {@link SubstanceSliderUI}.
	 */
	public static Icon getSliderRoundIcon(int size) {
		HashMapKey key = SubstanceCoreUtilities.getHashKey(size);
		if (SubstanceIconFactory.sliderRoundIcons.get(key) == null) {
			Icon icon = new SliderRoundIcon(size);
			SubstanceIconFactory.sliderRoundIcons.put(key, icon);
		}
		return SubstanceIconFactory.sliderRoundIcons.get(key);
	}

	/**
	 * Retrieves icon for vertical slider in {@link SubstanceSliderUI}.
	 * 
	 * @param size
	 *            The size of the icon to retrieve.
	 * @param isMirrorred
	 *            Indication whether the icon should be mirrored.
	 * @return Icon for vertical slider in {@link SubstanceSliderUI}.
	 */
	public static Icon getSliderVerticalIcon(int size, boolean isMirrorred) {
		HashMapKey key = SubstanceCoreUtilities.getHashKey(size, isMirrorred);
		if (SubstanceIconFactory.sliderVerticalIcons.get(key) == null) {
			Icon icon = new SliderVerticalIcon(size, isMirrorred);
			SubstanceIconFactory.sliderVerticalIcons.put(key, icon);
		}
		return SubstanceIconFactory.sliderVerticalIcons.get(key);
	}

	public static Icon getTreeIcon(JTree tree, boolean isCollapsed) {
		int fontSize = SubstanceSizeUtils.getComponentFontSize(tree);
		int size = SubstanceSizeUtils.getTreeIconSize(fontSize);

		HashMapKey key = SubstanceCoreUtilities.getHashKey(size, isCollapsed);
		if (SubstanceIconFactory.treeIcons.get(key) == null) {
			Icon icon = new TreeIcon(size, isCollapsed);
			SubstanceIconFactory.treeIcons.put(key, icon);
		}
		return SubstanceIconFactory.treeIcons.get(key);
	}

	/**
	 * Trackable slider (for sliders that do not use {@link SubstanceSliderUI}
	 * as their UI (such as
	 * {@link contrib.ch.randelshofer.quaqua.colorchooser.ColorSliderUI}from
	 * Quaqua). Uses reflection to implement the {@link Trackable}interface,
	 * fetching the value of {@link BasicSliderUI#thumbRect}field.
	 * 
	 * @author Kirill Grouchnikov
	 */
	private static class TrackableSlider implements Trackable {
		/**
		 * The associated slider.
		 */
		private JSlider slider;

		/**
		 * Reflection reference to {@link BasicSliderUI#thumbRect}field. If
		 * reflection failed, or no such field (for example the custom UI
		 * implements {@link SliderUI}directly, <code>this</code> field is
		 * <code>null</code>.
		 */
		private Field thumbRectField;

		/**
		 * Simple constructor.
		 * 
		 * @param slider
		 *            The associated slider.
		 */
		public TrackableSlider(JSlider slider) {
			this.slider = slider;

			SliderUI sliderUI = slider.getUI();
			if (sliderUI instanceof BasicSliderUI) {
				try {
					this.thumbRectField = BasicSliderUI.class
							.getDeclaredField("thumbRect");
					this.thumbRectField.setAccessible(true);
				} catch (Exception exc) {
					this.thumbRectField = null;
				}
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.jvnet.substance.Trackable#isInside(java.awt.event.MouseEvent)
		 */
		public boolean isInside(MouseEvent me) {
			try {
				Rectangle thumbB = (Rectangle) this.thumbRectField
						.get(this.slider.getUI());
				if (thumbB == null)
					return false;
				return thumbB.contains(me.getX(), me.getY());
			} catch (Exception exc) {
				return false;
			}
		}
	}

	/**
	 * Icon for horizontal slider in {@link SubstanceSliderUI}.
	 * 
	 * @author Kirill Grouchnikov
	 */
	private static class SliderHorizontalIcon implements Icon, UIResource {
		/**
		 * Icon hash.
		 */
		private static LazyResettableHashMap<Icon> icons = new LazyResettableHashMap<Icon>(
				"SubstanceIconFactory.SliderHorizontalIcon");

		/**
		 * The size of <code>this</code> icon.
		 */
		private int size;

		/**
		 * Indication whether the icon is mirrorred.
		 */
		private boolean isMirrorred;

		/**
		 * Contains models for the sliders whose UI is <b>not </b>
		 * {@link SubstanceSliderUI}. A model is used for rollover effects (as
		 * in {@link SubstanceSliderUI}and {@link SubstanceScrollBarUI}). Note
		 * that this is weak map (on keys) to allow disposing of unused keys.
		 */
		private WeakHashMap<JSlider, ButtonModel> models = new WeakHashMap<JSlider, ButtonModel>();

		/**
		 * Simple constructor.
		 * 
		 * @param size
		 *            The size of <code>this</code> icon.
		 * @param isMirrorred
		 *            Indication whether the icon should be mirrored.
		 */
		public SliderHorizontalIcon(int size, boolean isMirrorred) {
			this.size = size;
			this.isMirrorred = isMirrorred;
		}

		/**
		 * Retrieves icon that matches the specified state of the slider thumb.
		 * 
		 * @param state
		 *            Slider state.
		 * @param prevState
		 *            The previous slider state.
		 * @param slider
		 *            The slider itself.
		 * @param sliderIcon
		 *            The slider icon.
		 * @return Icon that matches the specified state of the slider thumb.
		 */
		private static Icon getIcon(ComponentState state,
				ComponentState prevState, JSlider slider,
				SliderHorizontalIcon sliderIcon) {

			float cyclePos = state.getCyclePosition();

			SubstanceColorScheme colorScheme = SubstanceColorSchemeUtilities
					.getColorScheme(slider, state);
			SubstanceColorScheme colorScheme2 = SubstanceColorSchemeUtilities
					.getColorScheme(slider, prevState);
			SubstanceColorScheme borderScheme = SubstanceColorSchemeUtilities
					.getColorScheme(slider, ColorSchemeAssociationKind.BORDER,
							state);
			SubstanceColorScheme borderScheme2 = SubstanceColorSchemeUtilities
					.getColorScheme(slider, ColorSchemeAssociationKind.BORDER,
							prevState);

			FadeTracker fadeTracker = FadeTracker.getInstance();
			int width = 2 * sliderIcon.size / 3;
			FadeState rolloverFadeState = fadeTracker.getFadeState(slider,
					FadeKind.ROLLOVER);
			FadeState pressFadeState = fadeTracker.getFadeState(slider,
					FadeKind.PRESS);
			if (rolloverFadeState != null) {
				cyclePos = rolloverFadeState.getFadePosition();
				if (rolloverFadeState.isFadingIn())
					cyclePos = 1.0f - cyclePos;
				width = (int) (sliderIcon.size
						* (2.0 + rolloverFadeState.getFadePosition()) / 3.0);
			} else if (pressFadeState != null) {
				cyclePos = pressFadeState.getFadePosition();
				if (pressFadeState.isFadingIn())
					cyclePos = 1.0f - cyclePos;
				width = sliderIcon.size;
			} else {
				switch (state) {
				case ROLLOVER_UNSELECTED:
				case ROLLOVER_SELECTED:
				case ACTIVE:
				case PRESSED_SELECTED:
				case PRESSED_UNSELECTED:
					width = sliderIcon.size;
				}
			}
			width = Math.min(width, sliderIcon.size - 2);
			int delta = (sliderIcon.size - width) / 2;

			HashMapKey key = SubstanceCoreUtilities.getHashKey(sliderIcon.size,
					width, colorScheme.getDisplayName(), colorScheme2
							.getDisplayName(), borderScheme.getDisplayName(),
					borderScheme2.getDisplayName(), cyclePos);

			Icon result = SliderHorizontalIcon.icons.get(key);
			if (result != null)
				return result;

			SubstanceGradientPainter painter = SubstanceCoreUtilities
					.getGradientPainter(slider);
			SubstanceBorderPainter borderPainter = SubstanceCoreUtilities
					.getBorderPainter(slider);

			int borderDelta = (int) Math.floor(SubstanceSizeUtils
					.getBorderStrokeWidth(SubstanceSizeUtils
							.getComponentFontSize(slider)) / 2.0);
			Shape contour = SubstanceOutlineUtilities.getTriangleButtonOutline(
					width, sliderIcon.size - 1, 2, borderDelta);

			BufferedImage stateImage = SubstanceCoreUtilities.getBlankImage(
					sliderIcon.size - 1, sliderIcon.size - 1);
			Graphics2D g2d = stateImage.createGraphics();
			g2d.translate(delta, 0);

			painter.paintContourBackground(g2d, slider, width,
					sliderIcon.size - 1, contour, false, colorScheme,
					colorScheme2, cyclePos, true, colorScheme != colorScheme2);
			// BufferedImage biResult = SubstanceCoreUtilities.getBlankImage(
			// sliderIcon.size - 1, sliderIcon.size - 1);
			// Graphics2D bg2d = (Graphics2D) biResult.getGraphics();
			// bg2d.translate(delta, 0);
			// bg2d.drawImage(stateImage, 0, 0, null);

			int borderThickness = (int) SubstanceSizeUtils
					.getBorderStrokeWidth(SubstanceSizeUtils
							.getComponentFontSize(slider));
			GeneralPath contourInner = SubstanceOutlineUtilities
					.getTriangleButtonOutline(width, sliderIcon.size - 1, 2,
							borderThickness + borderDelta);

			borderPainter.paintBorder(g2d, slider, width, sliderIcon.size - 1,
					contour, contourInner, borderScheme, borderScheme2,
					cyclePos, borderScheme != borderScheme2);
			g2d.translate(-delta, 0);

			if (sliderIcon.isMirrorred)
				stateImage = SubstanceImageCreator.getRotated(stateImage, 2);
			// Graphics g = stateImage.getGraphics();
			// g.setColor(Color.red);
			// g.drawRect(0, 0, stateImage.getWidth()-1,
			// stateImage.getHeight()-1);

			result = new ImageIcon(stateImage);
			SliderHorizontalIcon.icons.put(key, result);

			return result;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.Icon#paintIcon(java.awt.Component,
		 * java.awt.Graphics, int, int)
		 */
		public void paintIcon(Component c, Graphics g, int x, int y) {
			if (!(g instanceof Graphics2D)) {
				return;
			}

			JSlider slider = (JSlider) c;
			SliderUI sliderUI = slider.getUI();
			ComponentState state = ComponentState.ACTIVE;
			if (sliderUI instanceof SubstanceSliderUI) {
				state = ComponentState.getState(((SubstanceSliderUI) sliderUI)
						.getButtonModel(), null);
			} else {
				ButtonModel buttonModel = this.models.get(slider);
				if (buttonModel == null) {
					buttonModel = new DefaultButtonModel();
					buttonModel.setEnabled(slider.isEnabled());
					RolloverControlListener listener = new RolloverControlListener(
							new TrackableSlider(slider), buttonModel);
					slider.addMouseListener(listener);
					slider.addMouseMotionListener(listener);
					this.models.put(slider, buttonModel);
				}
				state = ComponentState.getState(buttonModel, null);
			}
			ComponentState prevState = SubstanceCoreUtilities
					.getPrevComponentState(slider);

			Icon iconToDraw = SliderHorizontalIcon.getIcon(state, prevState,
					slider, this);

			iconToDraw.paintIcon(c, g, x, y);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.Icon#getIconWidth()
		 */
		public int getIconWidth() {
			return this.size - 1;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.Icon#getIconHeight()
		 */
		public int getIconHeight() {
			return this.size - 1;
		}
	}

	/**
	 * Round icon for sliders in {@link SubstanceSliderUI}.
	 * 
	 * @author Kirill Grouchnikov
	 */
	private static class SliderRoundIcon implements Icon, UIResource {
		/**
		 * Icon hash.
		 */
		private static LazyResettableHashMap<Icon> icons = new LazyResettableHashMap<Icon>(
				"SubstanceIconFactory.SliderRoundIcon");

		/**
		 * The size of <code>this</code> icon.
		 */
		private int size;

		/**
		 * Contains models for the sliders whose UI is <b>not </b>
		 * {@link SubstanceSliderUI}. A model is used for rollover effects (as
		 * in {@link SubstanceSliderUI}and {@link SubstanceScrollBarUI}). Note
		 * that this is weak map (on keys) to allow disposing of unused keys.
		 */
		private WeakHashMap<JSlider, ButtonModel> models = new WeakHashMap<JSlider, ButtonModel>();

		/**
		 * Simple constructor.
		 * 
		 * @param size
		 *            The size of <code>this</code> icon.
		 */
		public SliderRoundIcon(int size) {
			this.size = size;
		}

		/**
		 * Retrieves icon that matches the specified state of the slider thumb.
		 * 
		 * @param state
		 *            Slider state.
		 * @param prevState
		 *            The previous slider state.
		 * @param slider
		 *            The slider itself.
		 * @param sliderIcon
		 *            The slider icon.
		 * @return Icon that matches the specified state of the slider thumb.
		 */
		private static Icon getIcon(ComponentState state,
				ComponentState prevState, JSlider slider,
				SliderRoundIcon sliderIcon) {

			// System.out.println("Curr : " + state.name() + ", prev : " +
			// prevState.name());

			float cyclePos = state.getCyclePosition();

			SubstanceColorScheme colorScheme = SubstanceColorSchemeUtilities
					.getColorScheme(slider, state);
			SubstanceColorScheme colorScheme2 = SubstanceColorSchemeUtilities
					.getColorScheme(slider, prevState);
			SubstanceColorScheme borderScheme = SubstanceColorSchemeUtilities
					.getColorScheme(slider, ColorSchemeAssociationKind.BORDER,
							state);
			SubstanceColorScheme borderScheme2 = SubstanceColorSchemeUtilities
					.getColorScheme(slider, ColorSchemeAssociationKind.BORDER,
							prevState);

			FadeTracker fadeTracker = FadeTracker.getInstance();
			int width = 2 * sliderIcon.size / 3;
			FadeState rolloverFadeState = fadeTracker.getFadeState(slider,
					FadeKind.ROLLOVER);
			FadeState pressFadeState = fadeTracker.getFadeState(slider,
					FadeKind.PRESS);
			if (rolloverFadeState != null) {
				cyclePos = rolloverFadeState.getFadePosition();
				if (rolloverFadeState.isFadingIn())
					cyclePos = 1.0f - cyclePos;
				width = (int) (sliderIcon.size
						* (2.0 + rolloverFadeState.getFadePosition()) / 3.0);
			} else if (pressFadeState != null) {
				cyclePos = pressFadeState.getFadePosition();
				if (pressFadeState.isFadingIn())
					cyclePos = 1.0f - cyclePos;
				width = sliderIcon.size;
			} else {
				switch (state) {
				case ROLLOVER_UNSELECTED:
				case ROLLOVER_SELECTED:
				case ACTIVE:
				case PRESSED_SELECTED:
				case PRESSED_UNSELECTED:
					width = sliderIcon.size;
				}
			}
			width = Math.min(width, sliderIcon.size - 2);
			if (width % 2 == 0)
				width--;
			int delta = (sliderIcon.size - width) / 2;

			HashMapKey key = SubstanceCoreUtilities.getHashKey(sliderIcon.size,
					width, colorScheme.getDisplayName(), colorScheme2
							.getDisplayName(), borderScheme.getDisplayName(),
					borderScheme2.getDisplayName(), cyclePos);

			Icon result = SliderRoundIcon.icons.get(key);
			if (result != null)
				return result;

			SubstanceGradientPainter painter = SubstanceCoreUtilities
					.getGradientPainter(slider);
			SubstanceBorderPainter borderPainter = SubstanceCoreUtilities
					.getBorderPainter(slider);

			int borderDelta = (int) Math.floor(SubstanceSizeUtils
					.getBorderStrokeWidth(SubstanceSizeUtils
							.getComponentFontSize(slider)) / 2.0);
			Shape contour = new Ellipse2D.Float(borderDelta, borderDelta, width
					- 2 * borderDelta - 1, width - 2 * borderDelta - 1);

			BufferedImage stateImage = SubstanceCoreUtilities.getBlankImage(
					sliderIcon.size - 1, sliderIcon.size - 1);
			Graphics2D g2d = stateImage.createGraphics();
			g2d.translate(delta, delta);

			painter.paintContourBackground(g2d, slider, width,
					sliderIcon.size - 1, contour, false, colorScheme,
					colorScheme2, cyclePos, true, colorScheme != colorScheme2);
			// BufferedImage biResult = SubstanceCoreUtilities.getBlankImage(
			// sliderIcon.size - 1, sliderIcon.size - 1);
			// Graphics2D bg2d = (Graphics2D) biResult.getGraphics();
			// // bg2d.setColor(Color.red);
			// // bg2d.drawRect(0, 0, sliderIcon.size-2, sliderIcon.size-2);
			// bg2d.drawImage(stateImage, 0, 0, null);

			int borderThickness = (int) SubstanceSizeUtils
					.getBorderStrokeWidth(SubstanceSizeUtils
							.getComponentFontSize(slider));
			Shape contourInner = new Ellipse2D.Float(borderDelta
					+ borderThickness, borderDelta + borderThickness, width - 2
					* borderDelta - 2 * borderThickness - 1, width - 2
					* borderDelta - 2 * borderThickness - 1);

			borderPainter.paintBorder(g2d, slider, width, sliderIcon.size - 1,
					contour, contourInner, borderScheme, borderScheme2,
					cyclePos, borderScheme != borderScheme2);
			// bg2d.translate(-delta, -delta);

			result = new ImageIcon(stateImage);
			SliderRoundIcon.icons.put(key, result);

			return result;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.Icon#paintIcon(java.awt.Component,
		 * java.awt.Graphics, int, int)
		 */
		public void paintIcon(Component c, Graphics g, int x, int y) {
			if (!(g instanceof Graphics2D)) {
				return;
			}

			JSlider slider = (JSlider) c;
			SliderUI sliderUI = slider.getUI();
			ComponentState state = ComponentState.ACTIVE;
			if (sliderUI instanceof SubstanceSliderUI) {
				state = ComponentState.getState(((SubstanceSliderUI) sliderUI)
						.getButtonModel(), null);
			} else {
				ButtonModel buttonModel = this.models.get(slider);
				if (buttonModel == null) {
					buttonModel = new DefaultButtonModel();
					buttonModel.setEnabled(slider.isEnabled());
					RolloverControlListener listener = new RolloverControlListener(
							new TrackableSlider(slider), buttonModel);
					slider.addMouseListener(listener);
					slider.addMouseMotionListener(listener);
					this.models.put(slider, buttonModel);
				}
				state = ComponentState.getState(buttonModel, null);
			}
			ComponentState prevState = SubstanceCoreUtilities
					.getPrevComponentState(slider);
			Icon iconToDraw = SliderRoundIcon.getIcon(state, prevState, slider,
					this);

			iconToDraw.paintIcon(c, g, x, y);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.Icon#getIconWidth()
		 */
		public int getIconWidth() {
			return this.size - 1;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.Icon#getIconHeight()
		 */
		public int getIconHeight() {
			return this.size - 1;
		}
	}

	/**
	 * Icon for vertical slider in {@link SubstanceSliderUI}.
	 * 
	 * @author Kirill Grouchnikov
	 */
	private static class SliderVerticalIcon implements Icon, UIResource {
		/**
		 * Icon hash.
		 */
		private static LazyResettableHashMap<Icon> icons = new LazyResettableHashMap<Icon>(
				"SubstanceIconFactory.SliderVerticalIcon");

		/**
		 * The size of <code>this</code> icon.
		 */
		private int size;

		/**
		 * Indication whether the icon is mirrorred.
		 */
		private boolean isMirrorred;

		/**
		 * Contains models for the sliders whose UI is <b>not </b>
		 * {@link SubstanceSliderUI}. A model is used for rollover effects (as
		 * in {@link SubstanceSliderUI}and {@link SubstanceScrollBarUI}). Note
		 * that this is weak map (on keys) to allow disposing of unused keys.
		 */
		private WeakHashMap<JSlider, ButtonModel> models = new WeakHashMap<JSlider, ButtonModel>();

		/**
		 * Simple constructor.
		 * 
		 * @param size
		 *            The size of <code>this</code> icon.
		 * @param isMirrorred
		 *            Indication whether the icon should be mirrored.
		 */
		public SliderVerticalIcon(int size, boolean isMirrorred) {
			this.size = size;
			this.isMirrorred = isMirrorred;
		}

		/**
		 * Retrieves icon that matches the specified state of the slider thumb.
		 * 
		 * @param state
		 *            Slider state.
		 * @param prevState
		 *            The previous slider state.
		 * @param slider
		 *            The slider itself.
		 * @param sliderIcon
		 *            The slider icon.
		 * @return Icon that matches the specified state of the slider thumb.
		 */
		private static Icon getIcon(ComponentState state,
				ComponentState prevState, JSlider slider,
				SliderVerticalIcon sliderIcon) {

			float cyclePos = state.getCyclePosition();

			SubstanceColorScheme colorScheme = SubstanceColorSchemeUtilities
					.getColorScheme(slider, state);
			SubstanceColorScheme colorScheme2 = SubstanceColorSchemeUtilities
					.getColorScheme(slider, prevState);
			SubstanceColorScheme borderScheme = SubstanceColorSchemeUtilities
					.getColorScheme(slider, ColorSchemeAssociationKind.BORDER,
							state);
			SubstanceColorScheme borderScheme2 = SubstanceColorSchemeUtilities
					.getColorScheme(slider, ColorSchemeAssociationKind.BORDER,
							prevState);

			FadeTracker fadeTracker = FadeTracker.getInstance();
			int height = 2 * sliderIcon.size / 3;

			FadeState rolloverFadeState = fadeTracker.getFadeState(slider,
					FadeKind.ROLLOVER);
			FadeState pressFadeState = fadeTracker.getFadeState(slider,
					FadeKind.PRESS);
			if (rolloverFadeState != null) {
				cyclePos = rolloverFadeState.getFadePosition();
				if (rolloverFadeState.isFadingIn())
					cyclePos = 1.0f - cyclePos;
				height = (int) (sliderIcon.size
						* (2.0 + rolloverFadeState.getFadePosition()) / 3.0);
			} else if (pressFadeState != null) {
				cyclePos = pressFadeState.getFadePosition();
				if (pressFadeState.isFadingIn())
					cyclePos = 1.0f - cyclePos;
				height = sliderIcon.size;
			} else {
				switch (state) {
				case ROLLOVER_UNSELECTED:
				case ROLLOVER_SELECTED:
				case ACTIVE:
				case PRESSED_SELECTED:
				case PRESSED_UNSELECTED:
					height = sliderIcon.size;
				}
			}
			height = Math.min(height, sliderIcon.size - 2);
			int delta = (sliderIcon.size - height) / 2 - 1;

			HashMapKey key = SubstanceCoreUtilities.getHashKey(sliderIcon.size,
					height, slider.getComponentOrientation(), colorScheme
							.getDisplayName(), colorScheme2.getDisplayName(),
					borderScheme.getDisplayName(), borderScheme2
							.getDisplayName(), cyclePos);

			Icon result = SliderVerticalIcon.icons.get(key);
			if (result != null)
				return result;

			SubstanceGradientPainter painter = SubstanceCoreUtilities
					.getGradientPainter(slider);
			SubstanceBorderPainter borderPainter = SubstanceCoreUtilities
					.getBorderPainter(slider);

			int borderDelta = (int) Math.floor(SubstanceSizeUtils
					.getBorderStrokeWidth(SubstanceSizeUtils
							.getComponentFontSize(slider)) / 2.0);
			Shape contour = SubstanceOutlineUtilities.getTriangleButtonOutline(
					height, sliderIcon.size, 2, borderDelta);

			BufferedImage stateImage = SubstanceCoreUtilities.getBlankImage(
					sliderIcon.size - 1, sliderIcon.size - 1);
			Graphics2D g2d = stateImage.createGraphics();
			g2d.translate(delta, 0);

			painter.paintContourBackground(g2d, slider, height,
					sliderIcon.size, contour, false, colorScheme, colorScheme2,
					cyclePos, true, colorScheme != colorScheme2);
			// BufferedImage biResult = SubstanceCoreUtilities.getBlankImage(
			// sliderIcon.size - 1, sliderIcon.size - 1);
			// Graphics2D bg2d = (Graphics2D) biResult.getGraphics();
			// bg2d.translate(delta, 0);
			// bg2d.drawImage(stateImage, 0, 0, null);

			int borderThickness = (int) SubstanceSizeUtils
					.getBorderStrokeWidth(SubstanceSizeUtils
							.getComponentFontSize(slider));
			GeneralPath contourInner = SubstanceOutlineUtilities
					.getTriangleButtonOutline(height, sliderIcon.size, 2,
							borderThickness + borderDelta);

			borderPainter.paintBorder(g2d, slider, height, sliderIcon.size - 1,
					contour, contourInner, borderScheme, borderScheme2,
					cyclePos, borderScheme != borderScheme2);
			// bg2d.translate(-delta, 0);

			if (sliderIcon.isMirrorred)
				stateImage = SubstanceImageCreator.getRotated(stateImage, 1);
			else
				stateImage = SubstanceImageCreator.getRotated(stateImage, 3);

			if (!slider.getComponentOrientation().isLeftToRight()) {
				stateImage = SubstanceImageCreator.getRotated(stateImage, 2);
			}

			// Graphics g = stateImage.getGraphics();
			// g.setColor(Color.red);
			// g.drawRect(0, 0, stateImage.getWidth()-1,
			// stateImage.getHeight()-1);

			result = new ImageIcon(stateImage);
			SliderVerticalIcon.icons.put(key, result);

			return result;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.Icon#paintIcon(java.awt.Component,
		 * java.awt.Graphics, int, int)
		 */
		public void paintIcon(Component c, Graphics g, int x, int y) {
			if (!(g instanceof Graphics2D)) {
				return;
			}
			JSlider slider = (JSlider) c;
			SliderUI sliderUI = slider.getUI();
			ComponentState state = ComponentState.ACTIVE;
			if (sliderUI instanceof SubstanceSliderUI) {
				state = ComponentState.getState(((SubstanceSliderUI) sliderUI)
						.getButtonModel(), null);
			} else {
				ButtonModel buttonModel = this.models.get(slider);
				if (buttonModel == null) {
					buttonModel = new DefaultButtonModel();
					buttonModel.setEnabled(slider.isEnabled());
					RolloverControlListener listener = new RolloverControlListener(
							new TrackableSlider(slider), buttonModel);
					slider.addMouseListener(listener);
					slider.addMouseMotionListener(listener);
					this.models.put(slider, buttonModel);
				}
				state = ComponentState.getState(buttonModel, null);
			}
			ComponentState prevState = SubstanceCoreUtilities
					.getPrevComponentState(slider);

			Icon iconToDraw = SliderVerticalIcon.getIcon(state, prevState,
					slider, this);
			iconToDraw.paintIcon(c, g, x, y);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.Icon#getIconWidth()
		 */
		public int getIconWidth() {
			return this.size - 1;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.Icon#getIconHeight()
		 */
		public int getIconHeight() {
			return this.size - 1;
		}
	}

	/**
	 * Collapse / expand icons for JTrees in {@link SubstanceTreeUI}.
	 * 
	 * @author Kirill Grouchnikov
	 */
	private static class TreeIcon implements Icon, UIResource {
		/**
		 * Icon hash.
		 */
		private static LazyResettableHashMap<Icon> icons = new LazyResettableHashMap<Icon>(
				"SubstanceIconFactory.TreeIcon");

		/**
		 * The collapsed indication of this icon
		 */
		private boolean isCollapsed;

		private int size;

		/**
		 * Simple constructor.
		 * 
		 * @param isCollapsed
		 *            The collapsed indication of <code>this</code> icon.
		 */
		public TreeIcon(int size, boolean isCollapsed) {
			this.isCollapsed = isCollapsed;
			this.size = size;
		}

		/**
		 * Retrieves icon that matches the specified state of the slider thumb.
		 * 
		 * @param state
		 *            Slider state.
		 * @param prevState
		 *            The previous slider state.
		 * @param slider
		 *            The slider itself.
		 * @param sliderIcon
		 *            The slider icon.
		 * @return Icon that matches the specified state of the slider thumb.
		 */
		private static Icon getIcon(JTree tree, boolean isCollapsed) {
			ComponentState state = ((tree == null) || tree.isEnabled()) ? ComponentState.DEFAULT
					: ComponentState.DISABLED_UNSELECTED;
			SubstanceColorScheme fillScheme = SubstanceColorSchemeUtilities
					.getColorScheme(tree, state);
			SubstanceColorScheme borderScheme = SubstanceColorSchemeUtilities
					.getColorScheme(tree, ColorSchemeAssociationKind.BORDER,
							state);

			int fontSize = SubstanceSizeUtils.getComponentFontSize(tree);

			HashMapKey key = SubstanceCoreUtilities.getHashKey(fontSize,
					fillScheme.getDisplayName(), borderScheme.getDisplayName(),
					isCollapsed);

			Icon result = TreeIcon.icons.get(key);
			if (result != null)
				return result;

			result = new ImageIcon(SubstanceImageCreator.getTreeIcon(tree,
					fillScheme, borderScheme, isCollapsed));
			TreeIcon.icons.put(key, result);

			return result;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.Icon#paintIcon(java.awt.Component,
		 * java.awt.Graphics, int, int)
		 */
		public void paintIcon(Component c, Graphics g, int x, int y) {
			if (!(g instanceof Graphics2D)) {
				return;
			}

			// The following check is here because some applications
			// (like JIDE's ExpandablePanel) may decide to use the
			// "Tree.collapsedIcon" and "Tree.expandedIcon" UIManager
			// entries to paint on non-JTree components. Sigh.
			JTree tree = (c instanceof JTree) ? (JTree) c : null;
			Icon iconToDraw = TreeIcon.getIcon(tree, this.isCollapsed);

			iconToDraw.paintIcon(c, g, x, y);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.Icon#getIconWidth()
		 */
		public int getIconWidth() {
			return this.size;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.Icon#getIconHeight()
		 */
		public int getIconHeight() {
			return this.size;
		}
	}

	/**
	 * Icon kind of a title pane button.
	 * 
	 * @author Kirill Grocuhnikov.
	 */
	public enum IconKind {
		/**
		 * Icon of a close button.
		 */
		CLOSE,

		/**
		 * Icon of a minimize button.
		 */
		MINIMIZE,

		/**
		 * Icon of a maximize button.
		 */
		MAXIMIZE,

		/**
		 * Icon of a restore button.
		 */
		RESTORE;
	}

	/**
	 * Cache of title pane icons.
	 */
	private static final Map<IconKind, LazyResettableHashMap<Icon>> titlePaneIcons = SubstanceIconFactory
			.createTitlePaneIcons();

	/**
	 * Creates an empty map of title pane icons.
	 * 
	 * @return Empty map of title pane icons.
	 */
	private static Map<IconKind, LazyResettableHashMap<Icon>> createTitlePaneIcons() {
		Map<IconKind, LazyResettableHashMap<Icon>> result = new HashMap<IconKind, LazyResettableHashMap<Icon>>();

		result.put(IconKind.CLOSE, new LazyResettableHashMap<Icon>(
				"Close title pane icons"));
		result.put(IconKind.MINIMIZE, new LazyResettableHashMap<Icon>(
				"Minimize title pane icons"));
		result.put(IconKind.MAXIMIZE, new LazyResettableHashMap<Icon>(
				"Maximize title pane icons"));
		result.put(IconKind.RESTORE, new LazyResettableHashMap<Icon>(
				"Restore title pane icons"));
		return result;
	}

	/**
	 * Returns title pane icon of the specified kind.
	 * 
	 * @param iconKind
	 *            Icon kind.
	 * @param scheme
	 *            Color scheme.
	 * @return Title pane icon of the specified kind.
	 */
	public static Icon getTitlePaneIcon(IconKind iconKind,
			SubstanceColorScheme scheme, SubstanceColorScheme backgroundScheme) {

		LazyResettableHashMap<Icon> kindMap = SubstanceIconFactory.titlePaneIcons
				.get(iconKind);

		HashMapKey key = SubstanceCoreUtilities.getHashKey(scheme
				.getDisplayName(), backgroundScheme.getDisplayName());
		Icon result = kindMap.get(key);
		if (result != null)
			return result;

		switch (iconKind) {
		case CLOSE:
			result = SubstanceImageCreator.getCloseIcon(scheme,
					backgroundScheme);
			break;
		case MINIMIZE:
			result = SubstanceImageCreator.getMinimizeIcon(scheme,
					backgroundScheme);
			break;
		case MAXIMIZE:
			result = SubstanceImageCreator.getMaximizeIcon(scheme,
					backgroundScheme);
			break;
		case RESTORE:
			result = SubstanceImageCreator.getRestoreIcon(scheme,
					backgroundScheme);
			break;
		}
		kindMap.put(key, result);
		return result;
	}

}