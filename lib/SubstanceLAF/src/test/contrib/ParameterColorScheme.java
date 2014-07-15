package test.contrib;

import java.awt.Color;

import org.jvnet.substance.api.SubstanceColorScheme;
import org.jvnet.substance.colorscheme.*;

public class ParameterColorScheme implements SubstanceColorScheme {
	protected Color[] colors;

	public ParameterColorScheme(Color[] colors) {
		super();
		this.colors = colors;
	}

	public SubstanceColorScheme hueShift(double arg0) {
		return new HueShiftColorScheme(this, arg0);
	}

	public SubstanceColorScheme invert() {
		return new InvertedColorScheme(this);
	}

	public boolean isDark() {
		return true;
	}

	public SubstanceColorScheme negate() {
		return new NegatedColorScheme(this);
	}

	// public SubstanceColorScheme protanopia() {
	// return new ProtanopiaColorScheme(this);
	// }

	public SubstanceColorScheme saturate(double arg0) {
		return new SaturatedColorScheme(this, arg0);
	}

	public SubstanceColorScheme shade(double arg0) {
		return new ShadeColorScheme(this, arg0);
	}

	public SubstanceColorScheme shift(Color arg0, double arg1, Color arg2,
			double arg3) {
		return new ShiftColorScheme(this, arg0, arg1, arg2, arg3, false);
	}

	public SubstanceColorScheme shiftBackground(Color arg0, double arg1) {
		return new ShiftColorScheme(this, arg0, arg1);
	}

	public SubstanceColorScheme tint(double arg0) {
		return new TintColorScheme(this, 0);
		// 0.2
	}

	public SubstanceColorScheme tone(double arg0) {
		return new ToneColorScheme(this, 0);
		// 0.35
	}

	// public SubstanceColorScheme tritanopia() {
	// return new TritanopiaColorScheme(this);
	// }

	public String getDisplayName() {
		return "ORDefaultSkin";
	}

	public Color getDarkColor() {
		return colors[5];
	}

	public Color getExtraLightColor() {
		return colors[2];
	}

	public Color getForegroundColor() {
		return colors[0];
	}

	public Color getLightColor() {
		return colors[1];
		// return colors[3];
	}

	public Color getMidColor() {
		return colors[4];
	}

	public Color getUltraDarkColor() {
		return colors[5];
		// return colors[6];
	}

	public Color getUltraLightColor() {
		return colors[2];
		// return colors[1];
	}

	public Color getBackgroundFillColor() {
		return colors[7];
	}

	public Color getFocusRingColor() {
		return colors[7];
	}

	public Color getTextBackgroundFillColor() {
		return colors[2];
	}

	public Color getLineColor() {
		return colors[5];
	}

	public Color getSelectionBackgroundColor() {
		return colors[8];
	}

	public Color getSelectionForegroundColor() {
		return colors[0];
	}

	public Color getWatermarkDarkColor() {
		return colors[5];
	}

	public Color getWatermarkLightColor() {
		return colors[2];
	}

	public Color getWatermarkStampColor() {
		return colors[3];
	}

	@Override
	public SubstanceColorScheme named(String colorSchemeDisplayName) {
		return this;
	}
}
