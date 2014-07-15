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
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.ComboPopup;

import org.jvnet.lafwidget.animation.FadeStateListener;
import org.jvnet.lafwidget.layout.TransitionLayout;
import org.jvnet.substance.api.ComponentState;
import org.jvnet.substance.api.SubstanceConstants.Side;
import org.jvnet.substance.api.renderers.SubstanceDefaultComboBoxRenderer;
import org.jvnet.substance.painter.utils.HighlightPainterUtils;
import org.jvnet.substance.utils.*;
import org.jvnet.substance.utils.border.SubstanceBorder;
import org.jvnet.substance.utils.combo.*;

/**
 * UI for combo boxes in <b>Substance </b> look and feel.
 * 
 * @author Kirill Grouchnikov
 * @author Thomas Bierhance http://www.orbital-computer.de/JComboBox/
 * @author inostock
 */
public class SubstanceComboBoxUI extends BasicComboBoxUI {
	/**
	 * Property change handler on <code>enabled</code> property,
	 * <code>componentOrientation</code> property and on
	 * {@link SubstanceLookAndFeel#COMBO_BOX_POPUP_FLYOUT_ORIENTATION} property.
	 */
	protected ComboBoxPropertyChangeHandler substanceChangeHandler;

	/**
	 * Listener for fade animations.
	 */
	protected FadeStateListener substanceFadeStateListener;

	/**
	 * Focus listener on the combobox.
	 */
	protected FocusListener substanceFocusListener;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.ComponentUI#createUI(javax.swing.JComponent)
	 */
	public static ComponentUI createUI(JComponent comp) {
		SubstanceCoreUtilities.testComponentCreationThreadingViolation(comp);
		SubstanceComboBoxUI ui = new SubstanceComboBoxUI();
		ui.comboBox = (JComboBox) comp;
		ui.comboBox.setOpaque(false);
		return ui;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.basic.BasicComboBoxUI#createArrowButton()
	 */
	@Override
	protected JButton createArrowButton() {
		SubstanceComboBoxButton result = new SubstanceComboBoxButton(
				this.comboBox);
		result.setFont(this.comboBox.getFont());
		result.setIcon(getCurrentIcon(result));
		// System.out.println("Combo [@" + this.comboBox.hashCode() + "] font "
		// + this.comboBox.getFont().getSize() + ", button [@"
		// + result.hashCode() + "] font " + result.getFont().getSize());
		return result;
	}

	/**
	 * Returns the icon for the specified arrow button.
	 * 
	 * @param button
	 *            Arrow button.
	 * @return Icon for the specified button.
	 */
	private Icon getCurrentIcon(JButton button) {
		Icon icon = SubstanceCoreUtilities
				.getArrowIcon(button, SubstanceCoreUtilities
						.getPopupFlyoutOrientation(this.comboBox));
		return icon;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.basic.BasicComboBoxUI#createRenderer()
	 */
	@Override
	protected ListCellRenderer createRenderer() {
		return new SubstanceDefaultComboBoxRenderer.SubstanceUIResource(
				this.comboBox);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.basic.BasicComboBoxUI#installListeners()
	 */
	@Override
	protected void installListeners() {
		super.installListeners();
		this.substanceChangeHandler = new ComboBoxPropertyChangeHandler();
		this.comboBox.addPropertyChangeListener(this.substanceChangeHandler);

		this.substanceFadeStateListener = new FadeStateListener(this.comboBox,
				null, null);
		this.substanceFadeStateListener.registerListeners();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.basic.BasicComboBoxUI#uninstallListeners()
	 */
	@Override
	protected void uninstallListeners() {
		this.substanceFadeStateListener.unregisterListeners();
		this.substanceFadeStateListener = null;

		this.comboBox.removePropertyChangeListener(this.substanceChangeHandler);
		this.substanceChangeHandler = null;
		super.uninstallListeners();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.basic.BasicComboBoxUI#installDefaults()
	 */
	@Override
	protected void installDefaults() {
		super.installDefaults();
		Border b = this.comboBox.getBorder();
		if (b == null || b instanceof UIResource) {
			Border newB = new SubstanceBorder(SubstanceSizeUtils
					.getComboBorderInsets(SubstanceSizeUtils
							.getComponentFontSize(this.comboBox)));

			this.comboBox.setBorder(newB);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.basic.BasicComboBoxUI#createLayoutManager()
	 */
	@Override
	protected LayoutManager createLayoutManager() {
		return new SubstanceComboBoxLayoutManager();
	}

	/**
	 * Layout manager for combo box.
	 * 
	 * @author Kirill Grouchnikov
	 */
	private class SubstanceComboBoxLayoutManager extends
			BasicComboBoxUI.ComboBoxLayoutManager {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.LayoutManager#layoutContainer(java.awt.Container)
		 */
		@Override
		public void layoutContainer(Container parent) {
			JComboBox cb = (JComboBox) parent;

			int width = cb.getWidth();
			int height = cb.getHeight();

			Insets insets = SubstanceComboBoxUI.this.getInsets();
			int buttonWidth = SubstanceSizeUtils
					.getScrollBarWidth(SubstanceSizeUtils
							.getComponentFontSize(comboBox));
			// buttonWidth = Math.max(buttonWidth,
			// arrowButton.getPreferredSize().width);

			if (SubstanceComboBoxUI.this.arrowButton != null) {
				if (cb.getComponentOrientation().isLeftToRight()) {
					SubstanceComboBoxUI.this.arrowButton
							.setBounds(
									width - buttonWidth - insets.right,
									0,
									buttonWidth
											+ SubstanceSizeUtils
													.getComboBorderInsets(SubstanceSizeUtils
															.getComponentFontSize(comboBox)).right,
									height);
				} else {
					int left = SubstanceSizeUtils
							.getComboBorderInsets(SubstanceSizeUtils
									.getComponentFontSize(comboBox)).left;
					SubstanceComboBoxUI.this.arrowButton.setBounds(insets.left
							- left, 0, buttonWidth + left, height);
				}
			}
			if (SubstanceComboBoxUI.this.editor != null) {
				SubstanceComboBoxUI.this.editor
						.setBounds(SubstanceComboBoxUI.this
								.rectangleForCurrentValue());
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.basic.BasicComboBoxUI#getDefaultSize()
	 */
	@Override
	protected Dimension getDefaultSize() {
		Component rend = new SubstanceDefaultComboBoxRenderer(this.comboBox)
				.getListCellRendererComponent(listBox, " ", -1, false, false);
		rend.setFont(this.comboBox.getFont());

		return rend.getPreferredSize();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.plaf.basic.BasicComboBoxUI#getMinimumSize(javax.swing.JComponent
	 * )
	 */
	@Override
	public Dimension getMinimumSize(JComponent c) {
		if (!this.isMinimumSizeDirty) {
			return new Dimension(this.cachedMinimumSize);
		}

		// Dimension size = null;
		//
		// if (!this.comboBox.isEditable() && this.arrowButton != null
		// && this.arrowButton instanceof SubstanceComboBoxButton) {
		//
		SubstanceComboBoxButton button = (SubstanceComboBoxButton) this.arrowButton;
		Insets buttonInsets = button.getInsets();
		Insets insets = this.comboBox.getInsets();

		Dimension size = this.getDisplaySize();

		size.width += insets.left + insets.right;
		size.width += buttonInsets.left + buttonInsets.right;
		size.width += button.getMinimumSize().getWidth();
		size.height += insets.top + insets.bottom;
		// } else if (this.comboBox.isEditable() && this.arrowButton != null
		// && this.editor != null) {
		// size = super.getMinimumSize(c);
		// } else {
		// size = super.getMinimumSize(c);
		// }

		this.cachedMinimumSize.setSize(size.width, size.height);
		this.isMinimumSizeDirty = false;

		return new Dimension(this.cachedMinimumSize);
	}

	/**
	 * This property change handler changes combo box arrow icon based on the
	 * enabled status of the combo box.
	 * 
	 * @author Kirill Grouchnikov
	 */
	public class ComboBoxPropertyChangeHandler extends PropertyChangeHandler {
		/*
		 * (non-Javadoc)
		 * 
		 * @seejavax.swing.plaf.basic.BasicComboBoxUI$PropertyChangeHandler#
		 * propertyChange(java.beans.PropertyChangeEvent)
		 */
		@Override
		public void propertyChange(final PropertyChangeEvent e) {
			String propertyName = e.getPropertyName();

			if (propertyName.equals("componentOrientation")) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						if (SubstanceComboBoxUI.this.comboBox == null)
							return;
						final ComponentOrientation newOrientation = (ComponentOrientation) e
								.getNewValue();
						final ListCellRenderer cellRenderer = SubstanceComboBoxUI.this.comboBox
								.getRenderer();
						final ComboBoxEditor editor = SubstanceComboBoxUI.this.comboBox
								.getEditor();
						if (SubstanceComboBoxUI.this.popup instanceof Component) {
							final Component cPopup = (Component) SubstanceComboBoxUI.this.popup;
							cPopup.applyComponentOrientation(newOrientation);
							cPopup.doLayout();
						}
						if (cellRenderer instanceof Component) {
							((Component) cellRenderer)
									.applyComponentOrientation(newOrientation);
						}
						if ((editor != null)
								&& (editor.getEditorComponent() != null)) {
							(editor.getEditorComponent())
									.applyComponentOrientation(newOrientation);
						}
						if (SubstanceComboBoxUI.this.comboBox != null)
							SubstanceComboBoxUI.this.comboBox.repaint();

						configureArrowButtonStraightSide();
					}
				});
			}

			if (SubstanceLookAndFeel.COMBO_BOX_POPUP_FLYOUT_ORIENTATION
					.equals(propertyName)) {
				((SubstanceComboBoxButton) arrowButton)
						.setIcon(SubstanceCoreUtilities
								.getArrowIcon(
										arrowButton,
										SubstanceCoreUtilities
												.getPopupFlyoutOrientation(SubstanceComboBoxUI.this.comboBox)));

			}

			if ("font".equals(propertyName)) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						if (comboBox != null)
							comboBox.updateUI();
					}
				});
			}

			if ("background".equals(propertyName)) {
				if (comboBox.isEditable()) {
					comboBox.getEditor().getEditorComponent().setBackground(
							comboBox.getBackground());
					popup.getList().setBackground(comboBox.getBackground());
				}
			}
			// Do not call super - fix for bug 63
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.basic.BasicComboBoxUI#createPopup()
	 */
	@Override
	protected ComboPopup createPopup() {
		final ComboPopup sPopup = new SubstanceComboPopup(this.comboBox);

		final ComponentOrientation currOrientation = this.comboBox
				.getComponentOrientation();

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (SubstanceComboBoxUI.this.comboBox == null)
					return;

				if (sPopup instanceof Component) {
					final Component cPopup = (Component) sPopup;
					cPopup.applyComponentOrientation(currOrientation);
					cPopup.doLayout();
				}
				ListCellRenderer cellRenderer = SubstanceComboBoxUI.this.comboBox
						.getRenderer();
				if (cellRenderer instanceof Component) {
					((Component) cellRenderer)
							.applyComponentOrientation(currOrientation);
				}
				ComboBoxEditor editor = SubstanceComboBoxUI.this.comboBox
						.getEditor();
				if ((editor != null) && (editor.getEditorComponent() != null)) {
					(editor.getEditorComponent())
							.applyComponentOrientation(currOrientation);
				}
				SubstanceComboBoxUI.this.comboBox.repaint();
			}
		});
		return sPopup;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.plaf.basic.BasicComboBoxUI#paintCurrentValueBackground(java
	 * .awt.Graphics, java.awt.Rectangle, boolean)
	 */
	@Override
	public void paintCurrentValueBackground(Graphics g, Rectangle bounds,
			boolean hasFocus) {
		ListUI listUI = this.popup.getList().getUI();
		SubstanceListUI ui = (SubstanceListUI) listUI;
		ComponentState state = ui.getCellState(-1, null);
		boolean isEnabled = this.comboBox.isEnabled();
		boolean isSelected = hasFocus;
		if (isSelected && isEnabled) {
			state = ComponentState.SELECTED;
		}
		if (!isEnabled) {
			state = ComponentState.DISABLED_UNSELECTED;
		}

		Graphics2D graphics = (Graphics2D) g.create();
		float alpha = SubstanceColorSchemeUtilities.getAlpha(this.comboBox,
				state) * 0.8f;

		int componentFontSize = SubstanceSizeUtils
				.getComponentFontSize(this.comboBox);
		int borderDelta = (int) Math.floor(SubstanceSizeUtils
				.getBorderStrokeWidth(componentFontSize) / 2.0);
		Shape contour = SubstanceOutlineUtilities.getBaseOutline(this.comboBox
				.getWidth(), this.comboBox.getHeight(),
				0.5f * SubstanceSizeUtils
						.getClassicButtonCornerRadius(componentFontSize), null,
				borderDelta);
		graphics.clip(contour);

		// Rectangle newBounds = new Rectangle(1, 1, this.comboBox.getWidth() -
		// 2,
		// this.comboBox.getHeight() - 2);

		graphics.setComposite(TransitionLayout.getAlphaComposite(this.comboBox,
				alpha, g));

		HighlightPainterUtils.paintHighlight(graphics, null, this.comboBox,
				new Rectangle(0, 0, this.comboBox.getWidth(), this.comboBox
						.getHeight()), 0.0f, null, state, state, 0.0f);
		graphics.dispose();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.plaf.basic.BasicComboBoxUI#paintCurrentValue(java.awt.Graphics
	 * , java.awt.Rectangle, boolean)
	 */
	@Override
	public void paintCurrentValue(Graphics g, Rectangle bounds, boolean hasFocus) {
		Graphics2D graphics = (Graphics2D) g.create();

		ListCellRenderer renderer = this.comboBox.getRenderer();
		Component c;
		if (hasFocus) {
			c = renderer.getListCellRendererComponent(this.listBox,
					this.comboBox.getSelectedItem(), -1, true, hasFocus);
		} else {
			c = renderer.getListCellRendererComponent(this.listBox,
					this.comboBox.getSelectedItem(), -1, false, hasFocus);
		}
		c.setFont(this.comboBox.getFont());

		// Fix for 4238829: should lay out the JPanel.
		boolean shouldValidate = false;
		if (c instanceof JPanel) {
			shouldValidate = true;
		}

		// SubstanceCoreUtilities.workaroundBug6576507(graphics);

		if (this.comboBox.getComponentOrientation().isLeftToRight()) {
			this.currentValuePane.paintComponent(graphics, c, this.comboBox, 1,
					1, this.arrowButton.getX() - 1,
					this.comboBox.getHeight() - 2, shouldValidate);
		} else {
			int startX = this.arrowButton.getX() + this.arrowButton.getWidth();
			this.currentValuePane.paintComponent(graphics, c, this.comboBox,
					startX, 1, this.comboBox.getWidth() - startX - 1,
					this.comboBox.getHeight() - 2, shouldValidate);
		}

		graphics.dispose();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.basic.BasicComboBoxUI#paint(java.awt.Graphics,
	 * javax.swing.JComponent)
	 */
	@Override
	public void paint(Graphics g, JComponent c) {
		super.paint(g, c);
		this.hasFocus = this.comboBox.hasFocus();
		if (!this.comboBox.isEditable() && this.hasFocus) {
			Rectangle r = this.rectangleForCurrentValue();
			r.y += 1;
			r.height -= 2;
			r.width -= (this.arrowButton.getWidth() + 10);
			this.paintFocus(g, r);
		}
	}

	/**
	 * Paints the focus indication.
	 * 
	 * @param g
	 *            Graphics.
	 * @param bounds
	 *            Bounds for text.
	 */
	protected void paintFocus(Graphics g, Rectangle bounds) {
		int fontSize = SubstanceSizeUtils.getComponentFontSize(this.comboBox);
		int focusRingPadding = SubstanceSizeUtils.getFocusRingPadding(fontSize);
		SubstanceCoreUtilities.paintFocus(g, this.comboBox, this.comboBox,
				SubstanceOutlineUtilities.getBaseOutline(this.arrowButton
						.getX() - 1, this.comboBox.getHeight() - 3
						* focusRingPadding / 2, SubstanceSizeUtils
						.getClassicButtonCornerRadius(fontSize), null, 0),
				bounds, 0.4f, 3 * focusRingPadding / 2);
	}

	/**
	 * Returns the popup of the associated combobox.
	 * 
	 * @return The popup of the associated combobox.
	 */
	public ComboPopup getPopup() {
		return this.popup;
	}

	// /*
	// * (non-Javadoc)
	// *
	// * @see javax.swing.plaf.ComponentUI#update(java.awt.Graphics,
	// * javax.swing.JComponent)
	// */
	// @Override
	// public void update(Graphics g, JComponent c) {
	// if (!this.comboBox.isEditable()) {
	// SubstanceTextUtilities.paintTextCompBackground(g, c);
	// } else {
	// SubstanceTextUtilities.paintTextCompBackground(g, c, this.editor
	// .getBackground(), false);
	// }
	// this.paint(g, c);
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.basic.BasicComboBoxUI#configureArrowButton()
	 */
	@Override
	public void configureArrowButton() {
		super.configureArrowButton();
		// Mustang decided to make the arrow button focusable on
		// focusable comboboxes
		this.arrowButton.setFocusable(false);

		this.substanceFocusListener = new FocusListener() {
			public void focusGained(FocusEvent e) {
				arrowButton.setSelected(true);
			}

			public void focusLost(FocusEvent e) {
				arrowButton.setSelected(false);
			}
		};
		this.arrowButton.setSelected(this.comboBox.hasFocus());
		this.comboBox.addFocusListener(this.substanceFocusListener);

		this.configureArrowButtonStraightSide();
	}

	/**
	 * Configures the straight side of the arrow button.
	 */
	protected void configureArrowButtonStraightSide() {
		this.arrowButton.putClientProperty(
				SubstanceLookAndFeel.BUTTON_SIDE_PROPERTY, this.comboBox
						.getComponentOrientation().isLeftToRight() ? Side.LEFT
						: Side.RIGHT);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.basic.BasicComboBoxUI#unconfigureArrowButton()
	 */
	@Override
	public void unconfigureArrowButton() {
		this.comboBox.removeFocusListener(this.substanceFocusListener);
		this.substanceFocusListener = null;
		super.unconfigureArrowButton();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.basic.BasicComboBoxUI#configureEditor()
	 */
	@Override
	protected void configureEditor() {
		super.configureEditor();
		// This for Mustang - setting Substance once again adds a border on
		// the text field in the combo editor.
		if (this.editor instanceof JComponent) {
			Insets ins = SubstanceSizeUtils
					.getComboTextBorderInsets(SubstanceSizeUtils
							.getComponentFontSize(this.editor));
			((JComponent) this.editor).setBorder(new EmptyBorder(ins.top,
					ins.left, ins.bottom, ins.right));
			this.editor.setBackground(this.comboBox.getBackground());
			// ((JComponent) this.editor).setBorder(new LineBorder(Color.red));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.basic.BasicComboBoxUI#createEditor()
	 */
	@Override
	protected ComboBoxEditor createEditor() {
		return new SubstanceComboBoxEditor.UIResource();
	}
}
