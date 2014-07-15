package test.issues;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import org.jvnet.lafwidget.LafWidget;
import org.jvnet.lafwidget.preview.DefaultPreviewPainter;
import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.skin.CremeSkin;
import org.jvnet.substance.skin.SubstanceBusinessLookAndFeel;

public class Issue396 extends JFrame {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					SubstanceLookAndFeel feel = new SubstanceBusinessLookAndFeel();
					UIManager.setLookAndFeel(feel);
					SubstanceLookAndFeel.setSkin(new CremeSkin());
				} catch (UnsupportedLookAndFeelException e) {
				}
				Issue396 scrollError = new Issue396();
				JScrollPane scrollPane = new JScrollPane();
				JTable table = new JTable();
				table.setModel(new DefaultTableModel(100, 5));
				scrollPane.setViewportView(table);
				scrollPane.putClientProperty(
						LafWidget.COMPONENT_PREVIEW_PAINTER,
						new DefaultPreviewPainter());
				scrollError.getContentPane().add(scrollPane);
				scrollError.pack();
				scrollError.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				scrollError.setLocationRelativeTo(null);
				scrollError.setVisible(true);
			}
		});
	}
}