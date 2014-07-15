package test.contrib;

import java.awt.Color;
import java.awt.Component;

import javax.swing.*;

import org.jvnet.substance.skin.SubstanceBusinessLookAndFeel;

public class ListPanelRenderer extends JFrame {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					UIManager
							.setLookAndFeel(new SubstanceBusinessLookAndFeel());
				} catch (UnsupportedLookAndFeelException e) {
					e.printStackTrace();
				}

				JList oList = new JList(new Object[] { "hi1", "hi2" });
				oList.setCellRenderer(new JPanelRenderer());

				JFrame oFrame = new ListPanelRenderer();
				oFrame.getContentPane().add(oList);
				oFrame.pack();
				oFrame.setVisible(true);
				oFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			}
		});
	}
}

class JPanelRenderer extends JPanel implements ListCellRenderer {
	@Override
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		setOpaque(true);

		removeAll();
		add(new JLabel(value.toString()));

		setBackground(Color.RED);
		return this;
	}
}
