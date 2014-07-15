package com.antibuzz.noisy.ui;

import org.fife.ui.rsyntaxtextarea.FileLocation;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.TextEditorPane;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class TextEditorDemo extends JFrame {

    TextEditorPane textArea;

    public TextEditorDemo() throws IOException {

        JPanel cp = new JPanel(new BorderLayout());

        textArea = new TextEditorPane(TextEditorPane.INSERT_MODE, false);

//        textArea.setBackground(new Color(49,51,53));

        textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_XML);
        textArea.setCodeFoldingEnabled(true);
        RTextScrollPane sp = new RTextScrollPane(textArea);

        cp.add(sp);

        setContentPane(cp);
        setTitle("Text Editor Demo");
        setDefaultCloseOperation(3);
        pack();
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        UI.dispatch(() -> {
            Theme theme = Theme.load(new FileInputStream("files/noisy.theme.xml"));
            final TextEditorDemo demo = new TextEditorDemo();
            demo.setVisible(true);
            FileLocation file = FileLocation.create(new File("files/noisy.theme.xml"));
            demo.textArea.load(file, "UTF-8");
            theme.apply(demo.textArea);
            UI.schedule(1000L, 1000L, () -> {
                TextEditorPane editor = demo.textArea;
                if (editor.isModifiedOutsideEditor()) {
                    UI.dispatch(editor::reload);
                }
            });
        });
    }


}