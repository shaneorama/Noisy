package test.contrib;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.jvnet.substance.skin.SubstanceRavenGraphiteLookAndFeel;

public class BigFontTextArea extends JPanel {

    public BigFontTextArea() {
        setLayout(new BorderLayout());

        String string = "";
        for (int i = 0; i < 20; i++) {
            string += "line " + i;
            if (i < 19) {
                string += "\n";
            }
        }

        JTextArea textArea = new JTextArea(string);
        textArea.setFont(textArea.getFont().deriveFont(60f));
        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane, BorderLayout.CENTER);

        setPreferredSize(new Dimension(400, 340));
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    UIManager.setLookAndFeel(new SubstanceRavenGraphiteLookAndFeel());
                } catch (UnsupportedLookAndFeelException e) {
                    e.printStackTrace();
                }

                JFrame frame = new JFrame();
                BigFontTextArea contentPane = new BigFontTextArea();
                frame.setContentPane(contentPane);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }

}
