package test;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.api.SubstanceSkin;
import org.jvnet.substance.api.SubstanceConstants.ImageWatermarkKind;
import org.jvnet.substance.skin.BusinessBlackSteelSkin;
import org.jvnet.substance.watermark.SubstanceImageWatermark;

import test.check.SampleFrame;

public class ImageWatermark {
	public static void main(String[] args) {
		SubstanceImageWatermark watermark = new SubstanceImageWatermark(
				ImageWatermark.class
						.getResourceAsStream("/test/samples/substance/api/dukeplug.gif"));
		watermark.setKind(ImageWatermarkKind.APP_TILE);
		SubstanceSkin skin = new BusinessBlackSteelSkin().withWatermark(watermark);
		SubstanceLookAndFeel.setSkin(skin);
		JFrame.setDefaultLookAndFeelDecorated(true);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				SampleFrame skinFrame = new SampleFrame(false);
				SwingUtilities.updateComponentTreeUI(skinFrame);
				skinFrame.setSize(315, 245);
				skinFrame.setLocationRelativeTo(null);
				skinFrame.setVisible(true);
				skinFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			}
		});
	}

}
