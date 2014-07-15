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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicMenuUI;

import org.jvnet.lafwidget.animation.FadeKind;
import org.jvnet.lafwidget.animation.FadeStateListener;
import org.jvnet.substance.utils.SubstanceCoreUtilities;
import org.jvnet.substance.utils.SubstanceSizeUtils;
import org.jvnet.substance.utils.icon.MenuArrowIcon;
import org.jvnet.substance.utils.menu.MenuUtilities;
import org.jvnet.substance.utils.menu.SubstanceMenu;
import org.jvnet.substance.utils.menu.MenuUtilities.MenuPropertyListener;

/**
 * UI for menus in <b>Substance</b> look and feel.
 * 
 * @author Kirill Grouchnikov
 */
public class SubstanceMenuUI extends BasicMenuUI implements SubstanceMenu {
	/**
	 * For rollover effects - enhancement 93.
	 */
	protected MouseListener substanceMouseListener;

	/**
	 * Listener for fade animations.
	 */
	protected FadeStateListener substanceFadeStateListener;

	/**
	 * Listens on all changes to the underlying menu item.
	 */
	protected MenuPropertyListener substanceMenuPropertyListener;

	/**
	 * Property change listener. Listens on changes to
	 * {@link AbstractButton#MODEL_CHANGED_PROPERTY} property.
	 */
	protected PropertyChangeListener substancePropertyListener;

	/**
	 * For rollover effects - enhancement 93.
	 */
	protected FocusListener substanceFocusListener;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.ComponentUI#createUI(javax.swing.JComponent)
	 */
	public static ComponentUI createUI(JComponent comp) {
		SubstanceCoreUtilities.testComponentCreationThreadingViolation(comp);
		return new SubstanceMenuUI();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.basic.BasicMenuUI#installDefaults()
	 */
	@Override
	protected void installDefaults() {
		super.installDefaults();
		this.menuItem.setRolloverEnabled(true);
		// this.menuItem.setOpaque(false);

		this.arrowIcon = new MenuArrowIcon((JMenu) this.menuItem);

		this.defaultTextIconGap = SubstanceSizeUtils
				.getTextIconGap(SubstanceSizeUtils
						.getComponentFontSize(this.menuItem));

		this.menuItem.putClientProperty(SubstanceLookAndFeel.FLAT_PROPERTY,
				Boolean.TRUE);
		this.menuItem.putClientProperty(SubstanceCoreUtilities.USE_HIGHLIGHT,
				Boolean.TRUE);

		LookAndFeel.installProperty(menuItem, "opaque", Boolean.FALSE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.basic.BasicMenuUI#installListeners()
	 */
	@Override
	protected void installListeners() {
		super.installListeners();

		// Improving performance on big menus.
		this.substanceMenuPropertyListener = new MenuPropertyListener(
				this.menuItem);
		this.substanceMenuPropertyListener.install();

		// fix for enhancement 93 - rollover effects on menu items
		this.substanceMouseListener = new MouseAdapter() {
			// fix for defect 93 - no rollover effects on menu
			// items that are not in the selected path
			private boolean toRepaint() {
				MenuElement[] selectedMenuPath = MenuSelectionManager
						.defaultManager().getSelectedPath();
				for (MenuElement elem : selectedMenuPath) {
					if (elem == SubstanceMenuUI.this.menuItem) {
						return true;
					}
				}
				return (selectedMenuPath.length == 0);
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				if (this.toRepaint()) {
					menuItem.getModel().setRollover(true);
					// fix for issue 371 - repaint the menu bar since the
					// menu is marked as flat
					Rectangle bounds = menuItem.getBounds();
					menuItem.getParent().repaint(bounds.x, bounds.y,
							bounds.width, bounds.height);
				}
			}

			@Override
			public void mouseExited(MouseEvent e) {
				if (this.toRepaint()) {
					menuItem.getModel().setRollover(false);
					// fix for issue 371 - repaint the menu bar since the
					// menu is marked as flat
					Rectangle bounds = menuItem.getBounds();
					menuItem.getParent().repaint(bounds.x, bounds.y,
							bounds.width, bounds.height);
				}
			}
		};
		this.menuItem.addMouseListener(this.substanceMouseListener);
		this.substanceFocusListener = new FocusAdapter() {
			// fix for defect 93 - no rollover effects on menu
			// items that are not in the selected path
			private boolean toRepaint() {
				MenuElement[] selectedMenuPath = MenuSelectionManager
						.defaultManager().getSelectedPath();
				for (MenuElement elem : selectedMenuPath) {
					if (elem == SubstanceMenuUI.this.menuItem) {
						return true;
					}
				}
				return (selectedMenuPath.length == 0);
			}

			@Override
			public void focusLost(FocusEvent e) {
				if (toRepaint()) {
					menuItem.getModel().setRollover(false);
					// fix for issue 371 - repaint the menu bar since the
					// menu is marked as flat
					Rectangle bounds = menuItem.getBounds();
					menuItem.getParent().repaint(bounds.x, bounds.y,
							bounds.width, bounds.height);
				}
			}
		};
		this.menuItem.addFocusListener(this.substanceFocusListener);

		final Set<FadeKind> toIgnore = new HashSet<FadeKind>();
		// fix for issue 371 - repaint the menu bar since the
		// menu is marked as flat
		this.substanceFadeStateListener = new FadeStateListener(this.menuItem,
				null, this.menuItem.getModel(), SubstanceCoreUtilities
						.getFadeCallback(this.menuItem, this.menuItem
								.getModel(), false, true, this.menuItem),
				toIgnore);
		this.substanceFadeStateListener.registerListeners();

		// this.menuItem.getModel().addChangeListener(new ChangeListener() {
		// public void stateChanged(ChangeEvent e) {
		// ButtonModel bm = menuItem.getModel();
		// System.out.println(menuItem.getText() + " e:" + bm.isEnabled()
		// + ":a:" + bm.isArmed() + ":r:" + bm.isRollover()
		// + ":p:" + bm.isPressed() + ":s:" + bm.isSelected());
		// }
		// });

		this.substancePropertyListener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if (AbstractButton.MODEL_CHANGED_PROPERTY.equals(evt
						.getPropertyName())) {
					if (substanceFadeStateListener != null)
						substanceFadeStateListener.unregisterListeners();
					substanceFadeStateListener = new FadeStateListener(
							menuItem,
							null,
							menuItem.getModel(),
							SubstanceCoreUtilities
									.getFadeCallback(menuItem, menuItem
											.getModel(), false, false, menuItem),
							toIgnore);
					substanceFadeStateListener.registerListeners();
				}
				if ("font".equals(evt.getPropertyName())) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							if (menuItem != null) {
								menuItem.updateUI();
							}
						}
					});
				}
			}
		};
		this.menuItem.addPropertyChangeListener(this.substancePropertyListener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.basic.BasicMenuUI#uninstallListeners()
	 */
	@Override
	protected void uninstallListeners() {
		super.uninstallListeners();

		// Improving performance on big menus.
		this.substanceMenuPropertyListener.uninstall();
		this.substanceMenuPropertyListener = null;

		// fix for enhancement 93 - rollover effects on menu items
		this.menuItem.removeMouseListener(this.substanceMouseListener);
		this.substanceMouseListener = null;
		this.menuItem.removeFocusListener(this.substanceFocusListener);
		this.substanceFocusListener = null;

		this.menuItem
				.removePropertyChangeListener(this.substancePropertyListener);
		this.substancePropertyListener = null;

		this.substanceFadeStateListener.unregisterListeners();
		this.substanceFadeStateListener = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jvnet.substance.SubstanceMenu#getAssociatedMenuItem()
	 */
	public JMenuItem getAssociatedMenuItem() {
		return this.menuItem;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jvnet.substance.SubstanceMenu#getAcceleratorFont()
	 */
	public Font getAcceleratorFont() {
		return this.acceleratorFont;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jvnet.substance.SubstanceMenu#getArrowIcon()
	 */
	public Icon getArrowIcon() {
		return this.arrowIcon;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jvnet.substance.SubstanceMenu#getCheckIcon()
	 */
	public Icon getCheckIcon() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jvnet.substance.SubstanceMenu#getDefaultTextIconGap()
	 */
	public int getDefaultTextIconGap() {
		return this.defaultTextIconGap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.plaf.basic.BasicMenuItemUI#getPreferredMenuItemSize(javax
	 * .swing.JComponent, javax.swing.Icon, javax.swing.Icon, int)
	 */
	@Override
	protected Dimension getPreferredMenuItemSize(JComponent c, Icon checkIcon,
			Icon arrowIcon, int defaultTextIconGap) {
		Dimension superDim = super.getPreferredMenuItemSize(c, checkIcon,
				arrowIcon, defaultTextIconGap);

		if (MenuUtilities.getPopupLayoutMetrics(menuItem, false) != null) {
			return new Dimension(MenuUtilities.getPreferredWidth(menuItem),
					superDim.height);
		}

		return superDim;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.plaf.basic.BasicMenuItemUI#paintMenuItem(java.awt.Graphics,
	 * javax.swing.JComponent, javax.swing.Icon, javax.swing.Icon,
	 * java.awt.Color, java.awt.Color, int)
	 */
	@Override
	protected void paintMenuItem(Graphics g, JComponent c, Icon checkIcon,
			Icon arrowIcon, Color background, Color foreground,
			int defaultTextIconGap) {
		MenuUtilities.paintMenuItem(g, menuItem, checkIcon, arrowIcon,
				defaultTextIconGap);
	}
}
