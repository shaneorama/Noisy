package test.contrib;

import java.awt.BorderLayout;
import java.text.SimpleDateFormat;

import javax.swing.*;

import org.jvnet.substance.skin.SubstanceAutumnLookAndFeel;

/**
 * @author quangvu.cao
 * 
 */
public class PerformanceSubstanceLnF extends JFrame {
	private String rows[][];

	/**
	 * @param args
	 */
	public void initialize(int r, int c) {
		SimpleDateFormat sdf = new SimpleDateFormat("mm:ss.SSS");
		// System.out.println(sdf.format(new Date()) + " - begin");
		rows = new String[r][c];
		for (int row = 0; row < r; row++) {
			for (int col = 0; col < c; col++) {
				rows[row][col] = "Row: " + row + " - col:" + col;
			}
		}
		// System.out.println(sdf.format(new Date()) + " - row creation");
		String[] colHeaders = new String[c];
		for (int col = 0; col < c; col++) {
			colHeaders[col] = "Header " + col;
		}
		// System.out.println(sdf.format(new Date()) + " - header creation");
		JTable table = new JTable(rows, colHeaders);
		// System.out.println(sdf.format(new Date()) + " - table creation");
		JScrollPane pane = new JScrollPane(table);
		// System.out.println(sdf.format(new Date()) +
		// " - scroll pane creation");
		this.getContentPane().add(pane, BorderLayout.CENTER);
		// System.out.println(sdf.format(new Date()) + " - add");
		this.getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);
		// System.out.println(sdf.format(new Date()) + " - decoration style");
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		// System.out.println(sdf.format(new Date()) + " - extended ste");
		this.setVisible(true);
		// System.out.println(sdf.format(new Date()) + " - visible");
		this.pack();
		// System.out.println(sdf.format(new Date()) + " - pack");
	}

	public static void main(String[] args) {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				PerformanceSubstanceLnF app = new PerformanceSubstanceLnF();
				System.out.println("START");
				try {
					UIManager.setLookAndFeel(new SubstanceAutumnLookAndFeel());
				} catch (UnsupportedLookAndFeelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				long startTime = System.currentTimeMillis();
				app.initialize(1000, 60);
				app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				long endTime = System.currentTimeMillis();
				System.out.println("END");
				System.out.println("Time: " + (endTime - startTime) + "ms");
			}
		});
	}
}
