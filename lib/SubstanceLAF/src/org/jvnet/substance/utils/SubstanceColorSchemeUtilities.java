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
package org.jvnet.substance.utils;

import java.awt.Color;
import java.awt.Component;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.plaf.UIResource;

import org.jvnet.lafwidget.animation.FadeKind;
import org.jvnet.substance.api.*;
import org.jvnet.substance.colorscheme.*;
import org.jvnet.substance.painter.decoration.DecorationAreaType;
import org.jvnet.substance.utils.combo.SubstanceComboBoxButton;
import org.jvnet.substance.utils.scroll.SubstanceScrollButton;

/**
 * Utilities related to color schemes. This class is for internal use only.
 * 
 * @author Kirill Grouchnikov
 */
public class SubstanceColorSchemeUtilities {
	private static enum ColorSchemeKind {
		LIGHT, DARK
	}

	/**
	 * Metallic skin.
	 */
	public static final SubstanceSkin METALLIC_SKIN = getMetallicSkin();

	/**
	 * Returns a metallic skin.
	 * 
	 * @return Metallic skin.
	 */
	private static SubstanceSkin getMetallicSkin() {
		SubstanceSkin res = new SubstanceSkin() {
			@Override
			public String getDisplayName() {
				return "Metallic Skin";
			}
		};
		res.registerDecorationAreaSchemeBundle(new SubstanceColorSchemeBundle(
				new MetallicColorScheme(), new MetallicColorScheme(),
				new LightGrayColorScheme()), DecorationAreaType.NONE);
		return res;
	}

	/**
	 * Base interface for colorization support.
	 * 
	 * @author Kirill Grouchnikov
	 */
	private static interface ColorizationSupport {
		/**
		 * Returns the background color of the specified component.
		 * 
		 * @param component
		 *            Component.
		 * @return The background color of the specified component.
		 */
		public Color getBackground(Component component);

		/**
		 * Returns the foreground color of the specified component.
		 * 
		 * @param component
		 *            Component.
		 * @return The foreground color of the specified component.
		 */
		public Color getForeground(Component component);
	}

	/**
	 * Returns a colorized version of the specified color scheme.
	 * 
	 * @param component
	 *            Component.
	 * @param scheme
	 *            Color scheme.
	 * @param isEnabled
	 *            Indicates whether the component is enabled.
	 * @return Colorized version of the specified color scheme.
	 */
	private static SubstanceColorScheme getColorizedScheme(Component component,
			SubstanceColorScheme scheme, boolean isEnabled) {
		ColorizationSupport support = new ColorizationSupport() {
			private boolean toTakeFromParent(Component component) {
				return (component.getParent() != null)
						&& ((component instanceof SubstanceScrollButton)
								|| (component instanceof SubstanceSpinnerButton)
								|| (component instanceof SubstanceComboBoxButton) || (component instanceof SubstanceTitleButton));
			}

			public Color getBackground(Component component) {
				return toTakeFromParent(component) ? component.getParent()
						.getBackground() : component.getBackground();
			}

			public Color getForeground(Component component) {
				return toTakeFromParent(component) ? component.getParent()
						.getForeground() : component.getForeground();
			}
		};
		return getColorizedScheme(component, scheme, support, isEnabled);
	}

	/**
	 * Returns a colorized version of the specified color scheme.
	 * 
	 * @param component
	 *            Component.
	 * @param scheme
	 *            Color scheme.
	 * @param support
	 *            Used to compute the colorized scheme.
	 * @param isEnabled
	 *            Indicates whether the component is enabled.
	 * @return Colorized version of the specified color scheme.
	 */
	private static SubstanceColorScheme getColorizedScheme(Component component,
			SubstanceColorScheme scheme, ColorizationSupport support,
			boolean isEnabled) {
		if (component != null) {
			// Support for enhancement 256 - colorizing
			// controls.
			Color bk = support.getBackground(component);
			Color fg = support.getForeground(component);
			// if (component instanceof SubstanceTitleButton) {
			// if ((fg != null) && (bk != null)) {
			// // guard for issue 322 - these are null when JavaHelp
			// // window is printed.
			// fg = SubstanceColorUtilities.getInterpolatedColor(fg, bk,
			// 0.5);
			// }
			// }
			if (bk instanceof UIResource)
				bk = null;
			if (fg instanceof UIResource) {
				fg = null;
			}
			if ((bk != null) || (fg != null)) {
				double colorization = SubstanceCoreUtilities
						.getColorizationFactor(component);
				if (!isEnabled)
					colorization /= 2.0;
				if (colorization > 0.0) {
					return ShiftColorScheme.getShiftedScheme(scheme, bk,
							colorization, fg, colorization);
				}
			}
		}
		return scheme;
	}

	/**
	 * Returns the color scheme of the specified tabbed pane tab.
	 * 
	 * @param jtp
	 *            Tabbed pane.
	 * @param tabIndex
	 *            Tab index.
	 * @param componentState
	 *            Tab component state.
	 * @return The color scheme of the specified tabbed pane tab.
	 */
	public static SubstanceColorScheme getColorScheme(final JTabbedPane jtp,
			final int tabIndex, ColorSchemeAssociationKind associationKind,
			ComponentState componentState) {
		SubstanceSkin skin = SubstanceCoreUtilities.getSkin(jtp);
		if (skin == null) {
			SubstanceCoreUtilities
					.traceSubstanceApiUsage(jtp,
							"Substance delegate used when Substance is not the current LAF");
		}
		SubstanceColorScheme nonColorized = skin.getColorScheme(jtp,
				associationKind, componentState);
		if (tabIndex >= 0) {
			Component component = jtp.getComponentAt(tabIndex);
			SubstanceColorScheme colorized = getColorizedScheme(component,
					nonColorized, new ColorizationSupport() {
						public Color getBackground(Component component) {
							return jtp.getBackgroundAt(tabIndex);
						}

						public Color getForeground(Component component) {
							return jtp.getForegroundAt(tabIndex);
						}
					}, componentState.isKindActive(FadeKind.ENABLE));
			return colorized;
		} else {
			return getColorizedScheme(jtp, nonColorized, componentState
					.isKindActive(FadeKind.ENABLE));
		}
	}

	// /**
	// * Returns the border color scheme of the specified tabbed pane tab.
	// *
	// * @param jtp
	// * Tabbed pane.
	// * @param tabIndex
	// * Tab index.
	// * @param componentState
	// * Tab component state.
	// * @return The border color scheme of the specified tabbed pane tab.
	// */
	// public static SubstanceColorScheme getBorderColorScheme(
	// final JTabbedPane jtp, final int tabIndex,
	// ComponentState componentState) {
	//
	// SubstanceColorScheme nonColorized = SubstanceCoreUtilities.getSkin(jtp)
	// .getColorScheme(jtp, ColorSchemeAssociationKind.TAB_BORDER,
	// componentState);
	// if (tabIndex >= 0) {
	// Component component = jtp.getComponentAt(tabIndex);
	// SubstanceColorScheme colorized = getColorizedScheme(component,
	// nonColorized, new ColorizationSupport() {
	// public Color getBackground(Component component) {
	// return jtp.getBackgroundAt(tabIndex);
	// }
	//
	// public Color getForeground(Component component) {
	// return jtp.getForegroundAt(tabIndex);
	// }
	// }, componentState.isKindActive(FadeKind.ENABLE));
	// return colorized;
	// } else {
	// return getColorizedScheme(jtp, nonColorized, componentState
	// .isKindActive(FadeKind.ENABLE));
	// }
	// }

	/**
	 * Returns the color scheme of the specified component.
	 * 
	 * @param component
	 *            Component.
	 * @param componentState
	 *            Component state.
	 * @return Component color scheme.
	 */
	public static SubstanceColorScheme getColorScheme(Component component,
			ComponentState componentState) {
		Component orig = component;
		// special case - if the component is marked as flat and
		// it is in the default state, or it is a button
		// that is never painting its background - get the color scheme of the
		// parent
		boolean isButtonThatIsNeverPainted = ((component instanceof AbstractButton) && SubstanceCoreUtilities
				.isButtonNeverPainted((AbstractButton) component));
		if (isButtonThatIsNeverPainted
				|| (SubstanceCoreUtilities.hasFlatAppearance(component, false) && (componentState == ComponentState.DEFAULT))) {
			component = component.getParent();
		}

		SubstanceSkin skin = SubstanceCoreUtilities.getSkin(component);
		if (skin == null) {
			SubstanceCoreUtilities
					.traceSubstanceApiUsage(component,
							"Substance delegate used when Substance is not the current LAF");
		}
		SubstanceColorScheme nonColorized = skin.getColorScheme(component,
				componentState);

		return getColorizedScheme(orig, nonColorized, componentState
				.isKindActive(FadeKind.ENABLE));
	}

	/**
	 * Returns the color scheme of the component.
	 * 
	 * @param component
	 *            Component.
	 * @param componentState
	 *            Component state.
	 * @return Component border color scheme.
	 */
	public static SubstanceColorScheme getColorScheme(Component component,
			ColorSchemeAssociationKind associationKind,
			ComponentState componentState) {
		// special case - if the component is marked as flat and
		// it is in the default state, get the color scheme of the parent.
		// However, flat toolbars should be ignored, since they are
		// the "top" level decoration area.
		if (!(component instanceof JToolBar)
				&& SubstanceCoreUtilities.hasFlatAppearance(component, false)
				&& (componentState == ComponentState.DEFAULT)) {
			component = component.getParent();
		}

		SubstanceColorScheme nonColorized = SubstanceCoreUtilities.getSkin(
				component).getColorScheme(component, associationKind,
				componentState);
		return getColorizedScheme(component, nonColorized, componentState
				.isKindActive(FadeKind.ENABLE));
	}

	// /**
	// * Returns the highlight color scheme of the component.
	// *
	// * @param component
	// * Component.
	// * @param componentState
	// * Component state.
	// * @return Component highlight color scheme.
	// */
	// public static SubstanceColorScheme getHighlightColorScheme(
	// Component component, ComponentState componentState) {
	// return SubstanceCoreUtilities.getSkin(component)
	// .getColorScheme(component,
	// ColorSchemeAssociationKind.HIGHLIGHT, componentState);
	// // .getHighlightColorScheme(component, componentState);
	// }

	/**
	 * Returns the alpha channel of the highlight color scheme of the component.
	 * 
	 * @param component
	 *            Component.
	 * @param componentState
	 *            Component state.
	 * @return Highlight color scheme alpha channel.
	 */
	public static float getHighlightAlpha(Component component,
			ComponentState componentState) {
		return SubstanceCoreUtilities.getSkin(component).getHighlightAlpha(
				component, componentState);
	}

	/**
	 * Returns the alpha channel of the color scheme of the component.
	 * 
	 * @param component
	 *            Component.
	 * @param componentState
	 *            Component state.
	 * @return Color scheme alpha channel.
	 */
	public static float getAlpha(Component component,
			ComponentState componentState) {
		return SubstanceCoreUtilities.getSkin(component).getAlpha(component,
				componentState);
	}

	/**
	 * Used as reference in attention-drawing animations. This field is <b>for
	 * internal use only</b>.
	 */
	public final static SubstanceColorScheme YELLOW = new SunGlareColorScheme();

	/**
	 * Used as reference in attention-drawing animations. This field is <b>for
	 * internal use only</b>.
	 */
	public final static SubstanceColorScheme ORANGE = new SunfireRedColorScheme();

	/**
	 * Used as reference to the green color scheme. This field is <b>for
	 * internal use only</b>.
	 */
	public final static SubstanceColorScheme GREEN = new BottleGreenColorScheme();

	public static SubstanceColorScheme getColorScheme(URL url) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(url.openStream()));
			String name = null;
			ColorSchemeKind kind = null;
			Color ultraLight = null;
			Color extraLight = null;
			Color light = null;
			Color mid = null;
			Color dark = null;
			Color ultraDark = null;
			Color foreground = null;

			SchemeBaseColors base = null;

			while (true) {
				String line = reader.readLine();
				if (line == null)
					break;
				String[] split = line.split("=");
				if (split.length != 2) {
					throw new IllegalArgumentException(
							"Unsupported format in line " + line);
				}
				String key = split[0];
				String value = split[1];
				if ("name".equals(key)) {
					if (name == null) {
						name = value;
						continue;
					}
					throw new IllegalArgumentException(
							"'name' should only be defined once");
				}
				if ("extends".equals(key)) {
					if (base == null) {
						base = getBaseColorScheme(new URL(url, value)
								.openStream());
						continue;
					}
					throw new IllegalArgumentException(
							"'extends' should only be defined once");
				}
				if ("kind".equals(key)) {
					if (kind == null) {
						if ("Light".equals(value)) {
							kind = ColorSchemeKind.LIGHT;
							continue;
						}
						if ("Dark".equals(value)) {
							kind = ColorSchemeKind.DARK;
							continue;
						}
						throw new IllegalArgumentException(
								"Unsupported format in line " + line);
					}
					throw new IllegalArgumentException(
							"'name' should only be defined once");
				}
				if ("colorUltraLight".equals(key)) {
					if (ultraLight == null) {
						ultraLight = Color.decode(value);
						continue;
					}
					throw new IllegalArgumentException(
							"'ultraLight' should only be defined once");
				}
				if ("colorExtraLight".equals(key)) {
					if (extraLight == null) {
						extraLight = Color.decode(value);
						continue;
					}
					throw new IllegalArgumentException(
							"'extraLight' should only be defined once");
				}
				if ("colorLight".equals(key)) {
					if (light == null) {
						light = Color.decode(value);
						continue;
					}
					throw new IllegalArgumentException(
							"'light' should only be defined once");
				}
				if ("colorMid".equals(key)) {
					if (mid == null) {
						mid = Color.decode(value);
						continue;
					}
					throw new IllegalArgumentException(
							"'mid' should only be defined once");
				}
				if ("colorDark".equals(key)) {
					if (dark == null) {
						dark = Color.decode(value);
						continue;
					}
					throw new IllegalArgumentException(
							"'dark' should only be defined once");
				}
				if ("colorUltraDark".equals(key)) {
					if (ultraDark == null) {
						ultraDark = Color.decode(value);
						continue;
					}
					throw new IllegalArgumentException(
							"'ultraDark' should only be defined once");
				}
				if ("colorForeground".equals(key)) {
					if (foreground == null) {
						foreground = Color.decode(value);
						continue;
					}
					throw new IllegalArgumentException(
							"'foreground' should only be defined once");
				}
				throw new IllegalArgumentException(
						"Unsupported format in line " + line);
			}
			if (base != null) {
				// merge with base
				if (ultraLight == null)
					ultraLight = base.getUltraLightColor();
				if (extraLight == null)
					extraLight = base.getExtraLightColor();
				if (light == null)
					light = base.getLightColor();
				if (mid == null)
					mid = base.getMidColor();
				if (dark == null)
					dark = base.getDarkColor();
				if (ultraDark == null)
					ultraDark = base.getUltraDarkColor();
				if (foreground == null)
					foreground = base.getForegroundColor();
			}
			if ((name == null) || (kind == null) || (ultraLight == null)
					|| (extraLight == null) || (light == null) || (mid == null)
					|| (dark == null) || (ultraDark == null)
					|| (foreground == null)) {
				throw new IllegalArgumentException("Incomplete specification");
			}
			Color[] colors = new Color[] { ultraLight, extraLight, light, mid,
					dark, ultraDark, foreground };
			switch (kind) {
			case LIGHT:
				return getLightColorScheme(name, colors);
			default:
				return getDarkColorScheme(name, colors);
			}
		} catch (IOException ioe) {
			throw new IllegalArgumentException(ioe);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException ioe) {
				}
			}
		}
	}

	public static SchemeBaseColors getBaseColorScheme(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		Color ultraLight = null;
		Color extraLight = null;
		Color light = null;
		Color mid = null;
		Color dark = null;
		Color ultraDark = null;
		Color foreground = null;
		try {
			while (true) {
				String line = reader.readLine();
				if (line == null)
					break;
				String[] split = line.split("=");
				if (split.length != 2) {
					throw new IllegalArgumentException(
							"Unsupported format in line " + line);
				}
				String key = split[0];
				String value = split[1];
				if ("colorUltraLight".equals(key)) {
					if (ultraLight == null) {
						ultraLight = Color.decode(value);
						continue;
					}
					throw new IllegalArgumentException(
							"'ultraLight' should only be defined once");
				}
				if ("colorExtraLight".equals(key)) {
					if (extraLight == null) {
						extraLight = Color.decode(value);
						continue;
					}
					throw new IllegalArgumentException(
							"'extraLight' should only be defined once");
				}
				if ("colorLight".equals(key)) {
					if (light == null) {
						light = Color.decode(value);
						continue;
					}
					throw new IllegalArgumentException(
							"'light' should only be defined once");
				}
				if ("colorMid".equals(key)) {
					if (mid == null) {
						mid = Color.decode(value);
						continue;
					}
					throw new IllegalArgumentException(
							"'mid' should only be defined once");
				}
				if ("colorDark".equals(key)) {
					if (dark == null) {
						dark = Color.decode(value);
						continue;
					}
					throw new IllegalArgumentException(
							"'dark' should only be defined once");
				}
				if ("colorUltraDark".equals(key)) {
					if (ultraDark == null) {
						ultraDark = Color.decode(value);
						continue;
					}
					throw new IllegalArgumentException(
							"'ultraDark' should only be defined once");
				}
				if ("colorForeground".equals(key)) {
					if (foreground == null) {
						foreground = Color.decode(value);
						continue;
					}
					throw new IllegalArgumentException(
							"'foreground' should only be defined once");
				}
				throw new IllegalArgumentException(
						"Unsupported format in line " + line);
			}
			final Color[] colors = new Color[] { ultraLight, extraLight, light,
					mid, dark, ultraDark, foreground };
			return new SchemeBaseColors() {
				@Override
				public String getDisplayName() {
					return null;
				}

				@Override
				public Color getUltraLightColor() {
					return colors[0];
				}

				@Override
				public Color getExtraLightColor() {
					return colors[1];
				}

				@Override
				public Color getLightColor() {
					return colors[2];
				}

				@Override
				public Color getMidColor() {
					return colors[3];
				}

				@Override
				public Color getDarkColor() {
					return colors[4];
				}

				@Override
				public Color getUltraDarkColor() {
					return colors[5];
				}

				@Override
				public Color getForegroundColor() {
					return colors[6];
				}
			};
		} catch (IOException ioe) {
			throw new IllegalArgumentException(ioe);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException ioe) {
				}
			}
		}
	}

	public static SubstanceColorScheme getLightColorScheme(String name,
			final Color[] colors) {
		if (colors == null)
			throw new IllegalArgumentException("Color encoding cannot be null");
		if (colors.length != 7)
			throw new IllegalArgumentException(
					"Color encoding must have 7 components");
		return new BaseLightColorScheme(name) {
			public Color getUltraLightColor() {
				return colors[0];
			}

			public Color getExtraLightColor() {
				return colors[1];
			}

			public Color getLightColor() {
				return colors[2];
			}

			public Color getMidColor() {
				return colors[3];
			}

			public Color getDarkColor() {
				return colors[4];
			}

			public Color getUltraDarkColor() {
				return colors[5];
			}

			public Color getForegroundColor() {
				return colors[6];
			}
		};
	}

	public static SubstanceColorScheme getDarkColorScheme(String name,
			final Color[] colors) {
		if (colors == null)
			throw new IllegalArgumentException("Color encoding cannot be null");
		if (colors.length != 7)
			throw new IllegalArgumentException(
					"Color encoding must have 7 components");
		return new BaseDarkColorScheme(name) {
			public Color getUltraLightColor() {
				return colors[0];
			}

			public Color getExtraLightColor() {
				return colors[1];
			}

			public Color getLightColor() {
				return colors[2];
			}

			public Color getMidColor() {
				return colors[3];
			}

			public Color getDarkColor() {
				return colors[4];
			}

			public Color getUltraDarkColor() {
				return colors[5];
			}

			public Color getForegroundColor() {
				return colors[6];
			}
		};
	}

	public static SubstanceSkin.ColorSchemes getColorSchemes(URL url) {
		List<SubstanceColorScheme> schemes = new ArrayList<SubstanceColorScheme>();

		BufferedReader reader = null;
		Color ultraLight = null;
		Color extraLight = null;
		Color light = null;
		Color mid = null;
		Color dark = null;
		Color ultraDark = null;
		Color foreground = null;
		String name = null;
		ColorSchemeKind kind = null;
		boolean inColorSchemeBlock = false;
		try {
			reader = new BufferedReader(new InputStreamReader(url.openStream()));
			while (true) {
				String line = reader.readLine();
				if (line == null)
					break;

				if (line.trim().length() == 0)
					continue;

				if (line.indexOf("{") >= 0) {
					if (inColorSchemeBlock) {
						throw new IllegalArgumentException(
								"Already in color scheme definition");
					}
					inColorSchemeBlock = true;
					name = line.substring(0, line.indexOf("{")).trim();
					continue;
				}

				if (line.indexOf("}") >= 0) {
					if (!inColorSchemeBlock) {
						throw new IllegalArgumentException(
								"Not in color scheme definition");
					}
					inColorSchemeBlock = false;

					if ((name == null) || (kind == null)
							|| (ultraLight == null) || (extraLight == null)
							|| (light == null) || (mid == null)
							|| (dark == null) || (ultraDark == null)
							|| (foreground == null)) {
						throw new IllegalArgumentException(
								"Incomplete specification");
					}
					Color[] colors = new Color[] { ultraLight, extraLight,
							light, mid, dark, ultraDark, foreground };
					if (kind == ColorSchemeKind.LIGHT) {
						schemes.add(getLightColorScheme(name, colors));
					} else {
						schemes.add(getDarkColorScheme(name, colors));
					}
					name = null;
					kind = null;
					ultraLight = null;
					extraLight = null;
					light = null;
					mid = null;
					dark = null;
					ultraDark = null;
					foreground = null;
					continue;
				}

				String[] split = line.split("=");
				if (split.length != 2) {
					throw new IllegalArgumentException(
							"Unsupported format in line " + line);
				}
				String key = split[0].trim();
				String value = split[1].trim();
				if ("kind".equals(key)) {
					if (kind == null) {
						if ("Light".equals(value)) {
							kind = ColorSchemeKind.LIGHT;
							continue;
						}
						if ("Dark".equals(value)) {
							kind = ColorSchemeKind.DARK;
							continue;
						}
						throw new IllegalArgumentException(
								"Unsupported format in line " + line);
					}
					throw new IllegalArgumentException(
							"'kind' should only be defined once");
				}
				if ("colorUltraLight".equals(key)) {
					if (ultraLight == null) {
						ultraLight = Color.decode(value);
						continue;
					}
					throw new IllegalArgumentException(
							"'ultraLight' should only be defined once");
				}
				if ("colorExtraLight".equals(key)) {
					if (extraLight == null) {
						extraLight = Color.decode(value);
						continue;
					}
					throw new IllegalArgumentException(
							"'extraLight' should only be defined once");
				}
				if ("colorLight".equals(key)) {
					if (light == null) {
						light = Color.decode(value);
						continue;
					}
					throw new IllegalArgumentException(
							"'light' should only be defined once");
				}
				if ("colorMid".equals(key)) {
					if (mid == null) {
						mid = Color.decode(value);
						continue;
					}
					throw new IllegalArgumentException(
							"'mid' should only be defined once");
				}
				if ("colorDark".equals(key)) {
					if (dark == null) {
						dark = Color.decode(value);
						continue;
					}
					throw new IllegalArgumentException(
							"'dark' should only be defined once");
				}
				if ("colorUltraDark".equals(key)) {
					if (ultraDark == null) {
						ultraDark = Color.decode(value);
						continue;
					}
					throw new IllegalArgumentException(
							"'ultraDark' should only be defined once");
				}
				if ("colorForeground".equals(key)) {
					if (foreground == null) {
						foreground = Color.decode(value);
						continue;
					}
					throw new IllegalArgumentException(
							"'foreground' should only be defined once");
				}
				throw new IllegalArgumentException(
						"Unsupported format in line " + line);
			}
			;
		} catch (IOException ioe) {
			throw new IllegalArgumentException(ioe);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException ioe) {
				}
			}
		}
		return new SubstanceSkin.ColorSchemes(schemes);
	}

}
