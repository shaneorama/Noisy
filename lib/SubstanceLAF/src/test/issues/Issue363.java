package test.issues;
import java.awt.Dimension;

import javax.swing.*;

import org.jvnet.lafwidget.animation.FadeConfigurationManager;
import org.jvnet.lafwidget.animation.FadeKind;


public class Issue363 extends javax.swing.JDialog {

    public Issue363(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        // empty dialog
        final JDialog dialog = new JDialog(this, true);
        dialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        dialog.setMinimumSize(new Dimension(600, 600));
        dialog.setPreferredSize(new Dimension(600, 600));
        dialog.pack();
       
        JButton btShow = new JButton();
        this.add(btShow);
        btShow.setText("Show an empty dialog...");
        btShow.setSelected(true);
        btShow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dialog.setVisible(true);
            }
        });

        this.pack();
    }
    
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                   
UIManager.setLookAndFeel("org.jvnet.substance.skin.SubstanceRavenGraphiteGlassLookAndFeel");
                    // animations
                    FadeConfigurationManager.getInstance()
                            .allowFades(FadeKind.FOCUS_LOOP_ANIMATION);
                    FadeConfigurationManager.getInstance()
                            .allowFades(FadeKind.ICON_GLOW);
                    FadeConfigurationManager.getInstance()
                            .allowFades(FadeKind.GHOSTING_ICON_ROLLOVER);
                    // comment out following line to "fix" bug #363
                    FadeConfigurationManager.getInstance()
                            .allowFades(FadeKind.GHOSTING_BUTTON_PRESS);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                
                Issue363 dialog = new Issue363(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }
}
