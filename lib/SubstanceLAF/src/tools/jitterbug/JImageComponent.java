/**
 * 
 */
package tools.jitterbug;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.*;

import org.jvnet.lafwidget.utils.RenderingUtils;
import org.jvnet.substance.utils.border.SubstanceBorder;

public class JImageComponent extends JPanel {
	private BufferedImage image;

	private double leftX;

	private double topY;

	private double zoom;

	private boolean isDragging;

	private Point lastDragPoint;

	private Color selectedColor;

	private Color rolloverColor;

	public JImageComponent() {
		this.setTransferHandler(new TransferHandler() {
			@Override
			public boolean canImport(TransferSupport support) {
				// can import a list of files
				if (support
						.isDataFlavorSupported(DataFlavor.javaFileListFlavor))
					return true;
				// an image
				if (support.isDataFlavorSupported(DataFlavor.imageFlavor))
					return true;
				for (DataFlavor df : support.getDataFlavors()) {
					// and a flavor represented by URL
					if (df.getRepresentationClass() == URL.class)
						return true;
				}
				return false;
			}

			@Override
			public boolean importData(TransferSupport support) {
				Transferable t = support.getTransferable();

				try {
					if (t.isDataFlavorSupported(DataFlavor.imageFlavor)) {
						// load the image
						Image data = (Image) t
								.getTransferData(DataFlavor.imageFlavor);
						image = new BufferedImage(data.getWidth(null), data
								.getHeight(null), BufferedImage.TYPE_INT_ARGB);
						image.getGraphics().drawImage(data, 0, 0, null);
						reset();
						repaint();
						return true;
					}

					if (t.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
						// load the image from the file
						java.util.List<File> l = (java.util.List<File>) t
								.getTransferData(DataFlavor.javaFileListFlavor);

						if (l.size() == 1) {
							image = ImageIO.read(l.get(0));
							reset();
							repaint();
						}
						return true;
					}

					for (DataFlavor df : support.getDataFlavors()) {
						if (df.getRepresentationClass() == URL.class) {
							// load the image from the URL
							URL url = (URL) t.getTransferData(df);
							Image data = ImageIO.read(url);
							if (data != null) {
								image = new BufferedImage(data.getWidth(null),
										data.getHeight(null),
										BufferedImage.TYPE_INT_ARGB);
								image.getGraphics().drawImage(data, 0, 0, null);
								reset();
								repaint();
								return true;
							}
						}
					}
					return true;
				} catch (Throwable thr) {
					thr.printStackTrace();
					return false;
				}
			}
		});

		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (image == null)
					return;

				if (!e.isPopupTrigger()) {
					lastDragPoint = e.getPoint();
				} else {
					processPopup(e);
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					processPopup(e);
				} else {
					if (image == null)
						return;

					if (!isDragging) {
						int xRel = e.getX();
						int yRel = e.getY();
						int xAbs = (int) ((xRel / zoom) + leftX);
						int yAbs = (int) ((yRel / zoom) + topY);

						if ((xAbs >= 0) && (xAbs < image.getWidth())
								&& (yAbs >= 0) && (yAbs < image.getHeight())) {
							selectedColor = new Color(image.getRGB(xAbs, yAbs));
							firePropertyChange("selectedColor", null,
									selectedColor);
						}
					}
					isDragging = false;
				}
			}

			private void processPopup(MouseEvent e) {
				JPopupMenu editMenu = new JPopupMenu();
				editMenu.add(new AbstractAction("paste from clipboard") {
					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							Clipboard clipboard = Toolkit.getDefaultToolkit()
									.getSystemClipboard();
							DataFlavor[] flavors = clipboard
									.getAvailableDataFlavors();
							if (flavors != null) {
								for (DataFlavor flavor : flavors) {
									if (Image.class == flavor
											.getRepresentationClass()) {
										Image data = (Image) clipboard
												.getData(flavor);
										image = new BufferedImage(data
												.getWidth(null), data
												.getHeight(null),
												BufferedImage.TYPE_INT_ARGB);
										image.getGraphics().drawImage(data, 0,
												0, null);
										reset();
										break;
									}
								}
							}
							repaint();
						} catch (Throwable thr) {
						}
					}

					@Override
					public boolean isEnabled() {
						Clipboard clipboard = Toolkit.getDefaultToolkit()
								.getSystemClipboard();
						DataFlavor[] flavors = clipboard
								.getAvailableDataFlavors();
						if (flavors != null) {
							for (DataFlavor flavor : flavors) {
								if (Image.class == flavor
										.getRepresentationClass()) {
									return true;
								}
							}
						}
						return false;
					}
				});
				Point pt = SwingUtilities.convertPoint(e.getComponent(), e
						.getPoint(), JImageComponent.this);
				editMenu.show(JImageComponent.this, pt.x, pt.y);
			}

		});

		this.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				if (image == null)
					return;

				isDragging = true;

				Point currDragPoint = e.getPoint();
				double dx = ((currDragPoint.x - lastDragPoint.x) / zoom);
				double dy = ((currDragPoint.y - lastDragPoint.y) / zoom);
				leftX -= dx;
				topY -= dy;

				lastDragPoint = currDragPoint;
				repaint();
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				if (image == null)
					return;

				int xRel = e.getX();
				int yRel = e.getY();
				int xAbs = (int) ((xRel / zoom) + leftX);
				int yAbs = (int) ((yRel / zoom) + topY);

				// System.out.println(xRel + ":" + yRel + "->" + xAbs + ":"
				// + yAbs);

				if ((xAbs >= 0) && (xAbs < image.getWidth()) && (yAbs >= 0)
						&& (yAbs < image.getHeight())) {
					Color old = rolloverColor;
					rolloverColor = new Color(image.getRGB(xAbs, yAbs));
					firePropertyChange("rolloverColor", old, rolloverColor);
				}
			}
		});

		this.addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				zoom += e.getScrollAmount() * e.getWheelRotation() / 10.0;
				zoom = Math.max(1.0, zoom);
				repaint();
			}
		});

		Action zoomInAction = new AbstractAction("zoomin") {
			@Override
			public void actionPerformed(ActionEvent e) {
				zoom += 0.1;
				repaint();
			}
		};
		Action zoomOutAction = new AbstractAction("zoomout") {
			@Override
			public void actionPerformed(ActionEvent e) {
				zoom -= 0.1;
				zoom = Math.max(1.0, zoom);
				repaint();
			}
		};

		// create the key input maps to handle the zooming
		// with I and O
		InputMap inputMap = new ComponentInputMap(this);
		inputMap.put(KeyStroke.getKeyStroke("I"), "zoomin");
		inputMap.put(KeyStroke.getKeyStroke("O"), "zoomout");

		ActionMap actionMap = new ActionMap();
		actionMap.put("zoomin", zoomInAction);
		actionMap.put("zoomout", zoomOutAction);

		// and register the maps
		this.setInputMap(WHEN_IN_FOCUSED_WINDOW, inputMap);
		this.setActionMap(actionMap);

		this.setBorder(new SubstanceBorder());

		this.zoom = 1.0;
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setColor(Color.white);
		g2d.fillRect(0, 0, getWidth(), getHeight());

		if (this.image != null) {
			// zoom from the visible top-left pixel
			g2d.scale(zoom, zoom);
			g2d.translate(-this.leftX, -this.topY);
			g2d.drawImage(this.image, 0, 0, null);
		} else {
			RenderingUtils.installDesktopHints(g2d, this);
			g2d.setFont(UIManager.getFont("Label.font"));
			g2d.setColor(Color.black);

			int fh = g2d.getFontMetrics().getHeight();
			g2d.drawString(
					"Image panel. Use one of the following to show an image:",
					10, 10 + fh);
			g2d.drawString(
					"* Right-click to paste an image from the clipboard", 20,
					10 + 2 * fh);
			g2d
					.drawString(
							"* Drag and drop an image file from local disk or another app",
							20, 10 + 3 * fh);
			g2d.drawString("* Drag and drop a URL pointing to an image", 20,
					10 + 4 * fh);
		}

		g2d.dispose();
	}

	private void reset() {
		leftX = 0;
		topY = 0;
		zoom = 1.0;
	}
}