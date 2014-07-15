/*
 * NewJDialog.java
 *
 * Created on November 16, 2008, 11:23 AM
 */
package test.issues;

import java.awt.EventQueue;
import java.util.EventListener;
import java.util.EventObject;
import javax.swing.UIManager;


public class Issue409 extends javax.swing.JDialog {

    FIActionClass FIA;

    /** Creates new form NewJDialog */
    public Issue409(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();


        FIA = new FIActionClass();
        // FIEventListener  FIE   = new FIEventListener();
        FIA.addFIEventListener(new FIEventListener() {

            @Override
            public void fiEventOccurred(FIEvent evt) {
                System.out.println("Fire event:" + evt.Action + "," + evt.Description+","+EventQueue.isDispatchThread());
                javax.swing.JTable JT = new javax.swing.JTable();
                JT.setModel(new javax.swing.table.DefaultTableModel(
                        new Object[][]{
                            {null, null, null, null},
                            {null, null, null, null},
                            {null, null, null, null},
                            {null, null, null, null}
                        },
                        new String[]{
                            "Title 1", "Title 2", "Title 3", "Title 4"
                        }));
                jScrollPane1.setViewportView(JT);
                mainPanel.validate();
            }
        });
        new Thread() {

            @Override
            public void run() {
            
                FIA._fireDialogEvent("ShowTable", 1);
            }
        }.start();

    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        mainPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        mainPanel.setLayout(new java.awt.BorderLayout());
        mainPanel.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 394, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 288, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                 try {
                        UIManager.setLookAndFeel(
                                "org.jvnet.substance.skin.SubstanceBusinessLookAndFeel");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Issue409 dialog = new Issue409(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel mainPanel;
    // End of variables declaration//GEN-END:variables
    private void _fireDialogEvent(String Description, int Action) {
        FIEvent event = new FIEvent(this, Description, Action);
        FIA.fireFIEvent(event);
    }

    public class FIEvent extends EventObject {

        public String Description;
        public int Action;

        public FIEvent(Object source, String Description, int Action) {
            super(source);
            this.Description = Description;
            this.Action = Action;
        }
    }

    public interface FIEventListener extends EventListener {

        public void fiEventOccurred(FIEvent evt);
    }

    public class FIActionClass {
        protected javax.swing.event.EventListenerList listenerList =
                new javax.swing.event.EventListenerList();
        public void addFIEventListener(FIEventListener listener) {
            listenerList.add(FIEventListener.class, listener);
        }
        public void removeFIEventListener(FIEventListener listener) {
            listenerList.remove(FIEventListener.class, listener);
        }
        void fireFIEvent(FIEvent evt) {
            Object[] listeners = listenerList.getListenerList();
            for (int i = 0; i < listeners.length; i += 2) {
                if (listeners[i] == FIEventListener.class) {
                    ((FIEventListener) listeners[i + 1]).fiEventOccurred(evt);
                }
            }
        }

        public void _fireDialogEvent(String Description, int Action) {
            FIEvent event = new FIEvent(this, Description, Action);
            fireFIEvent(event);
        }
    }
}