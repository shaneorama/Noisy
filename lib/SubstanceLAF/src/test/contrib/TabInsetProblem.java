package test.contrib;

import java.awt.*;

import javax.swing.*;

import org.jvnet.lafwidget.LafWidget;
import org.jvnet.lafwidget.LafWidgetRepository;
import org.jvnet.lafwidget.tabbed.DefaultTabPreviewPainter;
import org.jvnet.substance.SubstanceLookAndFeel;

public class TabInsetProblem {
	public static void main(String... args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				createAndShowGUI();
			}
		});
	}

	protected static void createAndShowGUI() {
		UIManager.put(SubstanceLookAndFeel.SHOW_EXTRA_WIDGETS, Boolean.TRUE);
		UIManager.put(LafWidget.TABBED_PANE_PREVIEW_PAINTER,
				new DefaultTabPreviewPainter());
		try {
			UIManager
					.setLookAndFeel("org.jvnet.substance.skin.SubstanceBusinessBlackSteelLookAndFeel");
			JFrame.setDefaultLookAndFeelDecorated(true);
			JDialog.setDefaultLookAndFeelDecorated(true);
			System.out.println("Using substance");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		// option 1: with this it works fine
		// UIManager.put("TabbedPane.tabAreaInsets", new Insets(30, 0, 0, 0));
		JFrame frame = new JFrame("test");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(createtabs());
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	private static JTabbedPane createtabs() {
		JTabbedPane tabs = new JTabbedPane();
		tabs.add("tab 1", new JPanel());
		tabs.setPreferredSize(new Dimension(300, 100));
		// option 2 it need the propagateChange()
		LafWidgetRepository.getRepository().getLafSupport().setTabAreaInsets(
				tabs, new Insets(12, 20, 0, 2));
		return tabs;
	}
}