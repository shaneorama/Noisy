/*
 * Created by JFormDesigner on Mon Sep 21 23:17:18 GMT+02:00 2009
 */

package test.contrib.setskin;

import java.awt.Container;
import java.awt.Window;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.BevelBorder;

import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.skin.MistSilverSkin;
import org.jvnet.substance.skin.SubstanceMistSilverLookAndFeel;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.*;

/**
 * @author Hesham G.
 */
/**
 * @author Hesham G.
 */
public class MainClass extends JFrame {
	private int origionalPanelHeight = 0;

	public MainClass() {
		initComponents();
		origionalPanelHeight = panel1.getHeight();
	}

	private void button1MouseReleased() {
		new TimerThread(panel1, origionalPanelHeight);
	}

	private void thisWindowClosing() {
		System.exit(0);
	}

	private void button2MouseReleased() {
		SubstanceLookAndFeel.setSkin(new MistSilverSkin());
	}

	private void button3MouseReleased() {
		try {
			UIManager.setLookAndFeel(new SubstanceMistSilverLookAndFeel());
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		for (Window w : Window.getWindows()) {
			SwingUtilities.updateComponentTreeUI(w);
		}
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY
		// //GEN-BEGIN:initComponents
		// Generated using JFormDesigner non-commercial license
		button1 = new JButton();
		panel1 = new JPanel();
		label1 = new JLabel();
		label2 = new JLabel();
		button2 = new JButton();
		button3 = new JButton();
		CellConstraints cc = new CellConstraints();

		// ======== this ========
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				thisWindowClosing();
			}
		});
		Container contentPane = getContentPane();
		contentPane.setLayout(new FormLayout(new ColumnSpec[] {
				new ColumnSpec(Sizes.dluX(13)),
				FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
				new ColumnSpec(Sizes.dluX(13)),
				FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
				new ColumnSpec(Sizes.dluX(13)),
				FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
				new ColumnSpec(Sizes.dluX(13)),
				FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
				new ColumnSpec(Sizes.dluX(13)),
				FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
				new ColumnSpec(Sizes.dluX(13)),
				FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
				new ColumnSpec(Sizes.dluX(13)),
				FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
				new ColumnSpec(Sizes.dluX(13)),
				FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
				new ColumnSpec(Sizes.dluX(13)),
				FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
				new ColumnSpec(Sizes.dluX(13)),
				FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
				new ColumnSpec(Sizes.dluX(13)),
				FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
				new ColumnSpec(Sizes.dluX(13)),
				FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
				new ColumnSpec(Sizes.dluX(13)),
				FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
				new ColumnSpec(Sizes.dluX(13)) }, new RowSpec[] {
				new RowSpec(RowSpec.FILL, Sizes.DLUY7, FormSpec.NO_GROW),
				FormFactory.LINE_GAP_ROWSPEC,
				new RowSpec(RowSpec.FILL, Sizes.dluY(13), FormSpec.NO_GROW),
				FormFactory.LINE_GAP_ROWSPEC,
				new RowSpec(RowSpec.FILL, Sizes.DLUY7, FormSpec.NO_GROW),
				FormFactory.LINE_GAP_ROWSPEC,
				new RowSpec(RowSpec.FILL, Sizes.dluY(13), FormSpec.NO_GROW),
				FormFactory.LINE_GAP_ROWSPEC,
				new RowSpec(RowSpec.FILL, Sizes.dluY(13), FormSpec.NO_GROW),
				FormFactory.LINE_GAP_ROWSPEC,
				new RowSpec(RowSpec.FILL, Sizes.dluY(13), FormSpec.NO_GROW),
				FormFactory.LINE_GAP_ROWSPEC,
				new RowSpec(RowSpec.FILL, Sizes.dluY(13), FormSpec.NO_GROW) }));

		// ---- button1 ----
		button1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				button1MouseReleased();
			}
		});
		contentPane.add(button1, cc.xywh(5, 3, 7, 1));

		// ======== panel1 ========
		{
			panel1.setBorder(new BevelBorder(BevelBorder.LOWERED));
			panel1.setLayout(new FormLayout(new ColumnSpec[] {
					new ColumnSpec(Sizes.dluX(13)),
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
					new ColumnSpec(Sizes.dluX(13)),
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
					new ColumnSpec(Sizes.dluX(13)),
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
					new ColumnSpec(Sizes.dluX(13)),
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
					new ColumnSpec(Sizes.dluX(13)),
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
					new ColumnSpec(Sizes.dluX(13)) },
					new RowSpec[] {
							new RowSpec(RowSpec.FILL, Sizes.dluY(13),
									FormSpec.NO_GROW),
							FormFactory.LINE_GAP_ROWSPEC,
							new RowSpec(RowSpec.FILL, Sizes.dluY(13),
									FormSpec.NO_GROW),
							FormFactory.LINE_GAP_ROWSPEC,
							new RowSpec(RowSpec.FILL, Sizes.dluY(13),
									FormSpec.NO_GROW) }));
			panel1.add(label1, cc.xywh(1, 1, 11, 1));
			panel1.add(label2, cc.xywh(1, 3, 11, 1));
		}
		contentPane.add(panel1, cc.xywh(3, 7, 13, 5));

		// ---- button2 ----
		button2.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				button2MouseReleased();
			}
		});
		contentPane.add(button2, cc.xywh(17, 9, 9, 1));

		// ---- button3 ----
		button3.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				button3MouseReleased();
			}
		});
		contentPane.add(button3, cc.xywh(17, 11, 9, 1));
		pack();
		setLocationRelativeTo(getOwner());

		initComponentsI18n();
		// JFormDesigner - End of component initialization
		// //GEN-END:initComponents
	}

	private void initComponentsI18n() {
		// JFormDesigner - Component i18n initialization - DO NOT MODIFY
		// //GEN-BEGIN:initI18n
		// Generated using JFormDesigner non-commercial license
		button1.setText("Show / Hide");
		label1.setText("Click on the button above");
		label2.setText("to show / hide this box");
		button2.setText("setSkin theme");
		button3.setText("UIManager theme");
		// JFormDesigner - End of component i18n initialization
		// //GEN-END:initI18n
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY
	// //GEN-BEGIN:variables
	// Generated using JFormDesigner non-commercial license
	private JButton button1;
	private JPanel panel1;
	private JLabel label1;
	private JLabel label2;
	private JButton button2;
	private JButton button3;
	// JFormDesigner - End of variables declaration //GEN-END:variables
}

class TimerThread extends Thread {
	private JPanel panelToCollapse;
	private int origionalPanelHeight;

	public TimerThread(JPanel panelToCollapse, int origionalPanelHeight) {
		this.panelToCollapse = panelToCollapse;
		this.origionalPanelHeight = origionalPanelHeight;
		start();
	}

	@Override
	public void run() {
		if (panelToCollapse.getHeight() <= 0) {
			while (panelToCollapse.getHeight() < origionalPanelHeight) {
				synchronized (panelToCollapse) {

					panelToCollapse.setSize(panelToCollapse.getWidth(),
							panelToCollapse.getHeight() + 1);
					panelToCollapse.repaint();
					panelToCollapse.getParent().repaint();

					try {
						Thread.sleep(1); // Run this thread every (5) sec. to
						// reduce memory reserved.
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		} else {
			while (panelToCollapse.getHeight() > 0) {
				synchronized (panelToCollapse) {

					panelToCollapse.setSize(panelToCollapse.getWidth(),
							panelToCollapse.getHeight() - 1);
					panelToCollapse.repaint();
					panelToCollapse.getParent().repaint();

					try {
						Thread.sleep(1); // Run this thread every (5) sec. to
						// reduce memory reserved.
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
