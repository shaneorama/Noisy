package test.contrib;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.table.AbstractTableModel;

import org.jvnet.substance.skin.SubstanceMagmaLookAndFeel;
import org.jvnet.substance.skin.SubstanceMistAquaLookAndFeel;
import org.jvnet.substance.skin.SubstanceMistSilverLookAndFeel;
import org.jvnet.substance.skin.SubstanceModerateLookAndFeel;

/**
 * Every time you hit the "swap" button, it replaces the current contents of the
 * content pane with a new instance of SLeakTest. The former one is supposed to
 * get garbage collected. I have a LeakWatcher class that watches for previous
 * instances getting garbage collected and prints a message to System.err. If
 * the previous components are getting garbage collected, you will see a message
 * saying so each time you hit the "swap" button.
 * <p>
 * Here's what actually happens: For every LF except the Substance LFs, the
 * discarded components get garbage collected. For the Substance LFs, they
 * don't.
 */
public class SLeakTest extends JPanel {
  private static JFrame mFrame = new JFrame( "Leak Test" );
  private static int counter = 0;
  private static LeakWatcher<SLeakTest> watcher = new LeakWatcher<SLeakTest>(
      "SLeakTestQueue" );

  public static void main(String[] args) {
    Runnable doRun = new Runnable() {
      public void run() {
        createApplication();
      }
    };
    SwingUtilities.invokeLater( doRun );
  }

  private static void createApplication() {
    // Choose a L&F. The last four cause memory leaks.
    // LF.Platform.set();
    // LF.Ocean.set();
    // LF.Motif.set();
    // LF.Nimbus.set();
    // LF.WindowsClassic.set();
    // LF.Magma.set();
    // LF.MistAfqua.set();
    // LF.MistSilver.set();
    LF.Moderate.set();
    mFrame.setSize( 500, 300 );
    mFrame.setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE );
    addControlButton();
    swap();
    mFrame.setVisible( true );
  }

  private static void addControlButton() {
    JPanel cPanel = new JPanel( new FlowLayout() );
    JButton cButton = new JButton( "Swap" );
    ActionListener al = new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        swap();
      }
    };
    cButton.addActionListener( al );
    cPanel.add( cButton );
    mFrame.getContentPane().add( cPanel, BorderLayout.SOUTH );
  }

  private static void swap() {
    if ( mFrame.getContentPane().getComponentCount() > 1 )
      mFrame.getContentPane().remove( 1 );
    mFrame.getContentPane().add( new SLeakTest(), BorderLayout.CENTER );
    mFrame.getContentPane().validate();
    System.gc();
  }

  private SLeakTest() {
    super( new BorderLayout() );
    // The label doesn't cause a memory leak
    JLabel label = new JLabel( "Instance " + counter++ );
    add( label, BorderLayout.NORTH );

    // Almost any other components I add cause a memory leak.
    JTextArea ta = new JTextArea();
    JScrollPane scroll = new JScrollPane( ta,
        ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER );
    JSplitPane splitter = new JSplitPane();
    splitter.setTopComponent( scroll );
    splitter.setBottomComponent( new JTable( new LeakTableModel() ) );
    splitter.setDividerLocation( 0.5 );
    splitter.setResizeWeight( 0.5 );
    add( splitter, BorderLayout.CENTER );

    // // Replacing the Splitter above with this causes a leak
    // JScrollPane scroll = new JScrollPane( new JTable( new LeakTableModel() ),
    // ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
    // ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER );
    // add( scroll, BorderLayout.CENTER );

    // // But replace it with this and it no longer leaks.
    // add( new JButton( "Does Nothing" ), BorderLayout.CENTER );
    watcher.watch( this );
  }

  private class LeakTableModel extends AbstractTableModel {
    @Override
    public int getColumnCount() {
      return 10;
    }

    @Override
    public int getRowCount() {
      return 10;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
      return new Dimension( rowIndex, columnIndex ).hashCode();
    }
  }

  private static class LeakWatcher<T> {
    Set<WeakReference<T>> refs = new HashSet<WeakReference<T>>();
    ReferenceQueue<T> mQueue = new ReferenceQueue<T>();
    private final String mQueueName;

    public LeakWatcher(String queueName) {
      mQueueName = queueName;
      launchPoller( mQueue, queueName );
    }

    public void watch(T object) {
      WeakReference<T> ref = new WeakReference<T>( object, mQueue );
      // System.err.println(" <) " + ref + " on " + mQueueName );
      System.err.println( String.format( "   <) %-36s   on %s", ref.toString(),
          mQueueName ) );

      refs.add( ref );
    }

    private int getValidCount() {
      int count = 0;
      for ( WeakReference<?> ref : refs ) {
        if ( ref.get() != null ) {
          count++;
        }
      }
      return count;
    }

    private void launchPoller(final ReferenceQueue<T> queue, final String name) {
      System.err.println( "Launching " + queue );
      Runnable poller = new Runnable() {
        public void run() {
          System.err.println( "Looking at " + name + ": " + queue + "..." );
          // noinspection InfiniteLoopStatement
          while ( true ) {
            try {
              Reference<? extends T> ref = queue.remove(); // blocks until
              // available
              refs.remove( ref );
              // System.err.println( "GC of " + ref + " from " + name
              // + ". (Watching " + refs.size() + " refs, of which "
              // + getValidCount() + " are valid.)" );
              String fmt = "GC of %-36s from %s. (Watching %d refs, of which %d are valid.)";
              System.err.println( String.format( fmt, ref.toString(), name,
                  refs.size(), getValidCount() ) );
            } catch ( InterruptedException e ) {
              e.printStackTrace();
              break;
            }
          }
          System.err.println( "Queue done." );
        }
      };
      Thread pollThread = new Thread( poller, name );
      pollThread.setDaemon( true );
      pollThread.start();
    }
  }

  private static enum LF {
    Platform(UIManager.getSystemLookAndFeelClassName()),
    // Metal("javax.swing.plaf.metal.MetalLookAndFeel", "metalTheme"),
    Ocean("javax.swing.plaf.metal.MetalLookAndFeel"), // "oceanTheme"),
    WindowsClassic("com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel"),
    Motif("com.sun.java.swing.plaf.motif.MotifLookAndFeel"), // Motif
    Nimbus("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel"), // NImbus
    Magma(SubstanceMagmaLookAndFeel.class.getName()), // Substance LFs
    MistAqua(SubstanceMistAquaLookAndFeel.class.getName()), // Aqua
    MistSilver(SubstanceMistSilverLookAndFeel.class.getName()), // Silver
    Moderate(SubstanceModerateLookAndFeel.class.getName()), // Moderate
    ;

    private String path;

    LF(String name) {
      path = name;
    }

    public void set() {
      try {
        UIManager.setLookAndFeel( path );
      } catch ( Exception e ) {
        e.printStackTrace();
      }
    }
  }
}
