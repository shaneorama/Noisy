package com.antibuzz.noisy.ui;

import info.monitorenter.gui.chart.Chart2D;
import info.monitorenter.gui.chart.ITrace2D;
import info.monitorenter.gui.chart.traces.Trace2DSorted;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Random;

/**
 * Demonstrates minimal effort to create a dynamic chart.
 *
 * @author Achim Westermann
 *
 */

public class MinimalDynamicChart {

    public static void main(String[]args){
        // Create a chart:
        Chart2D chart = new Chart2D();
        chart.setBackground(Color.BLACK);
        // Create an ITrace:
        // Note that dynamic charts need limited amount of values!!!
        ITrace2D trace = new Trace2DSorted();
        trace.setColor(new Color(0,200,200));

        // Add the trace to the chart. This has to be done before adding points (deadlock prevention):
        chart.addTrace(trace);

        // Make it visible:
        // Create a frame.
        JFrame frame = new JFrame("MinimalDynamicChart");
        // add the chart to the frame:
        frame.getContentPane().add(chart);
        frame.setSize(400,300);
        // Enable the termination button [cross on the upper right edge]:
        frame.addWindowListener(
                new WindowAdapter(){
                    public void windowClosing(WindowEvent e){
                        System.exit(0);
                    }
                }
        );
        frame.setVisible(true);

    /*
     * Now the dynamic adding of points. This is just a demo!
     *
     * Use a separate thread to simulate dynamic adding of date.
     * Note that you do not have to copy this code. Dynamic charting is just about
     * adding points to traces at runtime from another thread. Whenever you hook on
     * to a serial port or some other data source with a polling Thread (or an event
     * notification pattern) you will have your own thread that just has to add points
     * to a trace.
     */
        final Random rand = new Random();
        final int min = -100, max = 100;

        Thread t = new Thread(()->{
            for(int x = min; ; x++) {
                trace.addPoint(x, rand.nextDouble());
                x = x % (max - min) + min;
                try {
                    Thread.sleep(5);
                } catch (InterruptedException oops) {
                }
            }
        });

        t.start();


    }

    /** Defcon. */
    private MinimalDynamicChart() {
        // nop
    }
}
