package tools.jitterbug;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;

import org.jvnet.lafwidget.utils.RenderingUtils;

public class JColorComponent extends JComponent {
	private JRadioButton radio;

	private Color selectedColor;

	private String name;

	private ColorVisualizer visualizer;

	private class ColorVisualizer extends JComponent {
		boolean isRollover;

		public ColorVisualizer() {
			this.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (!isEnabled())
						return;

					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							radio.setSelected(true);
							Color selected = JColorChooser.showDialog(
									ColorVisualizer.this, "Color chooser",
									selectedColor);
							if (selected != null) {
								Color old = selectedColor;
								selectedColor = selected;
								JColorComponent.this.firePropertyChange(
										"selectedColor", old, selectedColor);
							}
						}
					});
				}

				@Override
				public void mouseEntered(MouseEvent e) {
					if (!isEnabled())
						return;

					isRollover = true;
					repaint();
				}

				@Override
				public void mouseExited(MouseEvent e) {
					if (!isEnabled())
						return;

					isRollover = false;
					repaint();
				}
			});
			this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			this.setToolTipText("Open color chooser and change the color");
			this.isRollover = false;
		}

		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2d = (Graphics2D) g.create();
			RenderingUtils.installDesktopHints(g2d, this);
			g2d.setFont(UIManager.getFont("Label.font"));

			if (selectedColor != null) {
				g2d.setColor(selectedColor);
				g2d.fillRect(2, 2, 100, getHeight() - 4);
				g2d.setStroke(new BasicStroke(isRollover ? 2.5f : 1.0f));
				g2d.setColor(selectedColor.darker());
				g2d.drawRect(2, 2, 99, getHeight() - 5);

				g2d.setColor(Color.black);
				g2d.drawString(getEncodedColor(), 108, (getHeight() + g2d
						.getFontMetrics().getHeight())
						/ 2 - g2d.getFontMetrics().getDescent());
			} else {
				g2d.setColor(isEnabled() ? Color.gray : Color.lightGray);
				g2d.drawString("click to choose", 5, (getHeight() + g2d
						.getFontMetrics().getHeight())
						/ 2 - g2d.getFontMetrics().getDescent());
			}

			g2d.dispose();
		}

		@Override
		public Dimension getPreferredSize() {
			return new Dimension(200, 25);
		}
	}

	public JColorComponent(String name, Color color) {
		this.radio = new JRadioButton(name);
		this.radio.setFocusable(false);
		this.selectedColor = color;
		this.visualizer = new ColorVisualizer();
		this.setLayout(new ColorComponentLayout());

		this.add(this.radio);
		this.add(this.visualizer);
	}

	private class ColorComponentLayout implements LayoutManager {
		@Override
		public void addLayoutComponent(String name, Component comp) {
		}

		@Override
		public void layoutContainer(Container parent) {
			JColorComponent cc = (JColorComponent) parent;
			int width = cc.getWidth();
			int height = cc.getHeight();

			ColorVisualizer cv = cc.visualizer;
			Dimension cvPref = cv.getPreferredSize();
			cv.setBounds(width - cvPref.width, 0, cvPref.width, height);
			cc.radio.setBounds(0, 0, width - cvPref.width, height);
		}

		@Override
		public Dimension minimumLayoutSize(Container parent) {
			return preferredLayoutSize(parent);
		}

		@Override
		public Dimension preferredLayoutSize(Container parent) {
			JColorComponent cc = (JColorComponent) parent;
			ColorVisualizer cv = cc.visualizer;
			Dimension cvPref = cv.getPreferredSize();
			return new Dimension(100 + cvPref.width, cvPref.height);
		}

		@Override
		public void removeLayoutComponent(Component comp) {
		}
	}

	public String getEncodedColor() {
		return "#" + encodeColorComponent(selectedColor.getRed())
				+ encodeColorComponent(selectedColor.getGreen())
				+ encodeColorComponent(selectedColor.getBlue());
	}

	private static String encodeColorComponent(int colorComp) {
		String hex = "0123456789ABCDEF";
		return "" + hex.charAt(colorComp / 16) + hex.charAt(colorComp % 16);
	}

	public JRadioButton getRadio() {
		return radio;
	}

	public void setColor(Color color, boolean firePropertyChange) {
		Color old = this.selectedColor;
		this.selectedColor = color;
		this.repaint();
		if (firePropertyChange) {
			this.firePropertyChange("selectedColor", old, selectedColor);
		}
	}

	public Color getColor() {
		return this.selectedColor;
	}

	public boolean isDefined() {
		return (this.selectedColor != null);
	}
}
