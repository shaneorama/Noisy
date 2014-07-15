package test;

import java.awt.GridLayout;

import javax.swing.*;

import org.jvnet.lafwidget.LafWidget;
import org.jvnet.lafwidget.utils.LafConstants.AnimationKind;
import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.painter.decoration.DecorationAreaType;
import org.jvnet.substance.skin.*;

public class DecorationAreas extends JFrame {
	public DecorationAreas() {
		super("Some simple text");

		this.setLayout(new GridLayout(2, 2));

		JList list1 = new JList(new Object[] { "entry1", "entry2", "entry3",
				"entry4" });
		SubstanceLookAndFeel
				.setDecorationType(list1, DecorationAreaType.HEADER);
		JList list2 = new JList(new Object[] { "entry1", "entry2", "entry3",
				"entry4" });
		SubstanceLookAndFeel.setDecorationType(list2, DecorationAreaType.NONE);
		JList list3 = new JList(new Object[] { "entry1", "entry2", "entry3",
				"entry4" });
		SubstanceLookAndFeel.setDecorationType(list3,
				DecorationAreaType.GENERAL);
		JList list4 = new JList(new Object[] { "entry1", "entry2", "entry3",
				"entry4" });
		SubstanceLookAndFeel
				.setDecorationType(list4, DecorationAreaType.FOOTER);
		this.add(list1);
		this.add(list2);
		this.add(list3);
		this.add(list4);

		this.setSize(650, 400);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public static void main(String[] args) {
		JFrame.setDefaultLookAndFeelDecorated(true);
		UIManager.put(LafWidget.ANIMATION_KIND, AnimationKind.DEBUG_FAST);
		SubstanceLookAndFeel.setSkin(new BusinessBlackSteelSkin());
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new DecorationAreas().setVisible(true);
			}
		});
	}
}
