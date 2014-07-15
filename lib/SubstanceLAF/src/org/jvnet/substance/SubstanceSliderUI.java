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
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.BasicSliderUI;

import org.jvnet.lafwidget.animation.*;
import org.jvnet.lafwidget.layout.TransitionLayout;
import org.jvnet.substance.api.*;
import org.jvnet.substance.painter.border.SubstanceBorderPainter;
import org.jvnet.substance.painter.gradient.ClassicGradientPainter;
import org.jvnet.substance.painter.gradient.SubstanceGradientPainter;
import org.jvnet.substance.painter.utils.SeparatorPainterUtils;
import org.jvnet.substance.painter.utils.BackgroundPaintingUtils;
import org.jvnet.substance.utils.*;
import org.jvnet.substance.utils.icon.SubstanceIconFactory;

/**
 * UI for sliders in <b>Substance</b> look and feel.
 * 
 * @author Kirill Grouchnikov
 */
public class SubstanceSliderUI extends BasicSliderUI implements Trackable {
	/**
	 * Surrogate button model for tracking the thumb transitions.
	 */
	private ButtonModel thumbModel;

	/**
	 * Listener for fade animations.
	 */
	private RolloverControlListener substanceRolloverListener;

	/**
	 * Listener on property change events.
	 */
	private PropertyChangeListener substancePropertyChangeListener;

	/**
	 * Listener for fade animations.
	 */
	protected FadeStateListener substanceFadeStateListener;

	/**
	 * Icon for horizontal sliders.
	 */
	protected Icon horizontalIcon;

	/**
	 * Icon for sliders without labels and ticks.
	 */
	protected Icon roundIcon;

	/**
	 * Icon for vertical sliders.
	 */
	protected Icon verticalIcon;

	/**
	 * Cache of track images.
	 */
	protected static final LazyResettableHashMap<BufferedImage> trackCache = new LazyResettableHashMap<BufferedImage>(
			"SubstanceSliderUI.track");

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.ComponentUI#createUI(javax.swing.JComponent)
	 */
	public static ComponentUI createUI(JComponent comp) {
		SubstanceCoreUtilities.testComponentCreationThreadingViolation(comp);
		return new SubstanceSliderUI((JSlider) comp);
	}

	/**
	 * Simple constructor.
	 * 
	 * @param slider
	 *            Slider.
	 */
	public SubstanceSliderUI(JSlider slider) {
		super(null);
		this.thumbModel = new DefaultButtonModel();
		this.thumbModel.setArmed(false);
		this.thumbModel.setSelected(false);
		this.thumbModel.setPressed(false);
		this.thumbModel.setRollover(false);
		this.thumbModel.setEnabled(slider.isEnabled());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.basic.BasicSliderUI#calculateTrackRect()
	 */
	@Override
	protected void calculateTrackRect() {
		super.calculateTrackRect();
		if (this.slider.getOrientation() == SwingConstants.HORIZONTAL) {
			this.trackRect.y = 3
					+ (int) Math.ceil(SubstanceSizeUtils
							.getFocusStrokeWidth(SubstanceSizeUtils
									.getComponentFontSize(this.slider)))
					+ this.insetCache.top;
		}
	}

	/**
	 * Returns the rectangle of track for painting.
	 * 
	 * @return The rectangle of track for painting.
	 */
	private Rectangle getPaintTrackRect() {
		int trackLeft = 0, trackRight = 0, trackTop = 0, trackBottom = 0;
		if (this.slider.getOrientation() == SwingConstants.HORIZONTAL) {
			trackTop = 3 + this.insetCache.top + 2 * this.focusInsets.top;
			trackBottom = trackTop + this.getTrackWidth() - 1;
			trackRight = this.trackRect.width;
			return new Rectangle(this.trackRect.x + trackLeft, trackTop,
					trackRight - trackLeft, trackBottom - trackTop);
		} else {
			if (this.slider.getComponentOrientation().isLeftToRight()) {
				trackLeft = trackRect.x + this.insetCache.left
						+ this.focusInsets.left;
				trackRight = trackLeft + this.getTrackWidth() - 1;
			} else {
				trackRight = trackRect.x + trackRect.width
						- this.insetCache.right - this.focusInsets.right;
				// this.slider.getWidth() - 8 - this.insetCache.right;
				trackLeft = trackRight - this.getTrackWidth() + 1;
			}
			trackBottom = this.trackRect.height - 1;
			return new Rectangle(trackLeft, this.trackRect.y + trackTop,
					trackRight - trackLeft, trackBottom - trackTop);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.basic.BasicSliderUI#paintTrack(java.awt.Graphics)
	 */
	@Override
	public void paintTrack(Graphics g) {
		Graphics2D graphics = (Graphics2D) g.create();

		boolean drawInverted = this.drawInverted();

		Rectangle paintRect = this.getPaintTrackRect();

		// Width and height of the painting rectangle.
		int width = paintRect.width;
		int height = paintRect.height;

		if (this.slider.getOrientation() == JSlider.VERTICAL) {
			// apply rotation / translate transformation on vertical
			// sider tracks
			int temp = width;
			width = height;
			height = temp;
			AffineTransform at = graphics.getTransform();
			at.translate(paintRect.x, width + paintRect.y);
			at.rotate(-Math.PI / 2);
			graphics.setTransform(at);
		} else {
			graphics.translate(paintRect.x, paintRect.y);
		}

		ComponentState currState = ComponentState.getState(this.thumbModel,
				this.slider);
		ComponentState prevState = SubstanceCoreUtilities
				.getPrevComponentState(this.slider);

		SubstanceColorScheme trackScheme = SubstanceColorSchemeUtilities
				.getColorScheme(this.slider,
						this.slider.isEnabled() ? ComponentState.DEFAULT
								: ComponentState.DISABLED_UNSELECTED);

		this.paintSliderTrack(graphics, drawInverted, trackScheme,
				SubstanceColorSchemeUtilities.getColorScheme(this.slider,
						ColorSchemeAssociationKind.BORDER, currState), width,
				height);

		SubstanceColorScheme trackSchemeSelected = SubstanceColorSchemeUtilities
				.getColorScheme(this.slider, currState);
		SubstanceColorScheme trackBorderSchemeSelected = SubstanceColorSchemeUtilities
				.getColorScheme(this.slider, ColorSchemeAssociationKind.BORDER,
						currState);
		FadeState fadeState = SubstanceFadeUtilities.getFadeState(this.slider,
				FadeKind.ROLLOVER, FadeKind.SELECTION, FadeKind.PRESS);
		if (fadeState != null) {
			SubstanceColorScheme prevScheme = SubstanceColorSchemeUtilities
					.getColorScheme(this.slider, prevState);
			SubstanceColorScheme prevBorderScheme = SubstanceColorSchemeUtilities
					.getColorScheme(this.slider,
							ColorSchemeAssociationKind.BORDER, prevState);
			float cyclePos = fadeState.getFadePosition();
			if (!fadeState.isFadingIn())
				cyclePos = 1.0f - cyclePos;

			if (prevState != ComponentState.DEFAULT) {
				graphics.setComposite(TransitionLayout.getAlphaComposite(
						this.slider, 1.0f - cyclePos, g));
				this.paintSliderTrackSelected(graphics, drawInverted,
						paintRect, prevScheme, prevBorderScheme, width, height);
			}
			if (currState != ComponentState.DEFAULT) {
				graphics.setComposite(TransitionLayout.getAlphaComposite(
						this.slider, cyclePos, g));
				this.paintSliderTrackSelected(graphics, drawInverted,
						paintRect, trackSchemeSelected,
						trackBorderSchemeSelected, width, height);
			}
		} else {
			boolean hasFill = currState.isKindActive(FadeKind.ROLLOVER)
					|| currState.isKindActive(FadeKind.PRESS);
			if (hasFill) {
				this.paintSliderTrackSelected(graphics, drawInverted,
						paintRect, trackSchemeSelected,
						trackBorderSchemeSelected, width, height);
			}
		}

		graphics.dispose();
	}

	/**
	 * Paints the slider track.
	 * 
	 * @param graphics
	 *            Graphics.
	 * @param drawInverted
	 *            Indicates whether the value-range shown for the slider is
	 *            reversed.
	 * @param fillColorScheme
	 *            Fill color scheme.
	 * @param borderScheme
	 *            Border color scheme.
	 * @param width
	 *            Track width.
	 * @param height
	 *            Track height.
	 */
	private void paintSliderTrack(Graphics2D graphics, boolean drawInverted,
			SubstanceColorScheme fillColorScheme,
			SubstanceColorScheme borderScheme, int width, int height) {
		Graphics2D g2d = (Graphics2D) graphics.create();

		SubstanceGradientPainter gradientPainter = ClassicGradientPainter.INSTANCE;
		SubstanceBorderPainter borderPainter = SubstanceCoreUtilities
				.getBorderPainter(this.slider);

		int componentFontSize = SubstanceSizeUtils
				.getComponentFontSize(this.slider);
		int borderDelta = (int) Math.floor(SubstanceSizeUtils
				.getBorderStrokeWidth(componentFontSize) / 2.0);
		float radius = SubstanceSizeUtils
				.getClassicButtonCornerRadius(componentFontSize) / 2.0f;
		int borderThickness = (int) SubstanceSizeUtils
				.getBorderStrokeWidth(componentFontSize);

		HashMapKey key = SubstanceCoreUtilities.getHashKey(width, height,
				radius, borderDelta, borderThickness, fillColorScheme
						.getDisplayName(), borderScheme.getDisplayName());

		if (!trackCache.containsKey(key)) {
			BufferedImage trackImage = SubstanceCoreUtilities.getBlankImage(
					width + 1, height + 1);
			Graphics2D cacheGraphics = trackImage.createGraphics();

			Shape contour = SubstanceOutlineUtilities.getBaseOutline(width + 1,
					height + 1, radius, null, borderDelta);

			gradientPainter.paintContourBackground(cacheGraphics, slider,
					width, height, contour, false, fillColorScheme,
					fillColorScheme, 0, false, false);

			GeneralPath contourInner = SubstanceOutlineUtilities
					.getBaseOutline(width + 1, height + 1, radius
							- borderThickness, null, borderThickness
							+ borderDelta);
			borderPainter.paintBorder(cacheGraphics, slider, width + 1,
					height + 1, contour, contourInner, borderScheme,
					borderScheme, 0, false);

			trackCache.put(key, trackImage);
			cacheGraphics.dispose();
		}

		BufferedImage cached = trackCache.get(key);
		g2d.drawImage(cached, 0, 0, null);

		g2d.dispose();
	}

	/**
	 * Paints the selected part of the slider track.
	 * 
	 * @param graphics
	 *            Graphics.
	 * @param drawInverted
	 *            Indicates whether the value-range shown for the slider is
	 *            reversed.
	 * @param paintRect
	 *            Selected portion.
	 * @param fillScheme
	 *            Fill color scheme.
	 * @param borderScheme
	 *            Border color scheme.
	 * @param width
	 *            Track width.
	 * @param height
	 *            Track height.
	 */
	private void paintSliderTrackSelected(Graphics2D graphics,
			boolean drawInverted, Rectangle paintRect,
			SubstanceColorScheme fillScheme, SubstanceColorScheme borderScheme,
			int width, int height) {

		Graphics2D g2d = (Graphics2D) graphics.create();
		Insets insets = this.slider.getInsets();
		insets.top /= 2;
		insets.left /= 2;
		insets.bottom /= 2;
		insets.right /= 2;

		SubstanceGradientPainter gp = SubstanceCoreUtilities
				.getGradientPainter(this.slider);
		SubstanceBorderPainter borderPainter = SubstanceCoreUtilities
				.getBorderPainter(this.slider);
		float radius = SubstanceSizeUtils
				.getClassicButtonCornerRadius(SubstanceSizeUtils
						.getComponentFontSize(slider)) / 2.0f;
		int borderDelta = (int) Math.floor(SubstanceSizeUtils
				.getBorderStrokeWidth(SubstanceSizeUtils
						.getComponentFontSize(slider)) / 2.0);

		// fill selected portion
		if (this.slider.isEnabled()) {
			if (this.slider.getOrientation() == SwingConstants.HORIZONTAL) {
				int middleOfThumb = this.thumbRect.x
						+ (this.thumbRect.width / 2) - paintRect.x;
				int fillMinX;
				int fillMaxX;

				if (drawInverted) {
					fillMinX = middleOfThumb;
					fillMaxX = width;
				} else {
					fillMinX = 0;
					fillMaxX = middleOfThumb;
				}

				int fillWidth = fillMaxX - fillMinX;
				int fillHeight = height + 1;
				if ((fillWidth > 0) && (fillHeight > 0)) {
					Shape contour = SubstanceOutlineUtilities.getBaseOutline(
							fillWidth, fillHeight, radius, null, borderDelta);
					g2d.translate(fillMinX, 0);
					gp.paintContourBackground(g2d, this.slider, fillWidth,
							fillHeight, contour, false, fillScheme, fillScheme,
							0.0f, false, false);
					borderPainter.paintBorder(g2d, this.slider, fillWidth,
							fillHeight, contour, null, borderScheme,
							borderScheme, 0.0f, false);
				}
			} else {
				int middleOfThumb = this.thumbRect.y
						+ (this.thumbRect.height / 2) - paintRect.y;
				int fillMin;
				int fillMax;

				if (this.drawInverted()) {
					fillMin = 0;
					fillMax = middleOfThumb;
					// fix for issue 368 - inverted vertical sliders
					g2d.translate(width + 2 - middleOfThumb, 0);
				} else {
					fillMin = middleOfThumb;
					fillMax = width + 1;
				}

				int fillWidth = fillMax - fillMin;
				int fillHeight = height + 1;
				if ((fillWidth > 0) && (fillHeight > 0)) {
					Shape contour = SubstanceOutlineUtilities.getBaseOutline(
							fillWidth, fillHeight, radius, null, borderDelta);

					gp.paintContourBackground(g2d, this.slider, fillWidth,
							fillHeight, contour, false, fillScheme, fillScheme,
							0.0f, false, false);
					borderPainter.paintBorder(g2d, this.slider, fillWidth,
							fillHeight, contour, null, borderScheme,
							borderScheme, 0.0f, false);
				}
			}
		}
		g2d.dispose();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.basic.BasicSliderUI#getThumbSize()
	 */
	@Override
	protected Dimension getThumbSize() {
		Icon thumbIcon = this.getIcon();
		return new Dimension(thumbIcon.getIconWidth(), thumbIcon
				.getIconHeight());
	}

	/**
	 * Returns the thumb icon for the associated slider.
	 * 
	 * @return The thumb icon for the associated slider.
	 */
	protected Icon getIcon() {
		if (this.slider.getOrientation() == JSlider.HORIZONTAL) {
			if (this.slider.getPaintTicks() || this.slider.getPaintLabels())
				return this.horizontalIcon;
			else
				return this.roundIcon;
		} else {
			if (this.slider.getPaintTicks() || this.slider.getPaintLabels())
				return this.verticalIcon;
			else
				return this.roundIcon;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.basic.BasicSliderUI#paintThumb(java.awt.Graphics)
	 */
	@Override
	public void paintThumb(Graphics g) {
		Graphics2D graphics = (Graphics2D) g.create();
		// graphics.setComposite(TransitionLayout.getAlphaComposite(slider));
		Rectangle knobBounds = this.thumbRect;
		// System.out.println(thumbRect);

		graphics.translate(knobBounds.x, knobBounds.y);

		Icon icon = this.getIcon();
		if (this.slider.getOrientation() == JSlider.HORIZONTAL) {
			if (icon != null)
				icon.paintIcon(this.slider, graphics, -1, 0);
		} else {
			if (this.slider.getComponentOrientation().isLeftToRight()) {
				if (icon != null)
					icon.paintIcon(this.slider, graphics, 0, -1);
			} else {
				if (icon != null)
					icon.paintIcon(this.slider, graphics, 0, 1);
			}
		}

		// graphics.translate(-knobBounds.x, -knobBounds.y);
		graphics.dispose();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.ComponentUI#paint(java.awt.Graphics,
	 * javax.swing.JComponent)
	 */
	@Override
	public void paint(Graphics g, final JComponent c) {
		Graphics2D graphics = (Graphics2D) g.create();

		ComponentState currState = ComponentState.getState(this.thumbModel,
				this.slider);
		float alpha = SubstanceColorSchemeUtilities.getAlpha(this.slider,
				currState);

		BackgroundPaintingUtils.updateIfOpaque(graphics, c);

		recalculateIfInsetsChanged();
		recalculateIfOrientationChanged();
		final Rectangle clip = graphics.getClipBounds();

		if (!clip.intersects(trackRect) && slider.getPaintTrack())
			calculateGeometry();

		graphics.setComposite(TransitionLayout.getAlphaComposite(this.slider,
				alpha, g));
		if (slider.getPaintTrack() && clip.intersects(trackRect)) {
			paintTrack(graphics);
		}
		if (slider.getPaintTicks() && clip.intersects(tickRect)) {
			paintTicks(graphics);
		}
		if (slider.hasFocus() && clip.intersects(focusRect)) {
			paintFocus(graphics);
		}
		if (clip.intersects(thumbRect)) {
			paintThumb(graphics);
		}
		graphics.setComposite(TransitionLayout.getAlphaComposite(this.slider,
				1.0f, g));
		if (slider.getPaintLabels() && clip.intersects(labelRect)) {
			paintLabels(graphics);
		}

		if (!this.slider.hasFocus()) {
			if (FadeTracker.getInstance().isTracked(c, FadeKind.FOCUS))
				this.paintFocus(graphics);
		}

		// graphics.setColor(Color.red);
		// graphics.draw(trackRect);
		// graphics.setColor(Color.green);
		// graphics.draw(tickRect);
		// graphics.setColor(Color.blue);
		// graphics.draw(thumbRect);
		// graphics.setColor(Color.magenta);
		// graphics.draw(labelRect);

		graphics.dispose();
	}

	/**
	 * Returns the button model for tracking the thumb transitions.
	 * 
	 * @return Button model for tracking the thumb transitions.
	 */
	public ButtonModel getButtonModel() {
		return this.thumbModel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jvnet.substance.Trackable#isInside(java.awt.event.MouseEvent)
	 */
	public boolean isInside(MouseEvent me) {
		Rectangle thumbB = this.thumbRect;
		if (thumbB == null)
			return false;
		return thumbB.contains(me.getX(), me.getY());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.plaf.basic.BasicSliderUI#installDefaults(javax.swing.JSlider)
	 */
	@Override
	protected void installDefaults(JSlider slider) {
		super.installDefaults(slider);
		Font f = slider.getFont();
		if (f == null || f instanceof UIResource) {
			slider.setFont(new FontUIResource(SubstanceLookAndFeel
					.getFontPolicy().getFontSet("Substance", null)
					.getControlFont()));
		}
		int size = SubstanceSizeUtils.getSliderIconSize(SubstanceSizeUtils
				.getComponentFontSize(slider));
		// System.out.println("Slider size : " + size);
		this.horizontalIcon = SubstanceIconFactory.getSliderHorizontalIcon(
				size, false);
		this.roundIcon = SubstanceIconFactory.getSliderRoundIcon(size);
		this.verticalIcon = SubstanceIconFactory.getSliderVerticalIcon(size,
				false);

		int focusIns = (int) Math.ceil(2.0 * SubstanceSizeUtils
				.getFocusStrokeWidth(SubstanceSizeUtils
						.getComponentFontSize(slider)));
		this.focusInsets = new Insets(focusIns, focusIns, focusIns, focusIns);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.plaf.basic.BasicSliderUI#installListeners(javax.swing.JSlider
	 * )
	 */
	@Override
	protected void installListeners(final JSlider slider) {
		super.installListeners(slider);

		// fix for defect 109 - memory leak on changing skin
		this.substanceRolloverListener = new RolloverControlListener(this,
				this.thumbModel);
		slider.addMouseListener(this.substanceRolloverListener);
		slider.addMouseMotionListener(this.substanceRolloverListener);

		this.substancePropertyChangeListener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if ("enabled".equals(evt.getPropertyName())) {
					SubstanceSliderUI.this.thumbModel.setEnabled(slider
							.isEnabled());
				}
				if ("font".equals(evt.getPropertyName())) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							slider.updateUI();
						}
					});
				}
			}
		};
		this.slider
				.addPropertyChangeListener(this.substancePropertyChangeListener);

		this.substanceFadeStateListener = new FadeStateListener(this.slider,
				this.thumbModel, SubstanceCoreUtilities
						.getFadeCallback(this.slider, this.thumbModel, false,
								false, this.slider));
		this.substanceFadeStateListener.registerListeners(false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.plaf.basic.BasicSliderUI#uninstallListeners(javax.swing.JSlider
	 * )
	 */
	@Override
	protected void uninstallListeners(JSlider slider) {
		super.uninstallListeners(slider);

		// fix for defect 109 - memory leak on changing skin
		slider.removeMouseListener(this.substanceRolloverListener);
		slider.removeMouseMotionListener(this.substanceRolloverListener);
		this.substanceRolloverListener = null;

		slider
				.removePropertyChangeListener(this.substancePropertyChangeListener);
		this.substancePropertyChangeListener = null;

		this.substanceFadeStateListener.unregisterListeners();
		this.substanceFadeStateListener = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.basic.BasicSliderUI#paintFocus(java.awt.Graphics)
	 */
	@Override
	public void paintFocus(Graphics g) {
		SubstanceCoreUtilities.paintFocus(g, this.slider, this.slider, null,
				null, 1.0f, (int) Math.ceil(SubstanceSizeUtils
						.getFocusStrokeWidth(SubstanceSizeUtils
								.getComponentFontSize(this.slider))) / 2);
	}

	/**
	 * Returns the amount that the thumb goes past the slide bar.
	 * 
	 * @return Amount that the thumb goes past the slide bar.
	 */
	protected int getThumbOverhang() {
		return (int) (this.getThumbSize().getHeight() - this.getTrackWidth()) / 2;
	}

	/**
	 * Returns the shorter dimension of the track.
	 * 
	 * @return Shorter dimension of the track.
	 */
	protected int getTrackWidth() {
		return SubstanceSizeUtils.getSliderTrackSize(SubstanceSizeUtils
				.getComponentFontSize(this.slider));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.basic.BasicSliderUI#getTickLength()
	 */
	@Override
	protected int getTickLength() {
		return SubstanceSizeUtils.getSliderTickSize(SubstanceSizeUtils
				.getComponentFontSize(this.slider));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.basic.BasicSliderUI#paintTicks(java.awt.Graphics)
	 */
	@Override
	public void paintTicks(Graphics g) {
		Rectangle tickBounds = this.tickRect;
		SubstanceColorScheme tickScheme = SubstanceColorSchemeUtilities
				.getColorScheme(this.slider,
						ColorSchemeAssociationKind.SEPARATOR, this.slider
								.isEnabled() ? ComponentState.DEFAULT
								: ComponentState.DISABLED_UNSELECTED);
		if (this.slider.getOrientation() == JSlider.HORIZONTAL) {
			int value = this.slider.getMinimum()
					+ this.slider.getMinorTickSpacing();
			int xPos = 0;

			if ((this.slider.getMinorTickSpacing() > 0)
					&& (this.slider.getMajorTickSpacing() > 0)) {
				// collect x's of the minor ticks
				java.util.List<Integer> minorXs = new ArrayList<Integer>();
				while (value < this.slider.getMaximum()) {
					int delta = value - this.slider.getMinimum();
					if (delta % this.slider.getMajorTickSpacing() != 0) {
						xPos = this.xPositionForValue(value);
						minorXs.add(xPos - 1);
					}
					value += this.slider.getMinorTickSpacing();
				}
				// and paint them in one call
				SeparatorPainterUtils.paintVerticalLines(g, this.slider,
						tickScheme, tickBounds.y, minorXs,
						tickBounds.height / 2, 0.75f);
			}

			if (this.slider.getMajorTickSpacing() > 0) {
				// collect x's of the major ticks
				java.util.List<Integer> majorXs = new ArrayList<Integer>();
				value = this.slider.getMinimum()
						+ this.slider.getMajorTickSpacing();
				while (value < this.slider.getMaximum()) {
					xPos = this.xPositionForValue(value);
					majorXs.add(xPos - 1);
					value += this.slider.getMajorTickSpacing();
				}
				// and paint them in one call
				SeparatorPainterUtils.paintVerticalLines(g, this.slider,
						tickScheme, tickBounds.y, majorXs, tickBounds.height,
						0.75f);
			}
		} else {
			g.translate(tickBounds.x, 0);

			int value = this.slider.getMinimum()
					+ this.slider.getMinorTickSpacing();
			int yPos = 0;

			boolean ltr = this.slider.getComponentOrientation().isLeftToRight();
			if (this.slider.getMinorTickSpacing() > 0) {
				// collect y's of the minor ticks
				java.util.List<Integer> minorYs = new ArrayList<Integer>();
				int offset = 0;
				if (!ltr) {
					offset = tickBounds.width - tickBounds.width / 2;
				}

				while (value < this.slider.getMaximum()) {
					yPos = this.yPositionForValue(value);
					minorYs.add(yPos);
					value += this.slider.getMinorTickSpacing();
				}

				// and paint them in one call
				SeparatorPainterUtils.paintHorizontalLines(g, this.slider,
						tickScheme, offset, minorYs, tickBounds.width / 2,
						ltr ? 0.75f : 0.25f, ltr);
			}

			if (this.slider.getMajorTickSpacing() > 0) {
				// collect y's of the major ticks
				java.util.List<Integer> majorYs = new ArrayList<Integer>();
				value = this.slider.getMinimum()
						+ this.slider.getMajorTickSpacing();

				while (value < this.slider.getMaximum()) {
					yPos = this.yPositionForValue(value);
					majorYs.add(yPos);
					value += this.slider.getMajorTickSpacing();
				}

				// and paint them in one call
				SeparatorPainterUtils.paintHorizontalLines(g, this.slider,
						tickScheme, 0, majorYs, tickBounds.width, ltr ? 0.75f
								: 0.25f, ltr);
			}
			g.translate(-tickBounds.x, 0);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.basic.BasicSliderUI#calculateTickRect()
	 */
	@Override
	protected void calculateTickRect() {
		if (this.slider.getOrientation() == JSlider.HORIZONTAL) {
			this.tickRect.x = this.trackRect.x;
			this.tickRect.y = this.trackRect.y + this.trackRect.height;
			this.tickRect.width = this.trackRect.width;
			this.tickRect.height = (this.slider.getPaintTicks()) ? this
					.getTickLength() : 0;
		} else {
			this.tickRect.width = (this.slider.getPaintTicks()) ? this
					.getTickLength() : 0;
			if (this.slider.getComponentOrientation().isLeftToRight()) {
				this.tickRect.x = this.trackRect.x + this.trackRect.width;
			} else {
				this.tickRect.x = this.trackRect.x - this.tickRect.width;
			}
			this.tickRect.y = this.trackRect.y;
			this.tickRect.height = this.trackRect.height;
		}

		if (this.slider.getPaintTicks()) {
			if (this.slider.getOrientation() == JSlider.HORIZONTAL) {
				this.tickRect.y -= 3;
			} else {
				if (this.slider.getComponentOrientation().isLeftToRight()) {
					this.tickRect.x -= 2;
				} else {
					this.tickRect.x += 2;
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.basic.BasicSliderUI#calculateLabelRect()
	 */
	@Override
	protected void calculateLabelRect() {
		super.calculateLabelRect();
		if ((this.slider.getOrientation() == JSlider.VERTICAL)
				&& !this.slider.getPaintTicks()
				&& this.slider.getComponentOrientation().isLeftToRight()) {
			this.labelRect.x += 3;
		}
		if (this.slider.getOrientation() == JSlider.VERTICAL) {
			this.labelRect.width = getHeightOfTallestLabel();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.basic.BasicSliderUI#calculateThumbLocation()
	 */
	@Override
	protected void calculateThumbLocation() {
		super.calculateThumbLocation();
		Rectangle trackRect = this.getPaintTrackRect();
		if (slider.getOrientation() == JSlider.HORIZONTAL) {
			int valuePosition = xPositionForValue(slider.getValue());

			double centerY = trackRect.y + trackRect.height / 2.0;
			thumbRect.y = (int) (centerY - thumbRect.height / 2.0) + 1;

			thumbRect.x = valuePosition - thumbRect.width / 2;
		} else {
			int valuePosition = yPositionForValue(slider.getValue());

			double centerX = trackRect.x + trackRect.width / 2.0;
			thumbRect.x = (int) (centerX - thumbRect.width / 2.0) + 1;

			thumbRect.y = valuePosition - (thumbRect.height / 2);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.plaf.basic.BasicSliderUI#getPreferredSize(javax.swing.JComponent
	 * )
	 */
	@Override
	public Dimension getPreferredSize(JComponent c) {
		this.recalculateIfInsetsChanged();
		Dimension d;
		if (this.slider.getOrientation() == JSlider.VERTICAL) {
			d = new Dimension(this.getPreferredVerticalSize());
			d.width = this.insetCache.left + this.insetCache.right;
			d.width += this.focusInsets.left + this.focusInsets.right;
			d.width += this.trackRect.width;
			if (this.slider.getPaintTicks())
				d.width += getTickLength();
			if (this.slider.getPaintLabels())
				d.width += getWidthOfWidestLabel();
			d.width += 3;
		} else {
			d = new Dimension(this.getPreferredHorizontalSize());
			d.height = this.insetCache.top + this.insetCache.bottom;
			d.height += this.focusInsets.top + this.focusInsets.bottom;
			d.height += this.trackRect.height;
			if (this.slider.getPaintTicks())
				d.height += getTickLength();
			if (this.slider.getPaintLabels())
				d.height += getHeightOfTallestLabel();
			d.height += 3;
		}

		return d;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.basic.BasicSliderUI#setThumbLocation(int, int)
	 */
	@Override
	public void setThumbLocation(int x, int y) {
		super.setThumbLocation(x, y);
		this.slider.repaint();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.basic.BasicSliderUI#getPreferredHorizontalSize()
	 */
	@Override
	public Dimension getPreferredHorizontalSize() {
		return new Dimension(SubstanceSizeUtils.getAdjustedSize(
				SubstanceSizeUtils.getComponentFontSize(this.slider), 200, 1,
				20, false), 21);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.basic.BasicSliderUI#getPreferredVerticalSize()
	 */
	@Override
	public Dimension getPreferredVerticalSize() {
		return new Dimension(21, SubstanceSizeUtils.getAdjustedSize(
				SubstanceSizeUtils.getComponentFontSize(this.slider), 200, 1,
				20, false));
	}
}
