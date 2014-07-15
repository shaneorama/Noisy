package test.contrib;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.api.ComponentState;
import org.jvnet.substance.api.SubstanceColorScheme;
import org.jvnet.substance.painter.gradient.SubduedGradientPainter;
import org.jvnet.substance.painter.gradient.SubstanceGradientPainter;
import org.jvnet.substance.skin.MagmaSkin;

public class GradientPaintTestPanel extends javax.swing.JPanel
{
	public static void main(String[] args)
	{
		SubstanceLookAndFeel.setSkin(new MagmaSkin());
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				JFrame frame = new JFrame();
				frame.setSize(450,250);
				frame.getContentPane().add(new GradientPaintTestPanel());
				frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
				frame.setVisible(true);
			}
		});
	}
	public GradientPaintTestPanel()
	{
		super();
	}
	@Override
	protected void paintComponent(Graphics g)
	{		
		Shape leftShape=new Ellipse2D.Float(10,10,200,200);
		Shape RightShape=new Ellipse2D.Float(220,10,200,200);
		
		Graphics2D g2 = (Graphics2D) g.create();
			
		SubstanceGradientPainter painter = new SubduedGradientPainter();
		SubstanceColorScheme activeColor = SubstanceLookAndFeel.getCurrentSkin().getColorScheme(this, ComponentState.ACTIVE);
		SubstanceColorScheme defaultColor = SubstanceLookAndFeel.getCurrentSkin().getColorScheme(this, ComponentState.DEFAULT);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		//background
		painter.paintContourBackground(g2, this, getWidth(), getHeight(), getBounds(), false, defaultColor, activeColor, 0, true, true);
		//left shape
		painter.paintContourBackground(g2, this, getWidth(), getHeight(), leftShape, false, activeColor, defaultColor, 0, true, true);
		//right shape
		painter = new MyGradientPainter();
		painter.paintContourBackground(g2, this, getWidth(), getHeight(), RightShape, false, activeColor, defaultColor, 0, true, true);
		g2.dispose();
	}
}