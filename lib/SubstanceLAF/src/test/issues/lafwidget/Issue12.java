package test.issues.lafwidget;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import org.jvnet.lafwidget.LafWidget;
import org.jvnet.lafwidget.tabbed.DefaultTabPreviewPainter;
import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.skin.BusinessBlackSteelSkin;

public class Issue12 extends JFrame {
	public Issue12() {
		super("Issue12");

		final JTabbedPane tabs = new JTabbedPane();
		tabs.putClientProperty(LafWidget.TABBED_PANE_PREVIEW_PAINTER,
				new DefaultTabPreviewPainter());
		tabs.addTab("tab", new JPanel());

		this.add(tabs, BorderLayout.CENTER);

		JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton remove = new JButton("remove");
		remove.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						while (tabs.getTabCount() > 0)
							tabs.removeTabAt(0);
					}
				});
			}
		});
		controls.add(remove);

		JButton add = new JButton("add");
		add.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						tabs.addTab("tab", new JPanel());
					}
				});
			}
		});
		controls.add(add);

		this.add(controls, BorderLayout.SOUTH);

		this.setSize(400, 300);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}

	public static void main(String[] args) {
		JFrame.setDefaultLookAndFeelDecorated(true);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				SubstanceLookAndFeel.setSkin(new BusinessBlackSteelSkin());
				UIManager.put(SubstanceLookAndFeel.SHOW_EXTRA_WIDGETS,
						Boolean.TRUE);
				new Issue12().setVisible(true);
			}
		});
	}

}
