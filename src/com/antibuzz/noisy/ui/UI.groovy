package com.antibuzz.noisy.ui

import javax.swing.SwingUtilities

/**
 * Created by shane_000 on 7/14/2014.
 */
class UI {
    static void dispatch(EventDispatchTask task) {
        SwingUtilities.invokeLater {
            try{
                task.run()
            } catch(Throwable t) {
                t.printStackTrace()
            }
        }
    }

    static void schedule(long delay, long period, EventDispatchTask task){
        Timer timer = new Timer()
        timer.schedule(new TimerTask(){
            @Override
            public void run() {
                try {
                    task.run()
                } catch(Throwable t){
                    t.printStackTrace()
                }
            }
        },delay, period)

    }

    @FunctionalInterface
    interface EventDispatchTask {
        public void run() throws Throwable
    }
}
