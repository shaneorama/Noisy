package test;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import org.jvnet.substance.painter.noise.*;
import org.jvnet.substance.painter.noise.FabricFilter.FabricFilterLink;
import org.jvnet.substance.painter.noise.NoiseFilter.TrigKind;
import org.jvnet.substance.skin.BusinessBlackSteelSkin;

public class NoiseFrame extends JFrame {

	public NoiseFrame() {
	}

	@Override
	public void paint(Graphics g) {
		int width = this.getWidth();
		int height = this.getHeight();

		// // double[][] noise = new
		// // PerlinNoiseGenerator().getSparseNormalizedNoise(
		// // width, height, 0);
		// ColorScheme cs = new BrownColorScheme();
		// Color c1 = SubstanceCoreUtilities.getInterpolatedColor(
		// cs.getMidColor(), cs.getLightColor(), 0.9);
		// Color c2 = cs.getLightColor();
		// Color c3 = cs.getUltraLightColor();
		//
		// BufferedImage bi = new BufferedImage(width, height,
		// BufferedImage.TYPE_INT_ARGB);
		// Graphics2D g2d = (Graphics2D) bi.getGraphics();
		//
		// for (int i = 0; i < width; i++) {
		// double ii = (double) (i - width / 2) / 10.0;
		// for (int j = 0; j < height; j++) {
		// double jj = (double) (j - height / 2) / 10.0;
		// double zz2 = 1.0;// 100 - ii * ii - 4 * jj * jj;
		// if (zz2 > 0.0) {
		// double noise = 0.5 + 0.5 * PerlinNoiseGenerator.noise(ii,
		// jj, Math.sqrt(zz2));
		// // noise = 0.5 + 0.5 * Math.cos(i + 20*noise);
		// // noise = 0.5 + 0.5*Math.sin(i + j + 20*noise);
		// // noise = (0.5 + 0.5 * Math.sin(i + 20 * noise)
		// // * Math.cos(-i + j + 20 * noise)); // fabric
		// // noise = 0.5 + 0.5 * Math.cos(i / 10. + noise); // marble
		// // noise = Math.sqrt(Math.abs(2*noise-1)); // maze
		//
		// double noise2 = 0.5 + 0.5 * PerlinNoiseGenerator.noise(
		// i / 100.0, j / 100.0, zz2);
		// // noise = 20 * noise - (int) (20 * noise); // wood
		//
		// // double bumps = PerlinNoiseGenerator.PerlinNoise_2D(ii,
		// // jj / 10.0);
		// // if (bumps < .5)
		// // bumps = 0;
		// // else
		// // bumps = 1;
		// // noise += (1-noise)*bumps*0.5;
		//
		// FabricFilter.FabricFilterLink fabricLink1 = FabricFilterLink
		// .getXLink(1.0, 20.0, TrigKind.SINE);
		// FabricFilter.FabricFilterLink fabricLink2 = FabricFilterLink
		// .getYLink(1.0, 20.0, TrigKind.COSINE);
		// NoiseFilter fabricFilter = new FabricFilter(fabricLink1,
		// fabricLink2);
		// NoiseFilter marbleFilter = MarbleFilter.getXFilter(0.1,
		// TrigKind.COSINE);
		// NoiseFilter mazeFilter = new MazeFilter();
		// NoiseFilter woodFilter = new WoodFilter(30.0);
		//
		// CompoundNoiseFilter compoundFilter = new CompoundNoiseFilter(
		// woodFilter);
		// noise = compoundFilter.apply(i, j, zz2, noise2);
		//
		// if (noise < 0.5)
		// g2d.setColor(SubstanceCoreUtilities
		// .getInterpolatedColor(c2, c1, 2.0 * noise));
		// else
		// g2d.setColor(SubstanceCoreUtilities
		// .getInterpolatedColor(c3, c2,
		// 2.0 * (noise - .5)));
		// g2d.fillRect(i, j, 1, 1);
		// } else {
		// g2d.setColor(Color.black);
		// g2d.fillRect(i, j, 1, 1);
		// }
		// }
		// }
		// ConvolveOp convolve = new ConvolveOp(new Kernel(3, 3, new float[] {
		// .08f, .08f, .08f, .08f, .38f, .08f, .08f, .08f, .08f }),
		// ConvolveOp.EDGE_NO_OP, null);
		// bi = convolve.filter(bi, null);

		FabricFilter.FabricFilterLink fabricLink1 = FabricFilterLink.getXLink(
				1.0, 10.0, TrigKind.SINE);
		FabricFilter.FabricFilterLink fabricLink2 = FabricFilterLink.getYLink(
				1.0, 10.0, TrigKind.COSINE);
		NoiseFilter fabricFilter = new FabricFilter(fabricLink1, fabricLink2);
		NoiseFilter marbleFilter = MarbleFilter
				.getXFilter(0.1, TrigKind.COSINE);
		NoiseFilter mazeFilter = new MedianBeakFilter();
		NoiseFilter woodFilter = new WoodFilter(30.0);

		// CompoundNoiseFilter compoundFilter = new CompoundNoiseFilter(
		// fabricFilter);
		// long start = System.currentTimeMillis();
		// BufferedImage bi = NoiseFactory.getNoiseImage(
		// new SubstanceCharcoalTheme(), new SubstanceCharcoalTheme(),
		// width, height, 0.01, 0.01, true, compoundFilter, true, false);
		// long end = System.currentTimeMillis();
		// System.out.println("Time0 " + (end - start));
		//
		// CompoundNoiseFilter compoundFilter2 = new CompoundNoiseFilter(
		// fabricFilter);
		long start = System.currentTimeMillis();
		BufferedImage bi2 = NoiseFactory.getNoiseImage(
				new BusinessBlackSteelSkin(), 400, 400, 0.8, 0.8, false, null,
				true, true);
		// bi2 = LafWidgetUtilities.createThumbnail(bi2, 200);
		long end = System.currentTimeMillis();
		System.out.println("Time1 " + (end - start));

		// g.setColor(new SubstanceCharcoalTheme().getDefaultTheme()
		// .getColorScheme().getDarkColor().brighter());
		// g.fillRect(0, 0, 1000, 1000);
		g.drawImage(bi2, 0, 0, null);
	}

	public static void main(String[] args) {
		NoiseFrame nf = new NoiseFrame();
		nf.setSize(400, 400);
		nf.setLocationRelativeTo(null);
		nf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		nf.setVisible(true);
	}

}
