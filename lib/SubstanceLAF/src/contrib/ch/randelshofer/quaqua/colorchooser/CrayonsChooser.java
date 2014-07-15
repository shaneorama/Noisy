/*
 * @(#)CrayonsChooser.java  1.1  2006-04-23
 *
 * Copyright (c) 2005-2006 Werner Randelshofer
 * Staldenmattweg 2, Immensee, CH-6405, Switzerland.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Werner Randelshofer. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Werner Randelshofer.
 */

package contrib.ch.randelshofer.quaqua.colorchooser;


import java.awt.*;
//import java.awt.event.*;
import java.beans.*;
import javax.swing.*;
import javax.swing.colorchooser.*;
import javax.swing.event.*;
import javax.swing.plaf.*;

import contrib.ch.randelshofer.quaqua.util.*;

/**
 * A color chooser which provides a choice of Crayons.
 *
 * @author  Werner Randelshofer
 * @version 1.2 2006-04-23 Retrieve labels from UIManager. 
 * <br>1.0.2 2005-11-07 Get "labels" resource bundle from UIManager.
 * <br>1.0.1 2005-09-11 Get icon from UIManager.
 * <br>1.0 August 28, 2005 Created.
 */
public class CrayonsChooser extends AbstractColorChooserPanel implements UIResource {
    private Crayons crayons;
    
    /**
     * Creates a new instance.
     */
    public CrayonsChooser() {
        initComponents();

        crayons = new Crayons();
        add(crayons);
        crayons.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals("Color")) {
                    setColorToModel(crayons.getColor());
                }
            }
        });
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents

    }//GEN-END:initComponents
    
    protected void buildChooser() {
    }
    
    public String getDisplayName() {
        return UIManager.getString("ColorChooser.crayons");
    }    
    
    public javax.swing.Icon getLargeDisplayIcon() {
        return UIManager.getIcon("ColorChooser.crayonsIcon");
    }
    
    public Icon getSmallDisplayIcon() {
        return getLargeDisplayIcon();
    }
    
    public void updateChooser() {
        crayons.setColor(getColorFromModel());
    }
    public void setColorToModel(Color color) {
        getColorSelectionModel().setSelectedColor(color);
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    
}
