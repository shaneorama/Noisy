package test.issues;
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

public class Issue416 extends JPanel {

    public Issue416() {
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
                Issue416 contentPane = new Issue416();
                frame.setContentPane(contentPane);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }

}
