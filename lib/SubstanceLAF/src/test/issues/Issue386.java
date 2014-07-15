package test.issues;

//package test.issues;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.datatransfer.*;
import java.io.IOException;

import javax.swing.*;

import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.skin.SubstanceCremeCoffeeLookAndFeel;

public class Issue386 extends JFrame {

	public Issue386() {
		super();
		ColorTransferHandler colorHandler = new ColorTransferHandler();
		JPanel previewColorPanel = new JPanel();
		// Kirill - the next line fixes the visual discrepancy between the
		// selected color and the painting of the panel.
		previewColorPanel.putClientProperty(
				SubstanceLookAndFeel.COLORIZATION_FACTOR, Double.valueOf(1.0));
		previewColorPanel.add(new JLabel("Preview Color"));
		previewColorPanel.setOpaque(true);
		previewColorPanel.setTransferHandler(colorHandler);
		JColorChooser chooser = new JColorChooser();
		chooser.setDragEnabled(true);
		this.add(chooser, BorderLayout.NORTH);
		this.add(previewColorPanel, BorderLayout.SOUTH);
		this.pack();
		this.setVisible(true);
	}

	public static void main(String[] args) throws Exception {
		UIManager.setLookAndFeel(new SubstanceCremeCoffeeLookAndFeel());
		JFrame.setDefaultLookAndFeelDecorated(true);
		System.setProperty("sun.awt.noerasebackground", "true");
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new Issue386();
			}
		});
	}

	class ColorTransferHandler extends TransferHandler {
		String mimetype = DataFlavor.javaJVMLocalObjectMimeType
				+ ";class=java.awt.Color";
		DataFlavor colorFlavor;

		ColorTransferHandler() {
			try {
				colorFlavor = new DataFlavor(mimetype);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}

		@Override
		public boolean importData(JComponent c, Transferable t) {
			if (hasColorFlavor(t.getTransferDataFlavors())) {
				try {
					Color col = (Color) t.getTransferData(colorFlavor);
					System.out.println("Setting " + col);
					c.setBackground(col);
					return true;
				} catch (UnsupportedFlavorException ufe) {
					ufe.printStackTrace();
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
			return false;
		}

		protected boolean hasColorFlavor(DataFlavor[] flavors) {
			if (colorFlavor == null) {
				return false;
			}
			for (int i = 0; i < flavors.length; i++) {
				if (colorFlavor.equals(flavors[i])) {
					return true;
				}
			}
			return false;
		}

		@Override
		public boolean canImport(JComponent c, DataFlavor[] flavors) {
			return hasColorFlavor(flavors);
		}
	}

}
