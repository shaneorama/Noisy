package com.antibuzz.noisy.ui

import org.apache.batik.util.gui.resource.JToolbarButton
import org.fife.ui.rsyntaxtextarea.FileLocation
import org.fife.ui.rsyntaxtextarea.SyntaxConstants
import org.fife.ui.rsyntaxtextarea.TextEditorPane
import org.fife.ui.rsyntaxtextarea.Theme
import org.fife.ui.rtextarea.RTextScrollPane

import javax.swing.*
import java.awt.*

/**
 * Created by shaner on 7/14/2014.
 */
class MainFrame {

    static TextEditorPane textArea

    static void main(args) {
        UI.dispatch {
            Appearance.setTheme("Raven Graphite")
            JFrame frame = new JFrame()

            JPanel cp = new JPanel(new BorderLayout())
            textArea = new TextEditorPane(TextEditorPane.INSERT_MODE, false)
            textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_XML)
            textArea.setCodeFoldingEnabled(true)
            RTextScrollPane sp = new RTextScrollPane(textArea)
            cp.add(sp)

            JToolBar toolbar = new JToolBar()
            toolbar.add(new JToolbarButton("Push Me"))
            cp.add(toolbar, BorderLayout.NORTH)

            frame.setContentPane(cp)
            frame.setDefaultCloseOperation(3)
            frame.setSize(800,700)
            frame.setTitle("Text Editor Demo")
            frame.setDefaultCloseOperation(3)
            frame.setLocationRelativeTo(null)
            frame.setVisible(true)
            frame.setAlwaysOnTop(true)

            Theme theme = Theme.load(new FileInputStream("files/noisy.theme.xml"))
            FileLocation file = FileLocation.create(new File("files/noisy.theme.xml"))
            textArea.load(file, "UTF-8")
            theme.apply(textArea)

            UI.schedule(1000L,1000L){
                if(textArea.isDirty()){
                    textArea.save()
                    theme = Theme.load(new FileInputStream("files/noisy.theme.xml"))
                    theme.apply(textArea)
                }
            }
        }
    }

}
